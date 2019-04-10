
package com.hanlin.fadp.minilang.method.callops;

import java.io.Serializable;

import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Simple class to wrap messages that come either from a straight string or a properties file
 */
@SuppressWarnings("serial")
public final class FlexibleMessage implements Serializable {

    private final FlexibleStringExpander messageFse;
    private final String propertykey;
    private final String propertyResource;

    public FlexibleMessage(Element element, String defaultProperty) {
        if (element != null) {
            String message = UtilXml.elementValue(element);
            if (message != null) {
                messageFse = FlexibleStringExpander.getInstance(message);
                propertykey = null;
                propertyResource = null;
            } else {
                messageFse = null;
                propertykey = MiniLangValidate.checkAttribute(element.getAttribute("property"), defaultProperty);
                propertyResource = MiniLangValidate.checkAttribute(element.getAttribute("resource"), "DefaultMessages");
            }
        } else {
            messageFse = null;
            propertykey = defaultProperty;
            propertyResource = "DefaultMessages";
        }
    }

    public String getMessage(ClassLoader loader, MethodContext methodContext) {
        if (messageFse != null) {
            return messageFse.expandString(methodContext.getEnvMap());
        } else {
            return UtilProperties.getMessage(propertyResource, propertykey, methodContext.getEnvMap(), methodContext.getLocale());
        }
    }
}
