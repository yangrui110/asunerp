
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.MessageString;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * A single operation, does the specified operation on the given field
 */
public abstract class SimpleMapOperation {

    String fieldName;
    boolean isProperty = false;
    String message = null;
    String propertyResource = null;
    SimpleMapProcess simpleMapProcess;

    public SimpleMapOperation(Element element, SimpleMapProcess simpleMapProcess) {
        Element failMessage = UtilXml.firstChildElement(element, "fail-message");
        Element failProperty = UtilXml.firstChildElement(element, "fail-property");
        if (failMessage != null) {
            this.message = failMessage.getAttribute("message");
            this.isProperty = false;
        } else if (failProperty != null) {
            this.propertyResource = failProperty.getAttribute("resource");
            this.message = failProperty.getAttribute("property");
            this.isProperty = true;
        }
        this.simpleMapProcess = simpleMapProcess;
        this.fieldName = simpleMapProcess.getFieldName();
    }

    public void addMessage(List<Object> messages, ClassLoader loader, Locale locale) {
        if (!isProperty && message != null) {
            messages.add(new MessageString(message, fieldName, true));
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] Adding message: " + message, module);
        } else if (isProperty && propertyResource != null && message != null) {
            // this one doesn't do the proper i18n: String propMsg = UtilProperties.getPropertyValue(UtilURL.fromResource(propertyResource, loader), message);
            String propMsg = UtilProperties.getMessage(propertyResource, message, locale);
            if (UtilValidate.isEmpty(propMsg)) {
                messages.add(new MessageString("Simple Map Processing error occurred, but no message was found, sorry.", fieldName, propertyResource, message, locale, true));
            } else {
                messages.add(new MessageString(propMsg, fieldName, propertyResource, message, locale, true));
            }
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] Adding property message: " + propMsg, module);
        } else {
            messages.add(new MessageString("Simple Map Processing error occurred, but no message was found, sorry.", fieldName, true));
            // if (Debug.infoOn()) Debug.logInfo("[SimpleMapOperation.addMessage] ERROR: No message found", module);
        }
    }

    public abstract void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader);
}
