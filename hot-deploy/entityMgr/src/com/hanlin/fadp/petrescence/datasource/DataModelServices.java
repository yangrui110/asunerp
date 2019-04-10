package com.hanlin.fadp.petrescence.datasource;

import java.util.List;
import java.util.Map;
import java.util.Set;


import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.common.EntityUtil;
import com.hanlin.fadp.common.EntityUtil.MapConditionParser;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.service.DispatchContext;

public class DataModelServices {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDataByModelService(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		try {
			TransactionUtil.commit();
		} catch (GenericTransactionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return UtilMisc.toMap("error", e1.getMessage());
		}
		Delegator delegator = DelegatorFactory.getDelegator("savemodel");
		String entityName = UtilGenerics.cast(context.get("saveModelId"));
		MapConditionParser parser = new EntityUtil.MapConditionParser(delegator,entityName);
		EntityCondition condition = parser.parseToCondition((Map<String, Object>) context.get("condition"));
		List<GenericValue> list = EntityQuery.use(delegator).from(entityName).select((Set<String>) context.get("selectField")).where(condition).queryList();
		return UtilMisc.toMap("dataList",list);
	}
}
