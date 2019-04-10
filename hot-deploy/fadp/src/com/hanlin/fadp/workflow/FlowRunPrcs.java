package com.hanlin.fadp.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ScriptUtil;
import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.EntityUtil;
import com.hanlin.fadp.common.MapUtil;
import com.hanlin.fadp.common.UserUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityConditionList;
import com.hanlin.fadp.entity.condition.EntityFieldMap;
import com.hanlin.fadp.entity.condition.EntityJoinOperator;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.util.EntityQuery;

//import static com.hanlin.fadp.workflow.FlowConst.*;
import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 流程实例中的步骤</br>
 * 要注意的是:<br>
 * 1.流程实例中的步骤存在反复的情况，导致流程定义中的一步可以在流程实例中执行多次。</br>
 * 2.抢办活动和回收活动的时候存在删除活动的情况
 * 
 * @author 陈林
 *
 */
public class FlowRunPrcs {
	public static final String module = FlowRunPrcs.class.getName();

	/***
	 * 处理标记（O(original)未接收；D(doing)办理中；R(recover)已转交但可回收；F(final)已转交且不可回收）
	 * 
	 * @author 陈林
	 *
	 */
	public enum State {
		OL("O", 1), D("D", 2), R("R", 3), F("F", 4);
		private final String value;
		private final int weight;

		State(String value, int weight) {
			this.value = value;
			this.weight = weight;
		}

		public String toString() {
			return value;
		}

		public boolean greaterThen(State state) {
			return state.weight > this.weight;
		}

		public static State getState(String str) {
			State[] values = State.values();
			for (State state : values) {
				if (state.value.equals(str)) {
					return state;
				}
			}
			return null;
		}
	}

	public Delegator delegator = null;
	public FlowRun run;
	public String runId;
	public String prcsId;
	public String runPrcsId;

	public static String entityName = "FadpFlowRunPrcs";

	/**
	 * 获得活动
	 * 
	 * @see #FlowRunPrcs(Delegator, Map)
	 * @param delegator
	 * @param runId
	 * @param runPrcsId
	 * @return
	 * @throws GenericEntityException
	 */
	public static FlowRunPrcs getRunPrcs(Delegator delegator, String runId, String runPrcsId) throws GenericEntityException {
		if (UtilValidate.isEmpty(runPrcsId)) {
			return null;
		}
		return new FlowRunPrcs(delegator, UtilMisc.toMap("runId", runId, "runPrcsId", (Object) runPrcsId));
	}

	/**
	 * 获得活动
	 * 
	 * @see #FlowRunPrcs(Delegator, Map)
	 * @param runPrcs
	 * @return
	 * @throws GenericEntityException
	 */
	public static FlowRunPrcs getRunPrcs(GenericValue runPrcs) throws GenericEntityException {
		return new FlowRunPrcs(runPrcs.getDelegator(), runPrcs);
	}

	/**
	 * 获取流程定义中的一步对应的流程实例中的步骤</br>
	 * 只获取为被标记删除的步骤
	 * 
	 * @param delegator
	 * @param runId
	 * @param prcsId
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<FlowRunPrcs> getRunPrcsListByPrcsId(Delegator delegator, String runId, String prcsId) throws GenericEntityException {
		List<GenericValue> list = queryRunPrcs(delegator, "runId", runId, "prcsId", prcsId).queryList();
		return getRunPrcsList(list);
	}

	/**
	 * 将数据库实体批量转换为FlowRunPrcs对象
	 * 
	 * @param list
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<FlowRunPrcs> getRunPrcsList(List<GenericValue> list) throws GenericEntityException {
		if (UtilValidate.isEmpty(list))
			return null;
		Delegator delegator = list.get(0).getDelegator();
		EntityUtil.cacheAllEntity(delegator, entityName, list);
		List<FlowRunPrcs> newList = FastList.newInstance();
		for (GenericValue flowRun : list) {
			newList.add(getRunPrcs(flowRun));
		}
		return newList;
	}

	/**
	 * 构造流程实例步骤
	 * 
	 * @param delegator
	 * @param context
	 *            其中的runId，prcsId不可为空.</br>
	 *            若runPrcsId为空则创建新的（且runPrcsId为本流程内的累加计数值），否则从数据库中获取
	 * @throws GenericEntityException
	 */
	protected FlowRunPrcs(Delegator delegator, Map<String, Object> context) throws GenericEntityException {
		this.delegator = delegator;
		GenericValue value = delegator.makeValidValue(entityName, context);
		this.runId = value.getString("runId");
		this.prcsId = value.getString("prcsId");
		this.runPrcsId = value.getString("runPrcsId");
		run = FlowRun.getFlowRun(delegator, runId);
		if (UtilValidate.isEmpty(runPrcsId)) {
			// 填充实例内计数器runPrcsId
			long prcsIdCount = queryRunPrcs("runId", runId).queryCount();
			if (prcsIdCount < 1) {
				prcsIdCount = 0;
			}
			runPrcsId = (prcsIdCount + 1) + "";
			value.put("runPrcsId", runPrcsId);

			value.put("createTime", UtilDateTime.nowTimestamp());

			// 填充活动办理人信息，注意这里必须保存活动办理时人员的信息，因为人员可能改变部门
			GenericValue user = UserUtil.getUserByUserLoginId(delegator, value.getString("userId"));
			value.setString("userName", user.getString("userName"));
			value.setString("deptId", user.getString("deptId"));
			value.setString("deptName", user.getString("deptName"));
			// 填充默认值
			MapUtil.putIfMiss(value, "prcsFlag", State.OL.value);
			MapUtil.putIfMiss(value, "delFlag", "N");
			MapUtil.putIfMiss(value, "isSponsor", "Y");
			delegator.create(value);
			delegator.putInPrimaryKeyCache(value.getPrimaryKey(), value);

		} else {
			if (UtilValidate.isEmpty(prcsId)) {
				this.prcsId = getRunPrcs().getString("prcsId");
			}
		}
	}

