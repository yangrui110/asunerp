
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
 * Implements the &lt;request-to-field&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Crequesttofield%3E}}">Mini-language Reference</a>
 */
public final class RequestToField extends MethodOperation {

    private final FlexibleStringExpander defaultFse;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander attributeNameFse;

    public RequestToField(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "request-name", "default");
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
        this.defaultFse = FlexibleStringExpander.getInstance(element.getAttribute("default"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            String attributeName = attributeNameFse.expandString(methodContext.getEnvMap());
            Object value = methodContext.getRequest().getAttribute(attributeName);
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                value = defaultFse.expandString(methodContext.getEnvMap());
            }
            fieldFma.put(methodContext.getEnvMap(), value);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<request-to-field ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (!this.attributeNameFse.isEmpty()) {
            sb.append("request-name=\"").append(this.attributeNameFse).append("\" ");
        }
        if (!this.defaultFse.isEmpty()) {
            sb.append("default=\"").append(this.defaultFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;request-to-field&gt; element.
     */
    public static final class RequestToFieldFactory implements Factory<RequestToField> {
        @Override
        public RequestToField createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new RequestToField(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "request-to-field";
        }
    }
}
