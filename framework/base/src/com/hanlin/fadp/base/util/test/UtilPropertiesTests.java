
package com.hanlin.fadp.base.util.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

import com.hanlin.fadp.base.test.GenericTestCaseBase;
import com.hanlin.fadp.base.util.UtilProperties;

public class UtilPropertiesTests extends GenericTestCaseBase {

    private final String country = "AU";
    private final String language = "en";
    private final Locale locale = new Locale(language, country);

    public UtilPropertiesTests(String name) {
        super(name);
    }

    /**
     * Old style xml:lang attribute value was of form en_AU. Test this
     * format still works.
     * @throws Exception
     */
    public void testReadXmlLangOldStyle() throws Exception {
        Properties result = xmlToProperties("_");
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Key Value", result.getProperty("PropertyKey"));
    }

    /**
     * New (and correct) style xml:lang value is en-AU.
     * Test it works.
      * @throws Exception
     */
    public void testReadXmlLangNewStyle() throws Exception {
        Properties result = xmlToProperties("-");
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Key Value", result.getProperty("PropertyKey"));

    }

    private Properties xmlToProperties(String separator) throws IOException {
        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<resource xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "          xsi:noNamespaceSchemaLocation=\"http://ofbiz.apache.org/dtds/ofbiz-properties.xsd\">\n" +
                "    <property key=\"PropertyKey\">\n" +
                "        <value xml:lang=\"" +
                language + separator + country +
                "\">Key Value</value>\n" +
                "    </property>\n" +
                "</resource>";
        InputStream in = new ByteArrayInputStream(new String(xmlData.getBytes(), Charset.forName("UTF-8")).getBytes());
        return UtilProperties.xmlToProperties(in, locale, null);
    }
}
