

package com.hanlin.fadp.entity.condition;

import java.util.List;

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
public interface EntityConditionVisitor {
    <T> void visit(T obj);
    <T> void accept(T obj);
    void acceptObject(Object obj);
    void acceptEntityCondition(EntityCondition condition);
    <T extends EntityCondition> void acceptEntityJoinOperator(EntityJoinOperator op, List<T> conditions);
    <L,R,T> void acceptEntityOperator(EntityOperator<L, R, T> op, L lhs, R rhs);
    <L,R> void acceptEntityComparisonOperator(EntityComparisonOperator<L, R> op, L lhs, R rhs);
    void acceptEntityConditionValue(EntityConditionValue value);
    void acceptEntityFieldValue(EntityFieldValue value);

    void acceptEntityExpr(EntityExpr expr);
    <T extends EntityCondition> void acceptEntityConditionList(EntityConditionList<T> list);
    void acceptEntityFieldMap(EntityFieldMap fieldMap);
    void acceptEntityConditionFunction(EntityConditionFunction func, EntityCondition nested);
    <T extends Comparable<?>> void acceptEntityFunction(EntityFunction<T> func);
    void acceptEntityWhereString(EntityWhereString condition);

    void acceptEntityDateFilterCondition(EntityDateFilterCondition condition);
}