	/**
	 * 获取流程步骤实体
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public GenericValue getRunPrcs() throws GenericEntityException {
		GenericValue runPrcs = queryRunPrcs("runId", runId, "runPrcsId", runPrcsId).queryOne();
		return runPrcs;
	}

	public FlowPrcs getFlowPrcs() throws GenericEntityException {
		return FlowPrcs.getFlowPrcs(delegator, run.getFlowId(), prcsId);
	}

	/**
	 * 获取上一步流程
	 * 
	 * @return
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public FlowRunPrcs getPrevRunPrcs() throws GenericEntityException, GenericFadpFlowException {
		if (isFirstPrcs()) {
			throw new GenericFadpFlowException("当前活动为第一步，没有上一步活动");
		}
		GenericValue runPrcs = queryRunPrcs("runId", runId, "runPrcsId", getRunPrcs().getString("parent")).queryOne();
		return getRunPrcs(runPrcs);
	}
	public FlowRunPrcs findParentByPrcsId(String prcsId) throws GenericEntityException, GenericFadpFlowException{
		if(isFirstPrcs()){
			return null;
		}
		FlowRunPrcs prevRunPrcs = getPrevRunPrcs();
		if(prevRunPrcs.prcsId.equals(prcsId)){
			return prevRunPrcs;
		}
		return findParentByPrcsId(prevRunPrcs.prcsId);
	}
	public List<FlowRunPrcs> getAllParent() throws GenericEntityException, GenericFadpFlowException{
		List<FlowRunPrcs> list=FastList.newInstance();
		if(!isFirstPrcs()){
			FlowRunPrcs prevRunPrcs = getPrevRunPrcs();
			list.addAll(prevRunPrcs.getAllParent());
			list.add(prevRunPrcs);
		}
		return list;
	}
	/**
	 * 获取下一活动
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public List<FlowRunPrcs> getNextRunPrcs() throws GenericEntityException {
		List<GenericValue> runPrcsList = queryRunPrcs("runId", runId, "parent", runPrcsId).queryList();
		if (runPrcsList == null) {
			return null;
		}
		List<FlowRunPrcs> newList = FastList.newInstance();
		for (GenericValue runPrcs : runPrcsList) {
			FlowRunPrcs prcs = getRunPrcs(runPrcs);
			newList.add(prcs);
		}
		return newList;
	}

	/**
	 * 执行活动</br>
	 * 涉及两种情况：1.协同办公，2.抢办</br>
	 * 主办人抢到任务，删除其他主办人的待办
	 * 
	 * @param context
	 *            其中包含 评论 附件
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public void process(Map<String, Object> context) throws GenericEntityException, GenericFadpFlowException {
		GenericValue value = delegator.makeValidValue(entityName, context);
		GenericValue oldRunPrcs = getRunPrcs();
		value.setPKFields(oldRunPrcs.getPrimaryKey());
		value.set("editTime", UtilDateTime.nowTimestamp());
		if (getState() == State.OL) {// 表示未接收状态
			if (!isFirstPrcs()) {
				getPrevRunPrcs().changeState(State.F);
			}
			value.setString("prcsFlag", State.D.toString());
			if (isSponsor() && getFlowPrcs().isFCFS()) {// 主办人抢到任务，删除其他主办人的待办
				List<GenericValue> brothersEntityValue = getBrothersEntityValue(false);
				List<EntityCondition> conList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(brothersEntityValue)) {
					for (GenericValue brother : brothersEntityValue) {
						conList.add(EntityCondition.makeCondition(brother.getPrimaryKey()));
					}
					delegator.storeByCondition(entityName, UtilMisc.toMap("delFlag", "Y"), EntityCondition.makeCondition(conList, EntityJoinOperator.OR));
				}
			}
		}
		value.store();

		if (context != null) {
			// 处理附件
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) context.get("attachmentList");
			if (UtilValidate.isNotEmpty(attachmentList)) {
				// TODO:处理附件
			}
		}

	}

	/**
	 * 流程向前推进</br>
	 * 这里关心的就是下一步要到哪里去，以及下一步任务要发给谁
	 * 
	 * @param nextFlowPrcs
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public void forward(FlowPrcs nextFlowPrcs, List<String> sponsorList, List<String> agentList) throws GenericEntityException, GenericFadpFlowException {
		// 若没有调用过process方法则默认改变上一活动状态
		if (getState() == State.OL) {// 表示未接收状态
			if (!isFirstPrcs()) {
				getPrevRunPrcs().changeState(State.F);
			}
		}
		if (getFlowPrcs().isLast()) {
			if (getState() != State.R) {

				changeState(State.R);
			}
			changeState(State.F);
			run.checkFinished();
			return;
		}
		if (UtilValidate.isEmpty(sponsorList)) {
			throw new GenericFadpFlowException("流程下一步主办人员为空");
		}
		// 先验证接收人是否在流程定义中的接收人范围内
		List<GenericValue> fadpFlowPrcsUserList = nextFlowPrcs.getFlowPrcsUserList(null);
		Map<String, Map<String, Object>> userMap = FastMap.newInstance();
		Set<String> userIdSet = UtilMisc.toSet(userMap.keySet());
		if (agentList != null) {// 先经办后主办，防止经办覆盖主办
			for (String userId : agentList) {
				userMap.put(userId, UtilMisc.toMap("userId", (Object) userId, "isSponsor", false));
			}
		}
		for (String userId : sponsorList) {
			userMap.put(userId, UtilMisc.toMap("userId", (Object) userId, "isSponsor", true));
		}
		if (UtilValidate.isNotEmpty(fadpFlowPrcsUserList)) {
			for (GenericValue fadpPrcsUser : fadpFlowPrcsUserList) {
				String userId = fadpPrcsUser.getString("userId");
				Map<String, Object> user = userMap.get(userId);
				if (user != null && fadpPrcsUser.getBoolean("isSponsor") == user.get("isSponsor")) {
					userIdSet.remove(userId);
				}
			}
			if (userIdSet.size() != 0) {
				throw new GenericFadpFlowException("流程下一步接收人员存在非法情况。以下接收人员不在流程定制中指定的接收人员范围内：" + userIdSet);
			}
		}

		if (isSponsor()) {// 当前活动是主办人的活动
			// 检查经办流程是否完成
			if (!isAgentFinished()) {
				throw new GenericFadpFlowException("尚有经办流程为完成");
			}
			// 使经办活动不可回收
			EntityFieldMap con = EntityCondition.makeCondition(UtilMisc.toMap("runId", runId, "prcsId", prcsId, "isSponsor", "N"));
			delegator.storeByCondition(entityName, UtilMisc.toMap("prcsFlag", State.F.value), con);
			// 创建下一步流程
			for (Map<String, Object> user : userMap.values()) {
				new FlowRunPrcs(delegator,
						UtilMisc.toMap("runId", runId, "userId", user.get("userId"), "isSponsor", (boolean) user.get("isSponsor") ? "Y" : "N", "parent", runPrcsId, "prcsId", (Object) nextFlowPrcs.prcsId));
			}
			// TODO:应该判断流程是否可以自动运行到下一步

		} else {// 当前活动是经办人的活动
			// TODO:改变当前经办活动状态

		}
		// 改变当前流程状态这里即适用主办活动，又适用经办活动
		if (getFlowPrcs().canRecover()) {// 可以回收的步骤
			changeState(State.R);
		} else {
			changeState(State.F);// 不可回收的步骤
		}

	}

	/**
	 * 流程回收（这是一个主动的行为）
	 * 
	 * @throws GenericEntityException
	 */
	public void recover() throws GenericEntityException {
		// 标记删除下一活动
		delegator.storeByCondition(entityName, UtilMisc.toMap("delFlag", "Y"), EntityCondition.makeCondition(UtilMisc.toMap("runId", runId, "parent", runPrcsId)));
		// 使经办活动可回收
		EntityFieldMap con = EntityCondition.makeCondition(UtilMisc.toMap("runId", runId, "prcsId", prcsId, "isSponsor", "N"));
		delegator.storeByCondition(entityName, UtilMisc.toMap("prcsFlag", State.R.value), con);
		// 改变当前活动状态为正在进行中
		changeState(State.D);
	}

