
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;clone-value&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cclonevalue%3E}}">Mini-language Reference</a>
 */
public final class CloneValue extends MethodOperation {

    private final FlexibleMapAccessor<GenericValue> newValueFma;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public CloneValue(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "new-value-field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "new-value-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "new-value-field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        newValueFma = FlexibleMapAccessor.getInstance(element.getAttribute("new-value-field"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value != null) {
            newValueFma.put(methodContext.getEnvMap(), GenericValue.create(value));
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<clone-value ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("new-value-field=\"").append(this.newValueFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;clone-value&gt; element.
     */
    public static final class CloneValueFactory implements Factory<CloneValue> {
        @Override
        public CloneValue createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CloneValue(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "clone-value";
        }
    }
}
