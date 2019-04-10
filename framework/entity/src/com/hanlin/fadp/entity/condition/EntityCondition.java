package com.hanlin.fadp.entity.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.lang.IsEmpty;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericModelException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.model.ModelEntity;

/**
 * Represents the conditions to be used to constrain a query
 * <br/>An EntityCondition can represent various type of constraints, including:
 * <ul>
 *  <li>EntityConditionList: a list of EntityConditions, combined with the operator specified
 *  <li>EntityExpr: for simple expressions or expressions that combine EntityConditions
 *  <li>EntityFieldMap: a map of fields where the field (key) equals the value, combined with the operator specified
 * </ul>
 * These can be used in various combinations using the EntityConditionList and EntityExpr objects.
 *
 */
@SuppressWarnings("serial")
public abstract class EntityCondition extends EntityConditionBase implements IsEmpty {

	/**
	 * @Title: makeCondition 
	 * @Description: 构建查询条件表达式
	 * @param lhs 左操作数
	 * @param operator 操作符
	 * @param rhs 右操作数
	 * @return: EntityExpr 查询表达式
	 */
    public static <L,R,LL,RR> EntityExpr makeCondition(L lhs, EntityComparisonOperator<LL,RR> operator, R rhs) {
        return new EntityExpr(lhs, operator, rhs);
    }

    public static <R> EntityExpr makeCondition(String fieldName, R value) {
        return new EntityExpr(fieldName, EntityOperator.EQUALS, value);
    }

    public static EntityExpr makeCondition(EntityCondition lhs, EntityJoinOperator operator, EntityCondition rhs) {
        return new EntityExpr(lhs, operator, rhs);
    }

    public static <T extends EntityCondition> EntityConditionList<T> makeCondition(EntityJoinOperator operator, T... conditionList) {
        return new EntityConditionList<T>(Arrays.<T>asList(conditionList), operator);
    }

    public static <T extends EntityCondition> EntityConditionList<T> makeCondition(T... conditionList) {
        return new EntityConditionList<T>(Arrays.<T>asList(conditionList), EntityOperator.AND);
    }

    public static <T extends EntityCondition> EntityConditionList<T> makeCondition(List<T> conditionList, EntityJoinOperator operator) {
        return new EntityConditionList<T>(conditionList, operator);
    }

    public static <T extends EntityCondition> EntityConditionList<T> makeCondition(List<T> conditionList) {
        return new EntityConditionList<T>(conditionList, EntityOperator.AND);
    }

    public static <L,R> EntityFieldMap makeCondition(Map<String, ? extends Object> fieldMap, EntityComparisonOperator<L,R> compOp, EntityJoinOperator joinOp) {
        return new EntityFieldMap(fieldMap, compOp, joinOp);
    }

    public static EntityFieldMap makeCondition(Map<String, ? extends Object> fieldMap, EntityJoinOperator joinOp) {
        return new EntityFieldMap(fieldMap, EntityOperator.EQUALS, joinOp);
    }

    /**
     * @Title: makeCondition 
     * @Description: 构建简单查询条件，包含“=”操作符和and操作符
     * @param fieldMap 查询对象，成对出现
     * @return: EntityFieldMap
     */
    public static EntityFieldMap makeCondition(Map<String, ? extends Object> fieldMap) {
        return new EntityFieldMap(fieldMap, EntityOperator.EQUALS, EntityOperator.AND);
    }

    public static <L,R> EntityFieldMap makeCondition(EntityComparisonOperator<L,R> compOp, EntityJoinOperator joinOp, Object... keysValues) {
        return new EntityFieldMap(compOp, joinOp, keysValues);
    }

    public static EntityFieldMap makeCondition(EntityJoinOperator joinOp, Object... keysValues) {
        return new EntityFieldMap(EntityOperator.EQUALS, joinOp, keysValues);
    }

    public static EntityFieldMap makeConditionMap(Object... keysValues) {
        return new EntityFieldMap(EntityOperator.EQUALS, EntityOperator.AND, keysValues);
    }

    public static EntityDateFilterCondition makeConditionDate(String fromDateName, String thruDateName) {
        return new EntityDateFilterCondition(fromDateName, thruDateName);
    }

    public static EntityWhereString makeConditionWhere(String sqlString) {
        return new EntityWhereString(sqlString);
    }

    @Override
    public String toString() {
        return makeWhereString(null, new ArrayList<EntityConditionParam>(), null);
    }

    public void accept(EntityConditionVisitor visitor) {
        throw new IllegalArgumentException(getClass().getName() + ".accept not implemented");
    }

    abstract public String makeWhereString(ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams, Datasource datasourceInfo);

    abstract public void checkCondition(ModelEntity modelEntity) throws GenericModelException;

    public boolean entityMatches(GenericEntity entity) {
        return mapMatches(entity.getDelegator(), entity);
    }

    public Boolean eval(GenericEntity entity) {
        return eval(entity.getDelegator(), entity);
    }

    public Boolean eval(Delegator delegator, Map<String, ? extends Object> map) {
        return mapMatches(delegator, map) ? Boolean.TRUE : Boolean.FALSE;
    }

    abstract public boolean mapMatches(Delegator delegator, Map<String, ? extends Object> map);

    abstract public EntityCondition freeze();

    public void visit(EntityConditionVisitor visitor) {
        throw new IllegalArgumentException(getClass().getName() + ".visit not implemented");
    }
}