	/**
	 * 流程后退一步（这是一个被动的行为）</br>
	 * 流程后退就比较有意思了。在表现上是回退了，而在程序实现中我们却要采用流程前进的思路。即不改变上一实例活动的状态，转而向前推进一步流程
	 * ，只不过这个新的实例活动的执行人还是上一实例活动的执行人,但是要注意主办与经办
	 * 
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public void backward() throws GenericEntityException, GenericFadpFlowException {
		backward(getPrevRunPrcs());
	}

	/**
	 * 回退到指定实例活动。（这是一个被动的行为）
	 * 
	 * @param runPrcs
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public void backward(FlowRunPrcs runPrcs) throws GenericEntityException, GenericFadpFlowException {
		FlowRunPrcs prevRunPrcs = getPrevRunPrcs();
		// 此时表明上一活动状态为State.R，所以更新上一实例活动状态为State.F
		if (getState() == State.OL) {
			prevRunPrcs.changeState(State.F);
		}
		// 先改变当前实例活动的状态为State.F
		changeState(State.F);
		// 新建主办活动
		GenericValue runPrcsValue = runPrcs.getRunPrcs();
		Map<String, Object> map = UtilMisc.toMap("runId", runId, "prcsId", runPrcs.prcsId, "parent", runPrcsId, "userId", runPrcsValue.getString("userId"), "isSponsor", "Y");
		new FlowRunPrcs(delegator, map);
		// 新建经办活动
		EntityFieldMap con = EntityCondition.makeCondition(UtilMisc.toMap("runId", runId, "prcsId", runPrcs.prcsId, "isSponsor", "N"));
		List<GenericValue> agentList = queryRunPrcs(con).queryList();
		for (GenericValue agent : agentList) {
			String userId = agent.getString("userId");
			Map<String, Object> newMap = FastMap.newInstance();
			newMap.putAll(map);
			newMap.put("userId", userId);
			newMap.put("isSponsor", "N");
			new FlowRunPrcs(delegator, newMap);
		}

	}

	/**
	 * 获取可以进入的下一活动 <br>
	 * 考虑下一步是有分支,有分支有分两种情况 :<br>
	 * 1.当前节点是个有权选择分支的节点 <br>
	 * 2.当前节点是一个被回退任务的分支节点，它无权选择分支，只能流向该节点的父节点对应的原分支，或者当前节点是一个没有分支的节点
	 * 
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public List<FlowPrcs> getNextAccessibleFlowPrcs(Map<String, Object> context) throws GenericEntityException, GenericFadpFlowException {

		List<FlowPrcs> nextFlowPrcsList = getFlowPrcs().getNextFlowPrcs();
		Map<String, FlowPrcs> map = FastMap.newInstance();
		for (FlowPrcs flowPrcs : nextFlowPrcsList) {
			map.put(flowPrcs.getPrcsId(), flowPrcs);
		}
		// 对应第一种情况
		if (!"1".equals(runPrcsId)) {
			List<FlowRunPrcs> allParent = getAllParent();
			for (FlowRunPrcs parentFlowRunPrcs : allParent) {
				String parentPrcsId = parentFlowRunPrcs.getPrcsId();
				if (map.containsKey(parentPrcsId)) {
					return UtilMisc.toList(map.get(parentPrcsId));
				}
			}
			findParentByPrcsId(prcsId);
			FlowPrcs prevFlowPrcs = getPrevRunPrcs().getFlowPrcs();
			if (map.containsKey(prevFlowPrcs.getPrcsId())) {
				return UtilMisc.toList(prevFlowPrcs);
			}
		}

		// 对应第二种情况
		List<GenericValue> nextRunPrcsListValue = queryRunPrcs(delegator, "runId", runId, "parent", runPrcsId).queryList();
		if (nextRunPrcsListValue != null) {
			for (GenericValue runPrcsValue : nextRunPrcsListValue) {
				map.remove(runPrcsValue.getString("prcsId"));
			}
		}

		List<FlowPrcs> newList = FastList.newInstance();

		for (FlowPrcs prcs : map.values()) {
			String enterCondition = prcs.getPrcs().getString("enterCondition");
			try {
				if (UtilValidate.isEmpty(enterCondition) || (boolean) ScriptUtil.evaluate("groovy", enterCondition, null, context)) {
					newList.add(prcs);
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		}
		return newList;

	}

	/**
	 * 判断当前实例活动执行人是否为该活动主办人
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean isSponsor() throws GenericEntityException {
		return getRunPrcs().getBoolean("isSponsor");
	}

	/**
	 * 判断当前主办活动相关的经办活动是否完成
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean isAgentFinished() throws GenericEntityException {
		EntityCondition con = EntityCondition.makeCondition(UtilMisc.toMap("runId", runId, "prcsId", prcsId, "isSponsor", "N"));
		con = EntityCondition.makeCondition(con, EntityJoinOperator.AND, EntityCondition.makeCondition("runPrcsId", EntityOperator.IN, UtilMisc.toList(State.OL.value, State.D.value)));

		long count = queryRunPrcs(con).queryCount();
		if (count == 0L) {
			return true;
		}
		return false;

	}

	/**
	 * 判断当前是否为第一步活动
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean isFirstPrcs() throws GenericEntityException {
		return "1".equals(runPrcsId);
	}

	public State getState() throws GenericEntityException {
		return State.getState(getRunPrcs().getString("prcsFlag"));
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getPrcsId() {
		return prcsId;
	}

	public void setPrcsId(String prcsId) {
		this.prcsId = prcsId;
	}

	public String getRunPrcsId() {
		return runPrcsId;
	}

	public void setRunPrcsId(String runPrcsId) {
		this.runPrcsId = runPrcsId;
	}

	/**
	 * 改变流程状态
	 * 
	 * @param state
	 * @throws GenericEntityException
	 */
	public void changeState(State state) throws GenericEntityException {
		GenericValue value = delegator.makeValue(entityName, getRunPrcs().getPrimaryKey());
		value.set("prcsFlag", state.value);
		if (state == State.R) {
			value.set("deliverTime", UtilDateTime.nowTimestamp());
		} else if (state == State.D) {
			value.set("prcsTime", UtilDateTime.nowTimestamp());
		}
		value.setString("prcsFlag", state.toString());
		value.store();
	}

