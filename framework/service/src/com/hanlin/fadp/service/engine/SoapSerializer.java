
package com.hanlin.fadp.service.engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.serialize.SerializeException;
import com.hanlin.fadp.entity.serialize.XmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A facade class used to connect SOAP code to the legacy XML serialization code.
 *
 */
public class SoapSerializer {
    public static final String module = SoapSerializer.class.getName();

    public static Object deserialize(String content, Delegator delegator) throws SerializeException, SAXException, ParserConfigurationException, IOException {
        Document document = UtilXml.readXmlDocument(content, false);
        if (document != null) {
            return XmlSerializer.deserialize(document, delegator);
        } else {
            Debug.logWarning("Serialized document came back null", module);
            return null;
        }
    }

    public static String serialize(Object object) throws SerializeException, FileNotFoundException, IOException {
        Document document = UtilXml.makeEmptyXmlDocument("ofbiz-ser");
        Element rootElement = document.getDocumentElement();
        rootElement.appendChild(XmlSerializer.serializeSingle(object, document));
        return UtilXml.writeXmlDocument(document);
    }
}
