package com.hanlin.fadp.workflow;

import static com.hanlin.fadp.common.MapUtil.putIfMiss;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.UserUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.util.EntityQuery;

/**
 * 流程实例
 * 
 * @author 陈林
 *
 */
public class FlowRun {
	private static Map<String, FlowRun> runMap = new ConcurrentHashMap<>();
	public Delegator delegator = null;
	public static String entityName = "FadpFlowRun";
	protected String runId;

	public static FlowRun getFlowRun(Delegator delegator, String runId) throws GenericEntityException {
		if (UtilValidate.isEmpty(runId)) {
			return null;
		}
		FlowRun flowRun = runMap.get(runId);
		if (flowRun != null) {
			return flowRun;
		}
		return new FlowRun(delegator, UtilMisc.toMap("runId", (Object) runId));
	}

	public static FlowRun getFlowRun(GenericValue flowRunValue) throws GenericEntityException {
		return getFlowRun(flowRunValue.getDelegator(), flowRunValue.getString("runId"));
	}

	/**
	 * 
	 * @param delegator
	 * @param fields
	 *            若runId为空则创建新的流程实例
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public FlowRun(Delegator delegator, Map<String, Object> fields) throws GenericEntityException {
		this.delegator = delegator;
		GenericValue value = delegator.makeValidValue("FadpFlowRun", fields);
		this.runId = value.getString("runId");
		if (UtilValidate.isEmail(runId)) {
			value.set("beginTime", UtilDateTime.nowTimestamp());
			putIfMiss(value, "delFlag", "N");
			value.set("userName", UserUtil.getUserByUserNameLoginId(delegator, value.getString("userId")));
			delegator.createSetNextSeqId(value);
			this.runId=value.getString("runId");
		}
	}

	/**
	 * 启动流程
	 * 
	 * @param context
	 *            流程上下文
	 * @return 
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public FlowRunPrcs start(Map<String, Object> context) throws GenericEntityException, GenericFadpFlowException {
		// 启动流程
		if (EntityQuery.use(delegator).from(FlowRunPrcs.entityName).where("runId",runId).queryCount() != 0) {
			throw new GenericFadpFlowException("流程不能重复开始");
		}

		FlowRunPrcs runPrcs = new FlowRunPrcs(delegator, UtilMisc.toMap("runId", runId, "userId", getRun().get("userId"), "prcsId", "1"));
		runPrcs.process(context);
		return runPrcs;
	}

	/**
	 * 检查流程是否结束
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean checkFinished() throws GenericEntityException {
		EntityCondition con = EntityCondition.makeCondition(UtilMisc.toMap("runId", runId));
		con = EntityCondition.makeCondition("prcsFlag", EntityOperator.NOT_EQUAL, FlowRunPrcs.State.F.toString());
		long count = FlowRunPrcs.queryRunPrcs(delegator, con).queryCount();
		if (count == 0L) {
			GenericValue run = delegator.makeValue(entityName, getRun().getPrimaryKey());
			run.set("endTime", UtilDateTime.nowTimestamp());
			run.store();
			return true;
		}
		return false;
	}

	public GenericValue getRun() throws GenericEntityException {
		return EntityQuery.use(delegator).from(entityName).cache().where(UtilMisc.toMap("runId", (Object) runId)).queryOne();
	}

	public String getRunId() {
		return runId;
	}

	// 获取流程编号
	public String getFlowId() throws GenericEntityException {
		return getRun().getString("flowId");
	}
}
