
package com.hanlin.fadp.entity.condition;

import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of EntityConditions to be used as a single EntityCondition combined as specified
 *
 */
@SuppressWarnings("serial")
public class EntityConditionList<T extends EntityCondition> extends EntityConditionListBase<T> {
    public EntityConditionList(List<T> conditionList, EntityJoinOperator operator) {
        super(conditionList, operator);
    }

    @Override
    public int getConditionListSize() {
        return super.getConditionListSize();
    }

    @Override
    public Iterator<T> getConditionIterator() {
        return super.getConditionIterator();
    }

    @Override
    public void accept(EntityConditionVisitor visitor) {
        visitor.acceptEntityConditionList(this);
    }
}
