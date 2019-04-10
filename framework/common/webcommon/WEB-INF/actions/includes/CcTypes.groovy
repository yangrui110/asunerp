import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityOperator;

context.creditCardTypes = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CREDIT_CARD_TYPE"), 
        ["enumId", "enumCode"] as Set, null, null, false);