
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;sequenced-id&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Csequencedid%3E}}">Mini-language Reference</a>
 */
public final class SequencedIdToEnv extends EntityOperation {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final boolean getLongOnly;
    private final FlexibleStringExpander sequenceNameFse;
    private final long staggerMax;

    public SequencedIdToEnv(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "sequence-name", "field", "get-long-only", "stagger-max", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "sequence-name", "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        sequenceNameFse = FlexibleStringExpander.getInstance(element.getAttribute("sequence-name"));
        fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        getLongOnly = "true".equals(element.getAttribute("get-long-only"));
        long staggerMax = 1;
        String staggerMaxAttribute = element.getAttribute("stagger-max");
        if (!staggerMaxAttribute.isEmpty()) {
            try {
                staggerMax = Long.parseLong(staggerMaxAttribute);
                if (staggerMax < 1) {
                    staggerMax = 1;
                }
            } catch (NumberFormatException e) {
                MiniLangValidate.handleError("Invalid stagger-max attribute value: " + e.getMessage(), simpleMethod, element);
            }
        }
        this.staggerMax = staggerMax;
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String seqName = sequenceNameFse.expandString(methodContext.getEnvMap());
        Delegator delegator = getDelegator(methodContext);
        if (getLongOnly) {
            fieldFma.put(methodContext.getEnvMap(), delegator.getNextSeqIdLong(seqName, staggerMax));
        } else {
            fieldFma.put(methodContext.getEnvMap(), delegator.getNextSeqId(seqName, staggerMax));
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<sequenced-id ");
        sb.append("sequence-name=\"").append(this.sequenceNameFse).append("\" ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        sb.append("stagger-max=\"").append(this.staggerMax).append("\" ");
        if (this.getLongOnly) {
            sb.append("get-long-only=\"true\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;sequenced-id&gt; element.
     */
    public static final class SequencedIdFactory implements Factory<SequencedIdToEnv> {
        @Override
        public SequencedIdToEnv createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new SequencedIdToEnv(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "sequenced-id";
        }
    }
}
