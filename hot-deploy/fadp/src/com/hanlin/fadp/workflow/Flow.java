package com.hanlin.fadp.workflow;

import static com.hanlin.fadp.common.MapUtil.putIfMiss;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.UserUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

public class Flow {
	public Delegator delegator = null;

	public static String entityName = "Flow";
	protected String flowId;
	public static Flow getFlow(Delegator delegator, String flowId) throws GenericEntityException {
		if (UtilValidate.isEmpty(flowId)) {
			return null;
		}
		return new Flow(delegator, UtilMisc.toMap("flowId", (Object)flowId));
	}

	public static Flow getFlow(GenericValue flow) throws GenericEntityException {
		return new Flow(flow.getDelegator(), flow);
	}
	public Flow(Delegator delegator, Map<String, Object> fields) throws  GenericEntityException {
		this.delegator = delegator;
		GenericValue value = delegator.makeValidValue("FadpFlow", fields);
		this.flowId = value.getString("flowId");
		if (UtilValidate.isEmail(flowId)) {
			value.put("createTime", UtilDateTime.nowTimestamp());
			putIfMiss(value, "hasAttachment", "Y");
			putIfMiss(value, "hasComment", "Y");
			value.put("userName", UserUtil.getUserByUserNameLoginId(delegator, value.getString("userId")));
			delegator.createSetNextSeqId(value);
			delegator.putInPrimaryKeyCache(value.getPrimaryKey(), value);
			this.flowId=value.getString("flowId");
		}
	}
	public GenericValue getFlow() throws GenericEntityException{
		return EntityQuery.use(delegator).from(entityName).cache().where(UtilMisc.toMap("flowId", (Object)flowId)).queryOne();
	}
	public String getFlowId() {
		return flowId;
	}
}
