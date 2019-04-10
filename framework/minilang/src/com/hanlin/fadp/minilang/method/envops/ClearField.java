
package com.hanlin.fadp.minilang.method.envops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;clear-field&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{<clearfield>}}">Mini-language Reference</a>
 */
public final class ClearField extends MethodOperation {

    private final FlexibleMapAccessor<Object> fieldFma;

    public ClearField(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        fieldFma.put(methodContext.getEnvMap(), null);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<set ");
        sb.append("field=\"").append(this.fieldFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;clear-field&gt; element.
     */
    public static final class ClearFieldFactory implements Factory<ClearField> {
        @Override
        public ClearField createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ClearField(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "clear-field";
        }
    }
}
