
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.finder.ByConditionFinder;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;entity-condition&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Centitycondition%3E}}">Mini-language Reference</a>
 */
public final class EntityCondition extends EntityOperation {

    public static final String module = EntityCondition.class.getName();

    private final ByConditionFinder finder;

    public EntityCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "filter-by-date", "list", "distinct", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "delegator-name");
            MiniLangValidate.childElements(simpleMethod, element, "condition-expr", "condition-list", "condition-object", "having-condition-list", "select-field", "order-by", "limit-range", "limit-view", "use-iterator");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "condition-expr", "condition-list", "condition-object");
        }
        this.finder = new ByConditionFinder(element);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        try {
            Delegator delegator = getDelegator(methodContext);
            finder.runFind(methodContext.getEnvMap(), delegator);
        } catch (GeneralException e) {
            String errMsg = "Exception thrown while performing entity find: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(this.finder.getEntityName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<entity-condition ");
        sb.append("entity-name=\"").append(this.finder.getEntityName()).append("\" />");
        return sb.toString();
    }

    public static final class EntityConditionFactory implements Factory<EntityCondition> {
        @Override
        public EntityCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EntityCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "entity-condition";
        }
    }
}
