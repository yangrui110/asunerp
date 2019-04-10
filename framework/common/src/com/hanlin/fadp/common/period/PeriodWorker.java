


package com.hanlin.fadp.common.period;

import java.sql.Timestamp;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityConditionList;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityOperator;

public class PeriodWorker {

    public static String module = PeriodWorker.class.getName();

    /**
     * Method to get a condition that checks that the given fieldName is in a given timePeriod.
     */
    public static EntityCondition getFilterByPeriodExpr(String fieldName, GenericValue timePeriod) {
        Timestamp fromDate;
        Timestamp thruDate;
        if (timePeriod.get("fromDate") instanceof Timestamp) {
            fromDate = timePeriod.getTimestamp("fromDate");
            thruDate = timePeriod.getTimestamp("thruDate");
        } else {
            fromDate = UtilDateTime.toTimestamp(timePeriod.getDate("fromDate"));
            thruDate = UtilDateTime.toTimestamp(timePeriod.getDate("thruDate"));
        }

        EntityConditionList<EntityExpr> betweenCondition = EntityCondition.makeCondition(
                    EntityCondition.makeCondition(fieldName, EntityOperator.GREATER_THAN, fromDate),
                    EntityCondition.makeCondition(fieldName, EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
        return EntityCondition.makeCondition(EntityCondition.makeCondition(fieldName, EntityOperator.NOT_EQUAL, null), betweenCondition);
    }
}
