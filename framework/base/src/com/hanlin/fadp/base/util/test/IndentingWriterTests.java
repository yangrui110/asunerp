
package com.hanlin.fadp.base.util.test;

import java.io.StringWriter;

import com.hanlin.fadp.base.lang.SourceMonitored;
import com.hanlin.fadp.base.util.IndentingWriter;
import com.hanlin.fadp.base.test.GenericTestCaseBase;

@SourceMonitored
public class IndentingWriterTests extends GenericTestCaseBase {
    public IndentingWriterTests(String name) {
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

    private static void doTest(String label, boolean doSpace, boolean doNewline, String wanted) throws Exception {
        StringWriter sw = new StringWriter();
        IndentingWriter iw;
        if (!doSpace || !doNewline) {
            iw = new IndentingWriter(sw, doSpace, doNewline);
        } else {
            iw = new IndentingWriter(sw);
        }
        iw.write('a');
        iw.push();
        iw.write("b\nm");
        iw.newline();
        iw.write(new char[] {'1', '\n', '2'});
        iw.space();
        iw.write('\n');
        iw.pop();
        iw.write("e");
        iw.close();
        assertEquals(label, wanted, sw.toString());
    }

    public void testIndentingWriter() throws Exception {
        StringWriter sw = new StringWriter();
        IndentingWriter iw = IndentingWriter.makeIndentingWriter(sw);
        assertSame("makeIndentingWriter - pass-thru", iw, IndentingWriter.makeIndentingWriter(iw));
        doTest("IndentingWriter doSpace:doNewline", true, true, "ab\n m\n 1\n 2 \n e");
        doTest("IndentingWriter doNewline", false, true, "ab\nm\n1\n2\ne");
        doTest("IndentingWriter doSpace", true, false, "ab\n m 1\n 2 \n e");
        doTest("IndentingWriter", false, false, "ab\nm1\n2\ne");
    }
}
