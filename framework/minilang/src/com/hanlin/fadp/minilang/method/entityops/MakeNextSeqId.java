
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;make-next-seq-id&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cmakenextseqid%3E}}">Mini-language Reference</a>
 */
public final class MakeNextSeqId extends MethodOperation {

    public static final String module = MakeNextSeqId.class.getName();

    private final FlexibleStringExpander incrementByFse;
    private final FlexibleStringExpander numericPaddingFse;
    private final FlexibleStringExpander seqFieldNameFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public MakeNextSeqId(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "seq-field-name", "increment-by", "numeric-padding");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "seq-field-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        seqFieldNameFse = FlexibleStringExpander.getInstance(element.getAttribute("seq-field-name"));
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        numericPaddingFse = FlexibleStringExpander.getInstance(element.getAttribute("numeric-padding"));
        incrementByFse = FlexibleStringExpander.getInstance(element.getAttribute("increment-by"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        String seqFieldName = seqFieldNameFse.expandString(methodContext.getEnvMap());
        String numericPaddingStr = numericPaddingFse.expandString(methodContext.getEnvMap());
        String incrementByStr = incrementByFse.expandString(methodContext.getEnvMap());
        int numericPadding = 5;
        if (!numericPaddingStr.isEmpty()) {
            try {
                numericPadding = Integer.parseInt(numericPaddingStr);
            } catch (Exception e) {
                throw new MiniLangRuntimeException("Invalid number in \"numeric-padding\" attribute", this);
            }
        }
        int incrementBy = 1;
        if (!incrementByStr.isEmpty()) {
            try {
                incrementBy = Integer.parseInt(incrementByStr);
            } catch (Exception e) {
                throw new MiniLangRuntimeException("Invalid number in \"increment-by\" attribute", this);
            }
        }
        value.getDelegator().setNextSubSeqId(value, seqFieldName, numericPadding, incrementBy);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<make-next-seq-id ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("seq-field-name=\"").append(this.seqFieldNameFse).append("\" ");
        if (!incrementByFse.isEmpty()) {
            sb.append("increment-by=\"").append(this.incrementByFse).append("\" ");
        }
        if (!numericPaddingFse.isEmpty()) {
            sb.append("numeric-padding=\"").append(this.numericPaddingFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;make-next-seq-id&gt; element.
     */
    public static final class MakeNextSeqIdFactory implements Factory<MakeNextSeqId> {
        @Override
        public MakeNextSeqId createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new MakeNextSeqId(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "make-next-seq-id";
        }
    }
}
