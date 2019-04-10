

package com.hanlin.fadp.entity.condition;

import groovy.util.BuilderSupport;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericModelException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.model.ModelEntity;

public class EntityConditionBuilder extends BuilderSupport {
    public static final String module = EntityConditionBuilder.class.getName();

    @SuppressWarnings("serial")
    private static class ConditionHolder extends EntityCondition {
        protected EntityCondition condition;

        protected ConditionHolder(EntityCondition condition) {
            this.condition = condition;
        }

        public Object asType(Class clz) {
            Debug.logInfo("asType(%s): %s", module, clz, condition);
            if (clz == EntityCondition.class) {
                return condition;
            }
            return this;
        }

        public EntityCondition build() {
            return condition;
        }

        public boolean isEmpty() {
            return condition.isEmpty();
        }

        public String makeWhereString(ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams, Datasource datasourceInfo) {
            return condition.makeWhereString(modelEntity, entityConditionParams, datasourceInfo);
        }

        public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
            condition.checkCondition(modelEntity);
        }

        public boolean mapMatches(Delegator delegator, Map<String, ? extends Object> map) {
            return condition.mapMatches(delegator, map);
        }

        public EntityCondition freeze() {
            return condition.freeze();
        }

        public int hashCode() {
            return condition.hashCode();
        }
        public boolean equals(Object obj) {
            return condition.equals(obj);
        }

    }

    @Override
    protected Object createNode(Object methodName) {
        String operatorName = ((String)methodName).toLowerCase();
        EntityJoinOperator operator = EntityOperator.lookupJoin(operatorName);
        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        return new ConditionHolder(EntityCondition.makeCondition(condList, operator));
    }

    @Override
    protected Object createNode(Object methodName, Object objArg) {
        Object node = createNode(methodName);
        setParent(node, objArg);
        return node;
    }

    @Override
    protected Object createNode(Object methodName, Map mapArg) {
        Map<String, Object> fieldValueMap = UtilGenerics.checkMap(mapArg);
        String operatorName = ((String)methodName).toLowerCase();
        EntityComparisonOperator<String, Object> operator = EntityOperator.lookupComparison(operatorName);
        List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
        for (Map.Entry<String, Object> entry : fieldValueMap.entrySet()) {
            conditionList.add(EntityCondition.makeCondition(entry.getKey(), operator, entry.getValue()));
        }
        if (conditionList.size() == 1) {
            return new ConditionHolder(conditionList.get(0));
        } else {
            return new ConditionHolder(EntityCondition.makeCondition(conditionList));
        }
    }

    @Override
    protected Object createNode(Object methodName, Map mapArg, Object objArg) {
        return null;
    }

    @Override
    protected void setParent(Object parent, Object child) {
        ConditionHolder holder = (ConditionHolder) parent;
        EntityConditionList<EntityCondition> parentConList = UtilGenerics.cast(holder.condition);
        Iterator<EntityCondition> iterator = parentConList.getConditionIterator();
        List<EntityCondition> tempList = new LinkedList<EntityCondition>();
        while (iterator.hasNext()) {
            tempList.add(iterator.next());
        }
        if (child instanceof EntityCondition) {
            tempList.add((EntityCondition)child);
        } else if (child instanceof ConditionHolder) {
            tempList.add(((ConditionHolder)child).condition);
        } else {
            tempList.addAll(UtilGenerics.<EntityCondition>checkList(child));
        }
        holder.condition = EntityCondition.makeCondition(tempList, parentConList.getOperator());
    }

}
