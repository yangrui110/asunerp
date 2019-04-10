
package com.hanlin.fadp.minilang.method.envops;

import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;to-string&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ctostring%3E}}">Mini-language Reference</a>
 */
public final class ToString extends MethodOperation {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final String format;
    private final Integer numericPadding;

    public ToString(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<to-string> element is deprecated (use <set>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "format", "numeric-padding");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.constantAttributes(simpleMethod, element, "format", "numeric-padding");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        format = element.getAttribute("format");
        Integer numericPadding = null;
        String npAttribute = element.getAttribute("numeric-padding");
        if (!npAttribute.isEmpty()) {
            try {
                numericPadding = Integer.valueOf(npAttribute);
            } catch (Exception e) {
                MiniLangValidate.handleError("Exception thrown while parsing numeric-padding attribute: " + e.getMessage(), simpleMethod, element);
            }
        }
        this.numericPadding = numericPadding;
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object value = fieldFma.get(methodContext.getEnvMap());
        if (value != null) {
            try {
                if (!format.isEmpty()) {
                    value = MiniLangUtil.convertType(value, String.class, methodContext.getLocale(), methodContext.getTimeZone(), format);
                } else {
                    value = value.toString();
                }
            } catch (Exception e) {
                throw new MiniLangRuntimeException("Exception thrown while converting field to a string: " + e.getMessage(), this);
            }
            if (this.numericPadding != null) {
                value = StringUtil.padNumberString(value.toString(), this.numericPadding.intValue());
            }
            fieldFma.put(methodContext.getEnvMap(), value);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<to-string ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (!this.format.isEmpty()) {
            sb.append("format=\"").append(this.format).append("\" ");
        }
        if (numericPadding != null) {
            sb.append("numeric-padding=\"").append(this.numericPadding).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;to-string&gt; element.
     */
    public static final class ToStringFactory implements Factory<ToString> {
        @Override
        public ToString createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ToString(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "to-string";
        }
    }
}
