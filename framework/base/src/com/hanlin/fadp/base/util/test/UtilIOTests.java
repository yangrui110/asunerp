
package com.hanlin.fadp.base.util.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.hanlin.fadp.base.lang.SourceMonitored;
import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilIO;

@SourceMonitored
public class UtilIOTests extends GenericTestCaseBase {
    private static final byte[] trademarkBytes = new byte[] {
        (byte) 0xE2, (byte) 0x84, (byte) 0xA2
    };
    public UtilIOTests(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReadString() throws Exception {
        readStringTest_0("unix line ending", "\n", new byte[] { 0x0A });
        readStringTest_0("mac line ending", "\r", new byte[] { 0x0D });
        readStringTest_0("windows line ending", "\r\n", new byte[] { 0x0D, 0x0A });
    }

    private static byte[] join(byte[]... parts) {
        int count = 0;
        for (byte[] part: parts) {
            count += part.length;
        }
        byte[] result = new byte[count];
        int i = 0;
        for (byte[] part: parts) {
            System.arraycopy(part, 0, result, i, part.length);
            i += part.length;
        }
        return result;
    }

    private static void readStringTest_0(String label, String lineSeparator, byte[] extra) throws IOException {
        String originalLineSeparator = System.getProperty("line.separator");
        try {
            System.getProperties().put("line.separator", lineSeparator);
            readStringTest_1(label + ":mark", "\u2122", join(trademarkBytes));
            readStringTest_1(label + ":mark NL", "\u2122\n", join(trademarkBytes, extra));
            readStringTest_1(label + ":NL mark", "\n\u2122", join(extra, trademarkBytes));
        } finally {
            System.getProperties().put("line.separator", originalLineSeparator);
        }
    }

    private static void readStringTest_1(String label, String wanted, byte[] toRead) throws IOException {
        assertEquals("readString bytes default:" + label, wanted, UtilIO.readString(toRead));
        assertEquals("readString bytes UTF-8:" + label, wanted, UtilIO.readString(toRead, "UTF-8"));
        assertEquals("readString bytes UTF8:" + label, wanted, UtilIO.readString(toRead, UtilIO.UTF8));
        assertEquals("readString bytes offset/length default:" + label, wanted, UtilIO.readString(toRead, 0, toRead.length));
        assertEquals("readString bytes offset/length UTF-8:" + label, wanted, UtilIO.readString(toRead, 0, toRead.length, "UTF-8"));
        assertEquals("readString bytes offset/length UTF8:" + label, wanted, UtilIO.readString(toRead, 0, toRead.length, UtilIO.UTF8));
        assertEquals("readString stream default:" + label, wanted, UtilIO.readString(new ByteArrayInputStream(toRead)));
        assertEquals("readString stream UTF-8:" + label, wanted, UtilIO.readString(new ByteArrayInputStream(toRead), "UTF-8"));
        assertEquals("readString stream UTF8:" + label, wanted, UtilIO.readString(new ByteArrayInputStream(toRead), UtilIO.UTF8));
    }

    public void testWriteString() throws Exception {
        writeStringTest_0("unix line ending", "\n", new byte[] { 0x0A });
        writeStringTest_0("mac line ending", "\r", new byte[] { 0x0D });
        writeStringTest_0("windows line ending", "\r\n", new byte[] { 0x0D, 0x0A });
    }

    private static void writeStringTest_0(String label, String lineSeparator, byte[] extra) throws IOException {
        String originalLineSeparator = System.getProperty("line.separator");
        try {
            System.getProperties().put("line.separator", lineSeparator);
            writeStringTest_1(label + ":mark", join(trademarkBytes), "\u2122");
            writeStringTest_1(label + ":mark NL", join(trademarkBytes, extra), "\u2122\n");
            writeStringTest_1(label + ":NL mark", join(extra, trademarkBytes), "\n\u2122");
        } finally {
            System.getProperties().put("line.separator", originalLineSeparator);
        }
    }

    private static void writeStringTest_1(String label, byte[] wanted, String toWrite) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        UtilIO.writeString(baos, toWrite);
        assertEquals("writeString default:" + label, wanted, baos.toByteArray());
        baos = new ByteArrayOutputStream();
        UtilIO.writeString(baos, "UTF-8", toWrite);
        assertEquals("writeString UTF-8:" + label, wanted, baos.toByteArray());
        baos = new ByteArrayOutputStream();
        UtilIO.writeString(baos, UtilIO.UTF8, toWrite);
        assertEquals("writeString UTF8:" + label, wanted, baos.toByteArray());
    }
}
