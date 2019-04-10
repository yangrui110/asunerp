
package com.hanlin.fadp.base.start;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName: NativeLibClassLoader 
 * @Description: 根据指定的url加载资源，主要是为了解决parent delegation机制下无法干净的解决的问题。假如有下述委派链：
 * ClassLoader A -> System class loader -> Extension class loader -> Bootstrap class loader
 * 那么委派链左边的ClassLoader就可以很自然的使用右边的ClassLoader所加载的类。但如果情况要反过来，是右边的ClassLoader所
 * 加载的代码需要反过来去找委派链靠左边的ClassLoader去加载东西怎么办呢？没辙，parent delegation是单向的，没办法反过来从右边找左边。
 * 这种情况下就可以把某个位于委派链左边的ClassLoader设置为线程的context class loader，这样就给机会让代码
 * 不受parent delegation的委派方向的限制而加载到类了。
 * 因为bootstrap ClassLoader缓存了本地库（或JDK核心库）路径，任何对本地库的改变均被忽略，但组件加载过程中确实会出现改
 * 变本地库的情况，因此，有了这个线程上下文类加载器，就可以解决这一问题了
 * @author: turtle
 * @date: 2015年9月14日 下午1:20:58
 */
public class NativeLibClassLoader extends URLClassLoader {

	/**
	 * 自定义的类加载器的库路径，CopyOnWriteArrayList是ArrayList 的一个线程安全的变体，其中所有可变操作（add、set等等）都是
	 * 通过对底层数组，进行一次新的复制来实现的。   这一般需要很大的开销，但是当遍历操作的数量大大超过可变操作的数量时，
	 * 这种方法可能比其他替代方法更有效。在不能或不想进行同步遍历，但又需要从并发线程中排除冲突时，它也很有用。
	 */
    private final CopyOnWriteArrayList<String> libPaths = new CopyOnWriteArrayList<String>();

    NativeLibClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * @Title: addNativeClassPath 
     * @Description: 将文件加入到当前线程类加载器的lib目录中
     * @param path File类型文件
     * @throws IOException
     * @return: void
     */
    public void addNativeClassPath(File path) throws IOException {
        if (path != null) {
            libPaths.addIfAbsent(path.getCanonicalPath());
        }
    }

    /**
     * @Title: addNativeClassPath 
     * @Description: 将文件加入到当前线程类加载器的lib目录中
     * @param path 字符串文件名
     * @return: void
     */
    public void addNativeClassPath(String path) {
        if (path != null) {
            StringTokenizer t = new StringTokenizer(path, File.pathSeparator);
            while (t.hasMoreTokens()) {
                libPaths.addIfAbsent(t.nextToken());
            }
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected String findLibrary(String libname) {
        String libFileName = System.mapLibraryName(libname);//将指定的库名转化为平台字符串表示的库名
        for (String path : libPaths) {
            File libFile = new File(path, libFileName);
            if (libFile.exists()) {
                return libFile.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * @Title: getNativeLibPaths 
     * @Description: 获取当前线程的lib路径
     * @return: List<String>
     */
    public List<String> getNativeLibPaths() {
        return new ArrayList<String>(libPaths);
    }
}
