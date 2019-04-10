
package com.hanlin.fadp.base.start;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName: Instrumenter 
 * @Description: 测试接口
 * @author: turtle
 * @date: 2015年9月14日 下午1:19:23
 */
public interface Instrumenter {
    File getDefaultFile() throws IOException;
    void open(File dataFile, boolean forInstrumenting) throws IOException;
    byte[] instrumentClass(byte[] bytes) throws IOException;
    void close() throws IOException;
}
