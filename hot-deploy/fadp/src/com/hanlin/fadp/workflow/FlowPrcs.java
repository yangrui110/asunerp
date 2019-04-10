package com.hanlin.fadp.workflow;

import static com.hanlin.fadp.common.MapUtil.putIfMiss;

import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.EntityUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

import javolution.util.FastList;

/**
 * 流程步骤
 * 
 * @author 陈林
 *
 */
public class FlowPrcs {
	public Delegator delegator = null;
	public static String entityName = "FadpFlowPrcs";
	protected String flowId;
	protected String prcsId;

	/**
	 * 获取流程步骤对象
	 * 
	 * @param delegator
	 * @param flowId
	 *            流程id
	 * @param prcsId
	 *            步骤id
	 * @return 流程步骤对象
	 * @throws GenericEntityException
	 */
	public static FlowPrcs getFlowPrcs(Delegator delegator, String flowId, String prcsId) throws GenericEntityException {
		if (UtilValidate.isEmpty(prcsId)) {
			return null;
		}
		return new FlowPrcs(delegator, UtilMisc.toMap("flowId", flowId, "prcsId", (Object) prcsId));
	}

	public static FlowPrcs getFlowPrcs(GenericValue flowPrcs) throws GenericEntityException {
		return new FlowPrcs(flowPrcs.getDelegator(), flowPrcs);
	}

	/**
	 * 创建流程步骤对象
	 * 
	 * @param delegator
	 * @param context
	 *            如果context中不存在prcsId，则创建新的流程步骤（且prcsId为计数累加的值,从1开始）,
	 *            否则从数据库中查询出流程步骤
	 * @throws GenericEntityException
	 */
	public FlowPrcs(Delegator delegator, Map<String, Object> context) throws GenericEntityException {
		this.delegator=delegator;
		// super(delegator, context);
		GenericValue value = delegator.makeValidValue(entityName, context);
		this.prcsId = value.getString("prcsId");
		this.flowId = value.getString("flowId");
		if (UtilValidate.isEmpty(prcsId)) {
			long prcsIdCount = queryPrcs().where("flowId", flowId).queryCount();
			if (prcsIdCount < 1) {
				prcsIdCount = 0;
			}
			prcsId = (prcsIdCount+1) + "";
			value.put("prcsId", prcsId);
			value.put("createTime", UtilDateTime.nowTimestamp());
			putIfMiss(value, "fcfs", "Y");// 默认是抢办的流程任务
			putIfMiss(value, "canRecover", "Y");// 默认是抢办的流程任务
			delegator.create(value);
			delegator.putInPrimaryKeyCache(value.getPrimaryKey(), value);
		}

	}

	/**
	 *  * 获取流程下一步
	 * 
	 *           
	 * @param context  判断流程进入下一步分支的上下文环境  ,其中必须有runId
	 * @return 返回可以进入的下一步流程
	 * @throws GenericEntityException
	 * @throws GenericFadpFlowException
	 */
	public List<FlowPrcs> getNextFlowPrcs() throws GenericEntityException, GenericFadpFlowException {
		
		List<GenericValue> childPrcsList = queryPrcs().where("flowId", flowId, "parentPrcsId", prcsId).queryList();
		if (UtilValidate.isEmpty(childPrcsList)) {
			return null;
		}
		EntityUtil.cacheAllEntity(delegator, entityName, childPrcsList);
		List<FlowPrcs> newList = FastList.newInstance();
		for (GenericValue childPrcs : childPrcsList) {
			FlowPrcs flowPrcs = getFlowPrcs(childPrcs);
				newList.add(flowPrcs);
		}
		return newList;
	}

	/**
	 * 获取上一步流程
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public FlowPrcs getPrevFlowPrcs() throws GenericEntityException {
		GenericValue flowPrcs = queryPrcs().where("flowId", flowId, "prcsId", getPrcs().getString("parentPrcsId")).queryOne();
		return getFlowPrcs(flowPrcs);
	}


	/**
	 * 获取可以参与当前步骤的用户视图数据
	 * 
	 * @param isSponsor
	 *            区分主办人组与经办人组
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getFlowPrcsUserList(Boolean isSponsor) throws GenericEntityException {
		Map<String, String> map = UtilMisc.toMap("flowId", flowId, "prcsId", prcsId);
		if (isSponsor != null) {
			map.put("isSponsor", isSponsor ? "Y" : "N");
		}
		List<GenericValue> userList = EntityQuery.use(delegator).from("FadpFlowPrcsUserToFadpUser").cache().where(map).queryList();
		return userList;
	}

	/**
	 * 获取流程步骤实体
	 * 
	 * @throws GenericEntityException
	 */
	public GenericValue getPrcs() throws GenericEntityException {
		return queryPrcs().where("flowId", flowId, "prcsId", prcsId).cache().queryOne();
	}

	public String getFlowId() {
		return flowId;
	}

	public String getPrcsId() {
		return prcsId;
	}

	/**
	 * 该步骤是否抢办</br>
	 * fcfs--First Come First Served
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean isFCFS() throws GenericEntityException {
		return getPrcs().getBoolean("fcfs");
	}

	/**
	 * 是否可回收（最后一步一定不可回收）
	 * 
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean canRecover() throws GenericEntityException {
		return getPrcs().getBoolean("canRecover");
	}
	/**
	 * 判断给活动的下一活动是否为结束
	 * @return
	 * @throws GenericEntityException
	 */
	public boolean isLast() throws GenericEntityException{
		return queryPrcs().where("flowId", flowId, "parentPrcsId", prcsId).queryCount()==0L;
	}
	private EntityQuery queryPrcs() {
		return queryPrcs(delegator);
	}
	public static EntityQuery queryPrcs(Delegator delegator){
		return EntityQuery.use(delegator).from(entityName).cache(); 
	}
}