	/**
	 * 获取活动数据库兄弟实体</br>
	 * 有3种兄弟：1.主办兄弟(主办兄弟是要被删除的，应为主办只能有一个)，2.协办兄弟
	 * 
	 * @param getAllBrother
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getBrothersEntityValue(boolean getAllBrother) throws GenericEntityException {
		if (getFlowPrcs().isFCFS()) {
			return null;
		}
		Map<String, String> conMap = UtilMisc.toMap("runId", runId, "prcsId", prcsId);
		if (!getAllBrother) {
			conMap.put("isSponsor", isSponsor() ? "Y" : "N");
		}
		EntityCondition con = EntityCondition.makeCondition(conMap);
		con = EntityCondition.makeCondition(con, EntityJoinOperator.AND, EntityCondition.makeCondition("runPrcsId", EntityOperator.NOT_EQUAL, runPrcsId));
		List<GenericValue> list = queryRunPrcs(con).queryList();
		return list;
	}

	public EntityQuery queryRunPrcs(Object... fields) {
		return queryRunPrcs(delegator, fields);
	}

	public EntityQuery queryRunPrcs(EntityCondition condition) {
		return queryRunPrcs(delegator, condition);
	}

	public static EntityQuery queryRunPrcs(Delegator delegator, Object... fields) {
		return queryRunPrcs(delegator, EntityCondition.makeCondition(UtilMisc.toMap(fields)));
	}

	public static EntityQuery queryRunPrcs(Delegator delegator, EntityCondition condition) {
		EntityConditionList<EntityCondition> conditionList = EntityCondition.makeCondition(UtilMisc.toList(condition, EntityCondition.makeCondition("delFlag", "N")));
		return EntityQuery.use(delegator).from(entityName).cache().where(conditionList);
	}

}
