
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
 * Implements the &lt;field-to-session&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfieldtosession%3E}}">Mini-language Reference</a>
 */
public final class FieldToSession extends MethodOperation {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander attributeNameFse;

    public FieldToSession(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "session-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        String attributeName = element.getAttribute("session-name");
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
                    methodContext.getRequest().getSession().setAttribute(attributeName, fieldVal);
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<field-to-session ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (!this.attributeNameFse.isEmpty()) {
            sb.append("session-name=\"").append(this.attributeNameFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;field-to-session&gt; element.
     */
    public static final class FieldToSessionFactory implements Factory<FieldToSession> {
        @Override
        public FieldToSession createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FieldToSession(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "field-to-session";
        }
    }
}
