
package com.hanlin.fadp.minilang.method;

import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangElement;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * Implements the &lt;fail-message&gt; and &lt;fail-property&gt; elements.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{<failmessage>}}">Mini-language Reference</a>
 */
public final class MessageElement extends MiniLangElement {

    public static MessageElement fromParentElement(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        Element childElement = UtilXml.firstChildElement(element, "fail-message");
        if (childElement != null) {
            return new MessageElement(childElement, simpleMethod);
        } else {
            childElement = UtilXml.firstChildElement(element, "fail-property");
            if (childElement != null) {
                return new MessageElement(childElement, simpleMethod);
            } else {
                return null;
            }
        }
    }

    private final FlexibleStringExpander messageFse;
    private final String propertykey;
    private final String propertyResource;

    public MessageElement(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if ("fail-message".equals(element.getTagName())) {
            if (MiniLangValidate.validationOn()) {
                MiniLangValidate.attributeNames(simpleMethod, element, "message");
                MiniLangValidate.requiredAttributes(simpleMethod, element, "message");
                MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "message");
            }
            this.messageFse = FlexibleStringExpander.getInstance(element.getAttribute("message"));
            this.propertykey = null;
            this.propertyResource = null;
        } else {
            if (MiniLangValidate.validationOn()) {
                MiniLangValidate.attributeNames(simpleMethod, element, "property", "resource");
                MiniLangValidate.requiredAttributes(simpleMethod, element, "property", "resource");
                MiniLangValidate.constantAttributes(simpleMethod, element, "property", "resource");
            }
            this.messageFse = null;
            this.propertykey = element.getAttribute("property");
            this.propertyResource = element.getAttribute("resource");
        }
    }

    public String getMessage(MethodContext methodContext) {
        if (messageFse != null) {
            return messageFse.expandString(methodContext.getEnvMap());
        } else {
            return UtilProperties.getMessage(propertyResource, propertykey, methodContext.getEnvMap(), methodContext.getLocale());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.messageFse != null) {
            sb.append("<fail-message message=\"").append(this.messageFse).append("\" />");
        }
        if (this.propertykey != null) {
            sb.append("<fail-property property=\"").append(this.propertykey).append(" resource=\"").append(this.propertyResource).append("\" />");
        }
        return sb.toString();
    }
}
