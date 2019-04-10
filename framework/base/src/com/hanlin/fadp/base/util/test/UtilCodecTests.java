
package com.hanlin.fadp.base.util.test;

import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilCodecTests  extends GenericTestCaseBase {
    public UtilCodecTests(String name) {
        super(name);
    }

    private static void encoderTest(String label, UtilCodec.SimpleEncoder encoder, String wanted, String toEncode) {
        assertNull(label + "(encoder):null", encoder.encode(null));
        assertEquals(label + "(encoder):encode", wanted, encoder.encode(toEncode));
    }

    public void testGetEncoder() {
        encoderTest("string", UtilCodec.getEncoder("string"), "abc\\\"def", "abc\"def");
        encoderTest("xml", UtilCodec.getEncoder("xml"), "&#x3c;&#x3e;&#x27;&#x22;", "<>'\"");
        encoderTest("html", UtilCodec.getEncoder("html"), "&lt;&gt;&#x27;&quot;", "<>'\"");
        assertNull("invalid encoder", UtilCodec.getEncoder("foobar"));
    }
    private static void checkStringForHtmlStrictNone_test(String label, String fixed, String input, String... wantedMessages) {
        List<String> gottenMessages = new ArrayList<String>();
        assertEquals(label, fixed, UtilCodec.checkStringForHtmlStrictNone(label, input, gottenMessages));
        assertEquals(label, Arrays.asList(wantedMessages), gottenMessages);
    }

    public void testCheckStringForHtmlStrictNone() {
        checkStringForHtmlStrictNone_test("null pass-thru", null, null);
        checkStringForHtmlStrictNone_test("empty pass-thru", "", "");
        checkStringForHtmlStrictNone_test("o-numeric-encode", "foo", "f&#111;o");
        checkStringForHtmlStrictNone_test("o-hex-encode", "foo", "f%6fo");
        // jacopoc: temporarily commented because this test is failing after the upgrade of owasp-esapi (still investigating)
        //checkStringForHtmlStrictNone_test("o-double-hex-encode", "foo", "f%256fo");
        checkStringForHtmlStrictNone_test("<-not-allowed", "f<oo", "f<oo", "In field [<-not-allowed] less-than (<) and greater-than (>) symbols are not allowed.");
        checkStringForHtmlStrictNone_test(">-not-allowed", "f>oo", "f>oo", "In field [>-not-allowed] less-than (<) and greater-than (>) symbols are not allowed.");
        checkStringForHtmlStrictNone_test("high-ascii", "fÀ®", "f%C0%AE");
        // this looks like a bug, namely the extra trailing ;
        // jacopoc: temporarily commented because this test is failing after the upgrade of owasp-esapi (still investigating)
        //checkStringForHtmlStrictNone_test("double-ampersand", "f\";oo", "f%26quot%3boo");
        checkStringForHtmlStrictNone_test("double-encoding", "%2%353Cscript", "%2%353Cscript", "In field [double-encoding] found character escaping (mixed or double) that is not allowed or other format consistency error: com.hanlin.fadp.base.util.UtilCodec$IntrusionException: Input validation failure");
    }

}
