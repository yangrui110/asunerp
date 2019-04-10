
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.Condition;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionExpr;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionList;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionObject;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;entity-count&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Centitycount%3E}}">Mini-language Reference</a>
 */
public final class EntityCount extends EntityOperation {

    public static final String module = EntityCount.class.getName();

    private final FlexibleMapAccessor<Long> countFma;
    private final FlexibleStringExpander entityNameFse;
    private final Condition havingCondition;
    private final Condition whereCondition;

    public EntityCount(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "count-field", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "count-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "count-field", "delegator-name");
            MiniLangValidate.childElements(simpleMethod, element, "condition-expr", "condition-list", "condition-object", "having-condition-list");
            MiniLangValidate.requireAnyChildElement(simpleMethod, element, "condition-expr", "condition-list", "condition-object");
        }
        this.entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        this.countFma = FlexibleMapAccessor.getInstance(element.getAttribute("count-field"));
        int conditionElementCount = 0;
        Element conditionExprElement = UtilXml.firstChildElement(element, "condition-expr");
        conditionElementCount = conditionExprElement == null ? conditionElementCount : conditionElementCount++;
        Element conditionListElement = UtilXml.firstChildElement(element, "condition-list");
        conditionElementCount = conditionListElement == null ? conditionElementCount : conditionElementCount++;
        Element conditionObjectElement = UtilXml.firstChildElement(element, "condition-object");
        conditionElementCount = conditionObjectElement == null ? conditionElementCount : conditionElementCount++;
        if (conditionElementCount > 1) {
            MiniLangValidate.handleError("Element must include only one condition child element", simpleMethod, conditionObjectElement);
        }
        if (conditionExprElement != null) {
            this.whereCondition = new ConditionExpr(conditionExprElement);
        } else if (conditionListElement != null) {
            this.whereCondition = new ConditionList(conditionListElement);
        } else if (conditionObjectElement != null) {
            this.whereCondition = new ConditionObject(conditionObjectElement);
        } else {
            this.whereCondition = null;
        }
        Element havingConditionListElement = UtilXml.firstChildElement(element, "having-condition-list");
        if (havingConditionListElement != null) {
            this.havingCondition = new ConditionList(havingConditionListElement);
        } else {
            this.havingCondition = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        try {
            Delegator delegator = getDelegator(methodContext);
            String entityName = this.entityNameFse.expandString(methodContext.getEnvMap());
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            EntityCondition whereEntityCondition = null;
            if (this.whereCondition != null) {
                whereEntityCondition = this.whereCondition.createCondition(methodContext.getEnvMap(), modelEntity, delegator.getModelFieldTypeReader(modelEntity));
            }
            EntityCondition havingEntityCondition = null;
            if (this.havingCondition != null) {
                havingEntityCondition = this.havingCondition.createCondition(methodContext.getEnvMap(), modelEntity, delegator.getModelFieldTypeReader(modelEntity));
            }
            long count = EntityQuery.use(delegator).from(entityName).where(whereEntityCondition).having(havingEntityCondition).queryCount();
            this.countFma.put(methodContext.getEnvMap(), count);
        } catch (GeneralException e) {
            String errMsg = "Exception thrown while performing entity count: " + e.getMessage();
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
        StringBuilder sb = new StringBuilder("<entity-count ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        sb.append("count-field=\"").append(this.countFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;entity-count&gt; element.
     */
    public static final class EntityCountFactory implements Factory<EntityCount> {
        @Override
        public EntityCount createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EntityCount(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "entity-count";
        }
    }
}
