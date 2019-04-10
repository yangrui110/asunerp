
package com.hanlin.fadp.entity.finder;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.Condition;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionExpr;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionList;
import com.hanlin.fadp.entity.finder.EntityFinderUtil.ConditionObject;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelFieldTypeReader;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a condition
 *
 */
@SuppressWarnings("serial")
public class ByConditionFinder extends ListFinder {
    public static final String module = ByConditionFinder.class.getName();

    protected Condition whereCondition;
    protected Condition havingCondition;

    public ByConditionFinder(Element element) {
        super(element, "condition");

        // NOTE: the whereCondition can be null, ie (condition-expr | condition-list) is optional; if left out, means find all, or with no condition in essense
        // process condition-expr | condition-list
        Element conditionExprElement = UtilXml.firstChildElement(element, "condition-expr");
        Element conditionListElement = UtilXml.firstChildElement(element, "condition-list");
        Element conditionObjectElement = UtilXml.firstChildElement(element, "condition-object");
        if (conditionExprElement != null) {
            this.whereCondition = new ConditionExpr(conditionExprElement);
        } else if (conditionListElement != null) {
            this.whereCondition = new ConditionList(conditionListElement);
        } else if (conditionObjectElement != null) {
            this.whereCondition = new ConditionObject(conditionObjectElement);
        }

        Element havingConditionListElement = UtilXml.firstChildElement(element, "having-condition-list");
        if (havingConditionListElement != null) {
            this.havingCondition = new ConditionList(havingConditionListElement);
        }
    }

    @Override
    public EntityCondition getWhereEntityCondition(Map<String, Object> context, ModelEntity modelEntity, ModelFieldTypeReader modelFieldTypeReader) {
        // create whereEntityCondition from whereCondition
        if (this.whereCondition != null) {
            return this.whereCondition.createCondition(context, modelEntity, modelFieldTypeReader);
        }
        return null;
    }

    @Override
    public EntityCondition getHavingEntityCondition(Map<String, Object> context, ModelEntity modelEntity, ModelFieldTypeReader modelFieldTypeReader) {
        // create havingEntityCondition from havingCondition
        if (this.havingCondition != null) {
            return this.havingCondition.createCondition(context, modelEntity, modelFieldTypeReader);
        }
        return null;
    }
}
