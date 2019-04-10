package com.hanlin.fadp.base.container;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.start.Config;
import com.hanlin.fadp.base.start.StartupException;
import com.hanlin.fadp.base.start.StartupLoader;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilValidate;

/**
 * @ClassName: ContainerLoader 
 * @Description: 组件容器加载器，加载组件容器，该类的实例由平台启动类实例化，
 * 客户代码则只是负责组件容器的关闭，不再实例化本类
 * @author: 祖国
 * @date: 2015年10月8日 上午10:04:03
 */
public class ContainerLoader implements StartupLoader {

    public static final String module = ContainerLoader.class.getName();

    /**值为Config中成员containerConfig的值，
     * 为framework/base/config/fadp-containers.xml   */
    private String configFile = null;
    /**已加载的容器集合list。从fadp-containers.xml、fadp-component.xml和
     * hot-deploy-containers.xml中选出的包含start包下面xxx.properties文
     * 件中fadp.start.loader1.loaders属性配置的
     * 值的container节点组成的list，放入list前完成加载   */
    private final List<Container> loadedContainers =new LinkedList<Container>();
    /**是否不加载，默认是false   */
    private boolean unloading = false;
    /** 已加载标识，默认false  */
    private boolean loaded = false;

    /**
     * @see com.hanlin.fadp.base.start.StartupLoader#load(Config, String[])
     */
    @Override
    public synchronized void load(Config config, String args[]) throws StartupException {
        if (this.loaded || this.unloading) {
            return;
        }
        this.loadedContainers.clear();
        // 值为framework/base/config/fadp-containers.xml
        this.configFile = config.containerConfig;

        //获取源码包start下的start.properties等文件中key=fadp.start.loader1.loaders的value
        List<String> loaders = null;
        for (Map loaderMap: config.loaders) {
            if (module.equals(loaderMap.get("class"))) {//如果加载类为本类，则取出main,rmi
                loaders = StringUtil.split((String)loaderMap.get("profiles"), ",");
            }
        }

        Debug.logInfo("[Startup] Loading containers from " + configFile + " for loaders " + loaders, module);
        Collection<ContainerConfig.Container> containers = null;
        try {//传入fadp-containers.xml配置文件获取关于container的配置数据list
            containers = ContainerConfig.getContainers(configFile);
        } catch (ContainerException e) {
            throw new StartupException(e);
        }
        for (ContainerConfig.Container containerCfg : containers) {
            if (this.unloading) {
                return;
            }
            boolean matchingLoaderFound = false;
            //如果fadp-container.xml中container元素的loaders为空，且start.properties中加载器为空，表明匹配发现
            if (UtilValidate.isEmpty(containerCfg.loaders) && UtilValidate.isEmpty(loaders)) {
                matchingLoaderFound = true;
            } else {
                for (String loader: loaders) {
                	/**
                	 * 遍历start.properties中加载器（值为rmi和main），如果fadp-container.xml中
                	 * container元素的loaders为空，或其包括start.properties中加载器配置元素，则退出本次循环.
                	 * 本处很重要，实际上是对fadp-containers.xml中配置的节点进行选择,以包含stat.properties
                	 * 中的加载器为依据
                	 */
                    if (UtilValidate.isEmpty(containerCfg.loaders) || containerCfg.loaders.contains(loader)) {
                        matchingLoaderFound = true;
                        break;
                    }
                }
            }
            if (matchingLoaderFound) {//上面else已经确保了本步的执行
                Debug.logInfo("Loading container: " + containerCfg.name, module);
                Container tmpContainer = loadContainer(containerCfg, args);
                this.loadedContainers.add(tmpContainer);
                Debug.logInfo("Loaded container: " + containerCfg.name, module);
            }
        }
        if (this.unloading) {
            return;
        }

        List<ContainerConfig.Container> containersDefinedInComponents = ComponentConfig.getAllContainers();
        for (ContainerConfig.Container containerCfg: containersDefinedInComponents) {
            boolean matchingLoaderFound = false;
            if (UtilValidate.isEmpty(containerCfg.loaders) && UtilValidate.isEmpty(loaders)) {
                matchingLoaderFound = true;
            } else {
                for (String loader: loaders) {
                    if (UtilValidate.isEmpty(containerCfg.loaders) || containerCfg.loaders.contains(loader)) {
                        matchingLoaderFound = true;
                        break;
                    }
                }
            }
            if (matchingLoaderFound) {
                Debug.logInfo("Loading component's container: " + containerCfg.name, module);
                Container tmpContainer = loadContainer(containerCfg, args);
                this.loadedContainers.add(tmpContainer);
                Debug.logInfo("Loaded component's container: " + containerCfg.name, module);
            }
        }
        // Get hot-deploy container configuration files
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = loader.getResources("hot-deploy-containers.xml");
            while (resources.hasMoreElements() && !this.unloading) {
                URL xmlUrl = resources.nextElement();
                Debug.logInfo("Loading hot-deploy containers from " + xmlUrl, module);
                Collection<ContainerConfig.Container> hotDeployContainers = ContainerConfig.getContainers(xmlUrl);
                for (ContainerConfig.Container containerCfg : hotDeployContainers) {
                    if (this.unloading) {
                        return;
                    }
                    Container tmpContainer = loadContainer(containerCfg, args);
                    this.loadedContainers.add(tmpContainer);
                }
            }
        } catch (Exception e) {
            Debug.logError(e, "Could not load hot-deploy-containers.xml", module);
            throw new StartupException(e);
        }
        loaded = true;
    }

    /**
     * @Title: loadContainer 
     * @Description: 根据ContainerConfig中解析fadp-containers.xml的信息进行相关组件容器加载和实例化
     * @param containerCfg
     * @param args
     * @throws StartupException
     * @return: Container
     */
    private Container loadContainer(ContainerConfig.Container containerCfg, String[] args) throws StartupException {
        // 获取线程上下文类加载器
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            Debug.logWarning("Unable to get context classloader; using system", module);
            loader = ClassLoader.getSystemClassLoader();
        }
        
        //加载ContainerConfig.Container类
        Class<?> containerClass = null;
        try {//加载容器，本容器是fadp-containers.xml中container节点的class属性
            containerClass = loader.loadClass(containerCfg.className);
        } catch (ClassNotFoundException e) {
            throw new StartupException("Cannot locate container class", e);
        }
        if (containerClass == null) {
            throw new StartupException("Component container class not loaded");
        }

        // 根据上面加载的Container类，创建一个Container实例，
        Container containerObj = null;
        try {
            containerObj = (Container) containerClass.newInstance();
        } catch (InstantiationException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        } catch (IllegalAccessException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        } catch (ClassCastException e) {
            throw new StartupException("Cannot create " + containerCfg.name, e);
        }

        if (containerObj == null) {
            throw new StartupException("Unable to create instance of component container");
        }

        // 初始化组件容器实例
        try {
            containerObj.init(args, containerCfg.name, configFile);
        } catch (ContainerException e) {
            throw new StartupException("Cannot init() " + containerCfg.name, e);
        } catch (java.lang.AbstractMethodError e) {
            throw new StartupException("Cannot init() " + containerCfg.name, e);
        }

        return containerObj;
    }

    private void printThreadDump() {
        Thread currentThread = Thread.currentThread();
        ThreadGroup group = currentThread.getThreadGroup();
        while (group.getParent() != null) {
            group = group.getParent();
        }
        Thread threadArr[] = new Thread[1000];
        group.enumerate(threadArr);

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("Thread dump:");
        for (Thread t: threadArr) {
            if (t != null) {
                ThreadGroup g = t.getThreadGroup();
                out.println("Thread: " + t.getName() + " [" + t.getId() + "] @ " + (g != null ? g.getName() : "[none]") + " : " + t.getPriority() + " [" + t.getState().name() + "]");
                out.println("--- Alive: " + t.isAlive() + " Daemon: " + t.isDaemon());
                for (StackTraceElement stack: t.getStackTrace()) {
                    out.println("### " + stack.toString());
                }
            }
        }
        Debug.logInfo(writer.toString(), module);
    }

    /**
     * @see com.hanlin.fadp.base.start.StartupLoader#start()
     */
    @Override
    public synchronized void start() throws StartupException {
        if (!this.loaded || this.unloading) {
            throw new IllegalStateException("start() called on unloaded containers");
        }
        Debug.logInfo("[Startup] Starting containers...", module);
        // start each container object
        for (Container container: this.loadedContainers) {
            if (this.unloading) {
                return;
            }
            Debug.logInfo("Starting container " + container.getName(), module);
            try {
                container.start();
            } catch (ContainerException e) {
                throw new StartupException("Cannot start() " + container.getClass().getName(), e);
            } catch (java.lang.AbstractMethodError e) {
                throw new StartupException("Cannot start() " + container.getClass().getName(), e);
            }
            Debug.logInfo("Started container " + container.getName(), module);
        }
    }

    /**
     * @see com.hanlin.fadp.base.start.StartupLoader#unload()
     */
    @Override
    public void unload() throws StartupException {
        if (!this.unloading) {
            this.unloading = true;
            synchronized (this) {
                Debug.logInfo("Shutting down containers", module);
                if (Debug.verboseOn()) {
                    printThreadDump();
                }
                // shutting down in reverse order
                for (int i = this.loadedContainers.size(); i > 0; i--) {
                    Container container = this.loadedContainers.get(i-1);
                    Debug.logInfo("Stopping container " + container.getName(), module);
                    try {
                        container.stop();
                    } catch (ContainerException e) {
                        Debug.logError(e, module);
                    }
                    Debug.logInfo("Stopped container " + container.getName(), module);
                }
            }
        }
    }
}
