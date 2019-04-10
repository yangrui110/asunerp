
package com.hanlin.fadp.minilang.method.serviceops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;field-to-result&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfieldtoresult%3E}}">Mini-language Reference</a>
 */
public final class FieldToResult extends MethodOperation {

    public static final String module = FieldToResult.class.getName();

    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleMapAccessor<Object> resultFma;

    public FieldToResult(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "result-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field", "result-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        String resultNameAttribute = element.getAttribute("result-name");
        if (resultNameAttribute.length() == 0) {
            this.resultFma = this.fieldFma;
        } else {
            this.resultFma = FlexibleMapAccessor.getInstance(resultNameAttribute);
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object fieldVal = this.fieldFma.get(methodContext.getEnvMap());
        if (fieldVal != null) {
            if (this.resultFma.containsNestedExpression()) {
                /*
                 *  Replace FMA nested expression functionality with our own.
                 *  The nested expression must be evaluated once using the
                 *  method context, [methodContext.getEnvMap()] then again to
                 *  place the value in the result Map [methodContext.getResults()].
                 */
                FlexibleStringExpander fse = FlexibleStringExpander.getInstance(this.resultFma.getOriginalName());
                String expression = fse.expandString(methodContext.getEnvMap());
                FlexibleMapAccessor<Object> resultFma = FlexibleMapAccessor.getInstance(expression);
                resultFma.put(methodContext.getResults(), fieldVal);
            } else {
                this.resultFma.put(methodContext.getResults(), fieldVal);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<field-to-result ");
        if (!this.fieldFma.isEmpty()) {
            sb.append("field=\"").append(this.fieldFma).append("\" ");
        }
        if (!this.resultFma.isEmpty()) {
            sb.append("result-name=\"").append(this.resultFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;field-to-request&gt; element.
     */
    public static final class FieldToResultFactory implements Factory<FieldToResult> {
        @Override
        public FieldToResult createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FieldToResult(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "field-to-result";
        }
    }
}
