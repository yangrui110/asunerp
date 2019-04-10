
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
 * Implements the &lt;remove-related&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cremoverelated%3E}}">Mini-language Reference</a>
 */
public final class RemoveRelated extends MethodOperation {

    public static final String module = RemoveRelated.class.getName();
    private final FlexibleStringExpander relationNameFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public RemoveRelated(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "relation-name", "do-cache-clear");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "relation-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        relationNameFse = FlexibleStringExpander.getInstance(element.getAttribute("relation-name"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        String relationName = relationNameFse.expandString(methodContext.getEnvMap());
        try {
            value.getDelegator().removeRelated(relationName, value);
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while removing related entities: " + e.getMessage();
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
        StringBuilder sb = new StringBuilder("<remove-related ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("relation-name=\"").append(this.relationNameFse).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;remove-related&gt; element.
     */
    public static final class RemoveRelatedFactory implements Factory<RemoveRelated> {
        @Override
        public RemoveRelated createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new RemoveRelated(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "remove-related";
        }
    }
}
