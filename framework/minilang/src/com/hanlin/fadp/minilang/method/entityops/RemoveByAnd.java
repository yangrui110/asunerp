
package com.hanlin.fadp.minilang.method.entityops;

import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;remove-by-and&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cremovebyand%3E}}">Mini-language Reference</a>
 */
public final class RemoveByAnd extends EntityOperation {

    public static final String module = RemoveByAnd.class.getName();
    private final FlexibleStringExpander entityNameFse;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;

    public RemoveByAnd(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "map", "do-cache-clear", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "map", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        @Deprecated
        String entityName = entityNameFse.expandString(methodContext.getEnvMap());
        if (entityName.isEmpty()) {
            throw new MiniLangRuntimeException("Entity name not found.", this);
        }
        try {
            Delegator delegator = getDelegator(methodContext);
            delegator.removeByAnd(entityName, mapFma.get(methodContext.getEnvMap()));
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while removing entities: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(entityNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<remove-by-and ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;remove-by-and&gt; element.
     */
    public static final class RemoveByAndFactory implements Factory<RemoveByAnd> {
        @Override
        public RemoveByAnd createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new RemoveByAnd(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "remove-by-and";
        }
    }
}
