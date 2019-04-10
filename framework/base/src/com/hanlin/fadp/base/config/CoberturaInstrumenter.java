package com.hanlin.fadp.base.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.hanlin.fadp.base.start.Instrumenter;

/**
 * @ClassName: CoberturaInstrumenter 
 * @Description: 测试类
 * @author: 祖国
 * @date: 2015年10月2日 下午10:49:03
 */
public final class CoberturaInstrumenter implements Instrumenter {
	/**ClassInstrumenter构造方法  */
    private static final Constructor<?> INSTRUMENTER_CONSTRUCTOR;
    /**获取ClassInstrumenter的isInstrumented方法，并设置可访问  */
    private static final Method IS_INSTRUMENTED_METHOD;
    static {
        try {
        	//cobertura的2.0版本中已经没有ClassInstrumenter类了，1.9.4版本有该类
            Class<?> clz = CoberturaInstrumenter.class.getClassLoader().loadClass("net.sourceforge.cobertura.instrument.ClassInstrumenter");
            INSTRUMENTER_CONSTRUCTOR = clz.getConstructor(ProjectData.class, ClassVisitor.class, Collection.class, Collection.class);
            INSTRUMENTER_CONSTRUCTOR.setAccessible(true);
            IS_INSTRUMENTED_METHOD = clz.getDeclaredMethod("isInstrumented");
            IS_INSTRUMENTED_METHOD.setAccessible(true);
        } catch (Throwable t) {
            throw (InternalError) new InternalError(t.getMessage()).initCause(t);
        }
    }

    /**测试数据的url文件  */
    protected File dataFile;
    /** 测试数据 */
    protected ProjectData projectData;
    /**是否进行代码打桩 */
    protected boolean forInstrumenting;

    /**
     * 获取默认的"cobertura.ser"文件
     */
    public File getDefaultFile() throws IOException {
        return CoverageDataFileHandler.getDefaultDataFile();
    }

    public void open(File dataFile, boolean forInstrumenting) throws IOException {
        System.setProperty("net.sourceforge.cobertura.datafile", dataFile.toString());
        this.forInstrumenting = forInstrumenting;
        this.dataFile = dataFile;
        if (forInstrumenting) {
            if (dataFile.exists()) {
            	//加载.ser文件
                projectData = CoverageDataFileHandler.loadCoverageData(dataFile);
            } else {
            	//测试数据信息，将序列化到文件
                projectData = new ProjectData();
            }
        }
    }

    /**
     * 将测试数据projectData写入到测试文件dataFile中
     */
    public void close() throws IOException {
        if (forInstrumenting) {
            CoverageDataFileHandler.saveCoverageData(projectData, dataFile);
        }
    }

    public byte[] instrumentClass(byte[] bytes) throws IOException {
        if (forInstrumenting) {
        	// ClassReader:该类用来解析编译过的class字节码文件
            ClassReader cr = new ClassReader(bytes);
            //ClassWriter:该类用来重新构建编译后的类，比如说修改类名、属性以及方法，甚至可以生成新的类的字节码文件。
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
            try {
            	//ClassVisitor，在 ASM3.0 中是一个接口，到了 ASM4.0 与 ClassAdapter 抽象类合并。主要负责 “拜访” 类成员信息。
            	//其中包括（标记在类上的注解，类的构造方法，类的字段，类的方法，静态代码块）
            	//ClassInstrumenter extends ClassAdapter,ClassAdapter implements ClassVisitor
                ClassVisitor ci = (ClassVisitor) INSTRUMENTER_CONSTRUCTOR.newInstance(projectData, cw, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
                cr.accept(ci, 0);
                //调用ClassInstrumenter（ClassVisitor型的ci）类的isInstrumented方法
                if (((Boolean) IS_INSTRUMENTED_METHOD.invoke(ci)).booleanValue()) {
                    return cw.toByteArray();
                }
            } catch (Throwable t) {
                throw (IOException) new IOException(t.getMessage()).initCause(t);
            }
        }
        return bytes;
    }
}
