
package com.hanlin.fadp.entity.condition;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericModelException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.model.ModelEntity;

/**
 * Encapsulates a list of EntityConditions to be used as a single EntityCondition combined as specified
 *
 */
@SuppressWarnings("serial")
public abstract class EntityConditionListBase<T extends EntityCondition> extends EntityCondition {
    public static final String module = EntityConditionListBase.class.getName();

    protected final List<T> conditionList;
    protected final EntityJoinOperator operator;

    protected EntityConditionListBase(List<T> conditionList, EntityJoinOperator operator) {
        this.conditionList = conditionList;
        this.operator = operator;
    }

    public EntityJoinOperator getOperator() {
        return this.operator;
    }

    public T getCondition(int index) {
        return this.conditionList.get(index);
    }

    protected int getConditionListSize() {
        return this.conditionList.size();
    }

    protected Iterator<T> getConditionIterator() {
        return this.conditionList.iterator();
    }

    @Override
    public void visit(EntityConditionVisitor visitor) {
        visitor.acceptEntityJoinOperator(operator, conditionList);
    }

    @Override
    public boolean isEmpty() {
        return operator.isEmpty(conditionList);
    }

    @Override
    public String makeWhereString(ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams, Datasource datasourceInfo) {
        // if (Debug.verboseOn()) Debug.logVerbose("makeWhereString for entity " + modelEntity.getEntityName(), module);
        StringBuilder sql = new StringBuilder();
        operator.addSqlValue(sql, modelEntity, entityConditionParams, conditionList, datasourceInfo);
        return sql.toString();
    }

    @Override
    public void checkCondition(ModelEntity modelEntity) throws GenericModelException {
        // if (Debug.verboseOn()) Debug.logVerbose("checkCondition for entity " + modelEntity.getEntityName(), module);
        operator.validateSql(modelEntity, conditionList);
    }

    @Override
    public boolean mapMatches(Delegator delegator, Map<String, ? extends Object> map) {
        return operator.mapMatches(delegator, map, conditionList);
    }

    @Override
    public EntityCondition freeze() {
        return operator.freeze(conditionList);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EntityConditionListBase<?>)) return false;
        EntityConditionListBase<?> other = UtilGenerics.cast(obj);

        boolean isEqual = conditionList.equals(other.conditionList) && operator.equals(other.operator);
        //if (!isEqual) {
        //    Debug.logWarning("EntityConditionListBase.equals is false:\n this.operator=" + this.operator + "; other.operator=" + other.operator +
        //            "\nthis.conditionList=" + this.conditionList +
        //            "\nother.conditionList=" + other.conditionList, module);
        //}
        return isEqual;
    }

    @Override
    public int hashCode() {
        return conditionList.hashCode() + operator.hashCode();
    }
}
