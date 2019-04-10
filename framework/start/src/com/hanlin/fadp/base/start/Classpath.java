
package com.hanlin.fadp.base.start;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @ClassName: Classpath 
 * @Description: classpath资源收集器（不仅仅是类的路径收集，还包括类路径上的其他配置文件、资源），
 * 通过本来的addxxx方法将类路径、组件路径、文件、jar包加入到该收集器中，可以通过getxxx从收集器中进行获取
 * @author: turtle
 * @date: 2015年9月14日 下午1:15:14
 */
public class Classpath {

	/** .dll结尾的文件标识 */
    private static final String nativeLibExt = System.mapLibraryName("someLib").replace("someLib", "").toLowerCase();
    /**
     * 应用跟路径fadp、平台启动的base/config文件夹、base/dtd文件夹、base/lib、
     * base/lib/commons、base/build/lib下的jar和zip形式的组件文件的列表
     */
    private List<File> elements = new ArrayList<File>();
    /**
     * 动态链接库列表，在Java代码中声明的native方法的那个libraryload，或者load其他什么动态连接库的列表
     */
    private final List<File> nativeFolders = new ArrayList<File>();

    /**
     * Default constructor.
     */
    public Classpath() {
    }

    /**
     * @Title: addClassPath 
     * @Description: 将组件加入到classpath中，本方法主要解析传入的路径参数，将其分解，然后调用addComponent来加入组件
     * @param path 路径名
     * @return <code>true</code> if any path elements were added
     * @throws IOException if there was a problem parsing the class path
     * @throws IllegalArgumentException if <code>path</code> is null or empty
     */
    public boolean addClassPath(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path cannot be null or empty");
        }
        boolean added = false;
        StringTokenizer t = new StringTokenizer(path, File.pathSeparator);
        while (t.hasMoreTokens()) {
            added |= addComponent(t.nextToken());
        }
        return added;
    }

    /**
     * @Title: addComponent 
     * @Description: 将组件加入到classpath的组件列表中
     * @param component 为File，待加入classpath的组件
     * @return <code>true</code> if the component was added
     * @throws IOException if there was a problem parsing the component
     * @throws IllegalArgumentException if <code>component</code> is null
     */
    public boolean addComponent(File component) throws IOException {
        if (component == null) {
            throw new IllegalArgumentException("component cannot be null");
        }
        if (component.exists()) {
            File key = component.getCanonicalFile();
            synchronized (elements) {
                if (!elements.contains(key)) {
                    elements.add(key);
                    return true;
                }
            }
        } else {
            System.out.println("Warning : Module classpath component '" + component + "' is not valid and will be ignored...");
        }
        return false;
    }

    /**
     * @Title: addComponent 
     * @Description: 将组件加入到classpath的组件列表中
     * @param component 为string，待加入到classpath的组件名，需要调用同名方法，本方法只是解决组件名为空的异常
     * @return <code>true</code> if the component was added
     * @throws IOException if there was a problem parsing the component
     * @throws IllegalArgumentException if <code>component</code> is null or empty
     */
    public boolean addComponent(String component) throws IOException {
        if (component == null || component.isEmpty()) {
            throw new IllegalArgumentException("component cannot be null or empty");
        }
        return addComponent(new File(component));
    }

    /**
     * @Title: addFilesFromPath 
     * @Description: 将一个目录下面的所有以.jar和.zip结尾的文件加入到classpath实例的组件文件列表中，native相关的加入到
     * classpath的native库列表中
     * @param path the directory to scan
     * @throws IOException if there was a problem processing the directory
     * @throws IllegalArgumentException if <code>path</code> is null
     */
    public void addFilesFromPath(File path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        if (path.isDirectory() && path.exists()) {//只考虑path为路径的情况
            // load all .jar, .zip files and native libs in this directory
            boolean containsNativeLibs = false;
            for (File file : path.listFiles()) {
                String fileName = file.getName().toLowerCase();
                //.jar, .zip结尾
                if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                    File key = file.getCanonicalFile();
                    synchronized (elements) {
                        if (!elements.contains(key)) {
                            elements.add(key);
                        }
                    }
                } else if (fileName.endsWith(nativeLibExt)) {//.dll结尾
                    containsNativeLibs = true;
                }
            }
            //Java代码中声明的native方法的libraryload，或者load其他什么动态连接库
            if (containsNativeLibs) {
                File key = path.getCanonicalFile();
                synchronized (nativeFolders) {
                    if (!nativeFolders.contains(key)) {
                        nativeFolders.add(key);
                    }
                }
            }
        } else {
            System.out.println("Warning : Module classpath component '" + path + "' is not valid and will be ignored...");
        }
    }

    /**
     * @Title: addNativeClassPath 
     * @Description: 将native库所在路径加入到classpath的native库列表中
     * @param path
     * @return 
     * @throws IOException
     * @throws IllegalArgumentException if <code>path</code> is null
     */
    public boolean addNativeClassPath(File path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        if (path.exists()) {
            File key = path.getCanonicalFile();
            synchronized (nativeFolders) {
                if (!nativeFolders.contains(key)) {
                    nativeFolders.add(key);
                    return true;
                }
            }
        } else {
            System.out.println("Warning : Module classpath component '" + path + "' is not valid and will be ignored...");
        }
        return false;
    }

    /**
     * @Title: appendPath 
     * @Description: 将字符串添加""后放入到StringBuilder中
     * @param cp
     * @param path
     * @return: void
     */
    private void appendPath(StringBuilder cp, String path) {
        if (path.indexOf(' ') >= 0) {
            cp.append('\"');
            cp.append(path);
            cp.append('"');
        } else {
            cp.append(path);
        }
    }

    /**
     * @Title: getNativeFolders 
     * @Description: 将classpath中native库中的内容转化为ArrayList
     * @return: List<File>
     */
    public List<File> getNativeFolders() {
        synchronized (nativeFolders) {
            return new ArrayList<File>(nativeFolders);
        }
    }

    /**
     * @Title: getUrls 
     * @Description: 将classpath实例中组件列表组装为URL数组
     * @throws MalformedURLException
     * @return: URL[]
     */
    public URL[] getUrls() throws MalformedURLException {
        synchronized (elements) {
            int cnt = elements.size();
            URL[] urls = new URL[cnt];
            for (int i = 0; i < cnt; i++) {
                urls[i] = elements.get(i).toURI().toURL();
            }
            return urls;
        }
    }

    @Override
    public String toString() {//将组件列表中的组件名通过";"进行分割后输出
        StringBuilder cp = new StringBuilder(1024);
        synchronized (elements) {
            int cnt = elements.size();
            if (cnt >= 1) {
                cp.append(elements.get(0).getPath());
            }
            for (int i = 1; i < cnt; i++) {
                cp.append(File.pathSeparatorChar);
                appendPath(cp, elements.get(i).getPath());
            }
        }
        return cp.toString();
    }
    
    //功能测试
    public static void main(String[] args){
 /*   	String fadpHomeTmp=System.getProperty("user.dir");
    	System.out.println(fadpHomeTmp);
    	fadpHomeTmp=fadpHomeTmp.replace("\\", "/");
    	System.out.println(fadpHomeTmp);
    	String fadpHome=fadpHomeTmp;
    	System.out.println(fadpHome);
    	Classpath classPath=new Classpath();
    	try {
			classPath.addComponent(fadpHome);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	for(int i=0;i<classPath.elements.size();i++){
    		System.out.println(classPath.elements.get(i));
    	}*/
    	
    	//测试toURI和toURL方法
    	File file=new File("C:Documents and Settingstest.xsl");
    	URI uri=file.toURI();
    	System.out.println(uri);
    	try {
			URL url=file.toURL();
			System.out.println(url);
			System.out.println(uri.toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}
