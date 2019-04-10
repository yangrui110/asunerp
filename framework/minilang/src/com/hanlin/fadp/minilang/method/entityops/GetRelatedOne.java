
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;get-related-one&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cgetrelatedone%3E}}">Mini-language Reference</a>
 */
public final class GetRelatedOne extends MethodOperation {

    public static final String module = GetRelatedOne.class.getName();

    private final FlexibleStringExpander relationNameFse;
    private final FlexibleMapAccessor<GenericValue> toValueFma;
    private final FlexibleStringExpander useCacheFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public GetRelatedOne(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "relation-name", "to-value-field", "use-cache");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "relation-name", "to-value-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "to-value-field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        relationNameFse = FlexibleStringExpander.getInstance(element.getAttribute("relation-name"));
        toValueFma = FlexibleMapAccessor.getInstance(element.getAttribute("to-value-field"));
        useCacheFse = FlexibleStringExpander.getInstance(element.getAttribute("use-cache"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        String relationName = relationNameFse.expandString(methodContext.getEnvMap());
        boolean useCache = "true".equals(useCacheFse.expandString(methodContext.getEnvMap()));
        try {
            toValueFma.put(methodContext.getEnvMap(), value.getRelatedOne(relationName, useCache));
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while finding related value: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(relationNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<get-related-one ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("relation-name=\"").append(this.relationNameFse).append("\" ");
        sb.append("to-value-field=\"").append(this.toValueFma).append("\" ");
        if (!useCacheFse.isEmpty()) {
            sb.append("use-cache=\"").append(this.useCacheFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;get-related-one&gt; element.
     */
    public static final class GetRelatedOneFactory implements Factory<GetRelatedOne> {
        @Override
        public GetRelatedOne createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new GetRelatedOne(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "get-related-one";
        }
    }
}
