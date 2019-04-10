
package com.hanlin.fadp.minilang.method.eventops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;field-to-request&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfieldtorequest%3E}}">Mini-language Reference</a>
 */
public final class FieldToRequest extends MethodOperation {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander attributeNameFse;

    public FieldToRequest(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "request-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        String attributeName = element.getAttribute("request-name");
        if (!attributeName.isEmpty()) {
            this.attributeNameFse = FlexibleStringExpander.getInstance(attributeName);
        } else {
            this.attributeNameFse = FlexibleStringExpander.getInstance(this.fieldFma.toString());
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            Object fieldVal = fieldFma.get(methodContext.getEnvMap());
            if (fieldVal != null) {
                String attributeName = attributeNameFse.expandString(methodContext.getEnvMap());
                if (!attributeName.isEmpty()) {
                    methodContext.getRequest().setAttribute(attributeName, fieldVal);
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<field-to-request ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (!this.attributeNameFse.isEmpty()) {
            sb.append("request-name=\"").append(this.attributeNameFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;field-to-request&gt; element.
     */
    public static final class FieldToRequestFactory implements Factory<FieldToRequest> {
        @Override
        public FieldToRequest createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FieldToRequest(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "field-to-request";
        }
    }
}
