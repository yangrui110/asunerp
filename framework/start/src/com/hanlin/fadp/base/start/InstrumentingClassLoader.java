

package com.hanlin.fadp.base.start;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: Instrumenter 
 * @Description: 测试中的类加载器
 * @author: turtle
 * @date: 2015年9月14日 下午1:19:23
 */
public final class InstrumentingClassLoader extends NativeLibClassLoader {

    /**
     * @Title: closeInstrumenter 
     * @Description: 将输入流中数据copy到输出流中
     * @throws IOException
     * @return: void
     */
    private static final void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int r;
        while ((r = in.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
    }

    private final Instrumenter instrumenter;

    /**
     * @Title: closeInstrumenter 
     * @Description: TODO
     * @throws IOException
     * @return: void
     */
    InstrumentingClassLoader(URL[] urls, ClassLoader parent, String instrumenterFileName, String instrumenterClassName)
            throws Exception {
        super(new URL[0], parent);
        URLClassLoader tmpLoader = new URLClassLoader(urls, InstrumenterWorker.class.getClassLoader());
        try {
            instrumenter = (Instrumenter) tmpLoader.loadClass(instrumenterClassName).newInstance();
        } finally {
            tmpLoader.close();
        }
        File instrumenterFile = new File(instrumenterFileName);
        instrumenterFile.delete();
        instrumenter.open(instrumenterFile, true);
        System.out.println("Instrumenter file opened");
        for (URL url : urls) {
            addURL(url);
        }
    }

    @Override
    public void addURL(URL url) {
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        String path = file.getPath();
        //字符串匹配条件：任意字符/fadpxxx.jar或任意字符/fadpxxx.zip，不包含分隔符/
        if (path.matches(".*/fadp[^/]*\\.(jar|zip)")) {
            String prefix = path.substring(0, path.length() - 4);//去掉.jar或.zip
            int slash = prefix.lastIndexOf("/");
            if (slash != -1)//如果含有"/"分隔符，则取分隔符后面的字符串，最后形成"fadpxxx"的字符串
                prefix = prefix.substring(slash + 1);
            prefix += "-";//形成"fadpxxx-"的字符串
            File zipTmp = null;
            try {
            	//zipTmp="instrumented-fadpxxx-.jar"或zipTmp="instrumented-fadpxxx-.zip"
                zipTmp = File.createTempFile("instrumented-" + prefix, path.substring(path.length() - 4));
                zipTmp.deleteOnExit();////在JVM进程退出的时候删除该文件,通常用在临时文件的删除.
                ZipInputStream zin = new ZipInputStream(new FileInputStream(file));
                ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipTmp));
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    InputStream in;
                    long size;
                    if (entry.getName().endsWith(".class")) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        copy(zin, baos);
                        byte[] bytes = instrumenter.instrumentClass(baos.toByteArray());
                        size = bytes.length;
                        in = new ByteArrayInputStream(bytes);
                    } else {
                        in = zin;
                        size = entry.getSize();
                    }
                    ZipEntry newEntry = new ZipEntry(entry);
                    newEntry.setSize(size);
                    newEntry.setCompressedSize(-1);
                    zout.putNextEntry(newEntry);
                    copy(in, zout);
                    if (entry.getName().endsWith(".class")) {
                        in.close();
                    }
                }
                zout.close();
                System.out.println("Instrumented file: " + zipTmp.getCanonicalPath());
                super.addURL(zipTmp.toURI().toURL());
            } catch (IOException e) {
                System.err.println("Exception thrown while instrumenting " + file + ": ");
                e.printStackTrace(System.err);
                if (zipTmp != null) {
                    zipTmp.delete();
                }
            }
        } else {
            super.addURL(url);
        }
    }

    /**
     * @Title: closeInstrumenter 
     * @Description: TODO
     * @throws IOException
     * @return: void
     */
    void closeInstrumenter() throws IOException {
        instrumenter.close();
    }
    
    
    //测试
    public static void main(String[] args){
    	
    }
}
