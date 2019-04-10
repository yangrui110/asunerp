
package com.hanlin.fadp.base.util.test;

import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilXml;

public class UtilXmlTests extends GenericTestCaseBase {

    public UtilXmlTests(String name) {
        super(name);
    }

    public void testUnsupportedClassConverter() throws Exception {
        String unsupportedClassInXml =
            "<handler class=\"java.beans.EventHandler\">" +
                " <target class=\"java.lang.ProcessBuilder\">" +
                    " <command>" +
                        " <string>open</string>" +
                        " <string>.</string>" +
                    " </command>" +
                " </target>" +
                " <action>start</action>" +
            "</handler>";
        try {
            @SuppressWarnings("unused")
            Object unsupportedObject = UtilXml.fromXml(unsupportedClassInXml);
            fail("Unsupported class in XML");
        } catch (Exception e) {
        }
    }
}
