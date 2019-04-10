package com.hanlin.fadp.workflow;

import java.io.IOException;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.common.UserUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;

import static com.hanlin.fadp.common.MapUtil.*;
/**
 * 流程定制工具类
 * @author 陈林
 *
 */
public class FlowWorker {
	public static String module=FlowWorker.class.getName();
	/**
	 * 定制流程
	 * @param delegator
	 * @param fields
	 * @return
	 * @throws IOException
	 * @throws GenericEntityException
	 */
	public static GenericValue createFlow(Delegator delegator,Map<String, Object> fields) throws IOException, GenericEntityException {
		GenericValue value = delegator.makeValidValue("FadpFlow", fields);
		value.put("createTime", UtilDateTime.nowTimestamp());
		putIfMiss(value, "hasAttachment", "Y");
		putIfMiss(value, "hasComment", "Y");
		value.put("userName", UserUtil.getUserByUserNameLoginId(delegator, value.getString("userId")));
		delegator.createSetNextSeqId(value);
		return value;
	}
}
