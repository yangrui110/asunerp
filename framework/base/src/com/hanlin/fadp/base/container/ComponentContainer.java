package com.hanlin.fadp.base.container;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.component.ComponentException;
import com.hanlin.fadp.base.component.ComponentLoaderConfig;
import com.hanlin.fadp.base.start.Classpath;
import com.hanlin.fadp.base.start.Config;
import com.hanlin.fadp.base.start.NativeLibClassLoader;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilValidate;

/**
 * @ClassName: ComponentContainer 
 * @Description: 组件容器，负责启动时组件加载的组件容器，其配置在fadp-containers.xml文件中，通过ContainerLoader读取
 * ContainerConfig类中关于fadp-containers.xml的配置信息，经过反射来加载并初始化，如：
 * <container name="component-container" class="com.hanlin.fadp.base.component.ComponentContainer"/>
 * @author: 祖国
 * @date: 2015年9月22日 上午7:17:56
 */
public class ComponentContainer implements Container {

    public static final String module = ComponentContainer.class.getName();

    /**容器配置文件url，为fadp-containers.xml文件  */
    protected String configFileLocation = null;
    /**为fadp-containers.xml文件中container节点的name属性  */
    private String name;
    /**是否已经加载，默认未加载,该值用于并发环境，可根据设置的值自动更改来同步  */
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * 初始化了组件容器的容器名name和
     */
    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
    	//将loaded的值设置为true，因为初始化时为false，与预期值相同，所以就设置为更改的值true
    	if (!loaded.compareAndSet(false, true)) {
            throw new ContainerException("Components already loaded, cannot start");
        }
        this.name = name;
        this.configFileLocation = configFile;
        // 从配置文件中获取name等于给定值的container节点的实例容器
        ContainerConfig.Container cc = ContainerConfig.getContainer(name, configFileLocation);
        // 获取container节点的loader-config子节点的value属性，其为一个class或boolean值
        String loaderConfig = null;
        if (cc.getProperty("loader-config") != null) {
            loaderConfig = cc.getProperty("loader-config").value;
        }
        /**
         * 加载组件，一般在fadp-containers.xml中很少配置loader-config子元素，
         * 因此输入的参数多为null
         */
        try {
            loadComponents(loaderConfig);
        } catch (ComponentException e) {
            throw new ContainerException(e);
        }
    }

    /**
     * @see com.hanlin.fadp.base.container.Container#start()
     */
    public boolean start() throws ContainerException {
        return loaded.get();
    }

    /**
     * @Title: loadComponents 
     * @Description: 所有组件的加载，
     * @param loaderConfig
     * @throws ComponentException
     * @return: void
     */
    public void loadComponents(String loaderConfig) throws ComponentException {
        // 获取要加载的组件，将会解析base/config目录下面的和各个一级应用下面的
    	//component-load.xml文件中关于load-component和load-components配置
        List<ComponentLoaderConfig.ComponentDef> components = 
        		ComponentLoaderConfig.getRootComponents(loaderConfig);
        String parentPath;
        try {//获取应用部署的根路径
            parentPath = FileUtil.getFile(System.getProperty("fadp.home")).
            		getCanonicalFile().toString().replaceAll("\\\\", "/");
            // 加载每一个组件
            if (components != null) {
                for (ComponentLoaderConfig.ComponentDef def: components) {
                    loadComponentFromConfig(parentPath, def);
                }
            }
        } catch (MalformedURLException e) {
            throw new ComponentException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ComponentException(e.getMessage(), e);
        }
        Debug.logInfo("All components loaded", module);
    }

    /**
     * @Title: loadComponentFromConfig 
     * @Description: TODO
     * @param parentPath
     * @param def
     * @throws IOException
     * @return: void
     */
    private void loadComponentFromConfig(String parentPath, ComponentLoaderConfig.ComponentDef def) throws IOException {
        String location;
        if (def.location.startsWith("/")) {
            location = def.location;
        } else {
            location = parentPath + "/" + def.location;
        }
        if (def.type == ComponentLoaderConfig.SINGLE_COMPONENT) {
            ComponentConfig config = null;
            try {
                config = ComponentConfig.getComponentConfig(def.name, location);
                if (UtilValidate.isEmpty(def.name)) {
                    def.name = config.getGlobalName();
                }
            } catch (ComponentException e) {
                Debug.logError("Cannot load component : " + def.name + " @ " + def.location + " : " + e.getMessage(), module);
            }
            if (config == null) {
                Debug.logError("Cannot load component : " + def.name + " @ " + def.location, module);
            } else {
                this.loadComponent(config);
            }
        } else if (def.type == ComponentLoaderConfig.COMPONENT_DIRECTORY) {
            this.loadComponentDirectory(location);
        }
    }

    private void loadComponentDirectory(String directoryName) throws IOException {
        Debug.logInfo("Auto-Loading component directory : [" + directoryName + "]", module);
        File parentPath = FileUtil.getFile(directoryName);
        if (!parentPath.exists() || !parentPath.isDirectory()) {
            Debug.logError("Auto-Load Component directory not found : " + directoryName, module);
        } else {
            File componentLoadConfig = new File(parentPath, ComponentLoaderConfig.COMPONENT_LOAD_XML_FILENAME);
            if (componentLoadConfig != null && componentLoadConfig.exists()) {
                URL configUrl = null;
                try {
                    configUrl = componentLoadConfig.toURI().toURL();
                    List<ComponentLoaderConfig.ComponentDef> componentsToLoad = ComponentLoaderConfig.getComponentsFromConfig(configUrl);
                    if (componentsToLoad != null) {
                        for (ComponentLoaderConfig.ComponentDef def: componentsToLoad) {
                            this.loadComponentFromConfig(parentPath.toString(), def);
                        }
                    }
                } catch (MalformedURLException e) {
                    Debug.logError(e, "Unable to locate URL for component loading file: " + componentLoadConfig.getAbsolutePath(), module);
                } catch (ComponentException e) {
                    Debug.logError(e, "Unable to load components from URL: " + configUrl.toExternalForm(), module);
                }
            } else {
                String[] fileNames = parentPath.list();
                Arrays.sort(fileNames);
                for (String sub: fileNames) {
                    try {
                        File componentPath = FileUtil.getFile(parentPath.getCanonicalPath() + File.separator + sub);
                        if (componentPath.isDirectory() && !sub.equals("CVS") && !sub.equals(".svn")) {
                            // make sure we have a component configuration file
                            String componentLocation = componentPath.getCanonicalPath();
                            File configFile = FileUtil.getFile(componentLocation.concat(File.separator).concat(ComponentConfig.FADP_COMPONENT_XML_FILENAME));
                            if (configFile.exists()) {
                                ComponentConfig config = null;
                                try {
                                    // pass null for the name, will default to the internal component name
                                    config = ComponentConfig.getComponentConfig(null, componentLocation);
                                } catch (ComponentException e) {
                                    Debug.logError(e, "Cannot load component : " + componentPath.getName() + " @ " + componentLocation + " : " + e.getMessage(), module);
                                }
                                if (config == null) {
                                    Debug.logError("Cannot load component : " + componentPath.getName() + " @ " + componentLocation, module);
                                } else {
                                    loadComponent(config);
                                }
                            }
                        }
                    } catch (IOException ioe) {
                        Debug.logError(ioe, module);
                    }
                }
            }
        }
    }

    private void loadComponent(ComponentConfig config) throws IOException {
        // make sure the component is enabled
        if (!config.enabled()) {
            Debug.logInfo("Not loading component [" + config.getComponentName() + "] because it is disabled", module);
            return;
        }
        List<ComponentConfig.ClasspathInfo> classpathInfos = config.getClasspathInfos();
        String configRoot = config.getRootLocation();
        configRoot = configRoot.replace('\\', '/');
        // set the root to have a trailing slash
        if (!configRoot.endsWith("/")) {
            configRoot = configRoot + "/";
        }
        if (classpathInfos != null) {
            Classpath classPath = new Classpath();
            // TODO: If any components change the class loader, then this will need to be changed.
            NativeLibClassLoader classloader = (NativeLibClassLoader) Thread.currentThread().getContextClassLoader();
            for (ComponentConfig.ClasspathInfo cp: classpathInfos) {
                String location = cp.location.replace('\\', '/');
                // set the location to not have a leading slash
                if (location.startsWith("/")) {
                    location = location.substring(1);
                }
                if (!"jar".equals(cp.type) && !"dir".equals(cp.type)) {
                    Debug.logError("Classpath type '" + cp.type + "' is not supported; '" + location + "' not loaded", module);
                    continue;
                }
                String dirLoc = location;
                if (dirLoc.endsWith("/*")) {
                    // strip off the slash splat
                    dirLoc = location.substring(0, location.length() - 2);
                }
                File path = FileUtil.getFile(configRoot + dirLoc);
                if (path.exists()) {
                    if (path.isDirectory()) {
                        if ("dir".equals(cp.type)) {
                            classPath.addComponent(configRoot + location);
                        }
                        classPath.addFilesFromPath(path);
                    } else {
                        // add a single file
                        classPath.addComponent(configRoot + location);
                    }
                } else {
                    Debug.logWarning("Location '" + configRoot + dirLoc + "' does not exist", module);
                }
            }
            for (URL url : classPath.getUrls()) {
                classloader.addURL(url);
            }
            for (File folder : classPath.getNativeFolders()) {
                classloader.addNativeClassPath(folder);
            }
        }
         Debug.logInfo("Loaded component : [" + config.getComponentName() + "]", module);
    }

    /**
     * @see com.hanlin.fadp.base.container.Container#stop()
     */
    public void stop() throws ContainerException {
    }

    public String getName() {
        return name;
    }
    
    public static void main(String[] args){

    }

}
