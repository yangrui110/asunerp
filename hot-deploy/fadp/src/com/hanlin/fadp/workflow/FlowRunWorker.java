package com.hanlin.fadp.workflow;

import java.sql.Timestamp;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.common.UserUtil;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

import static com.hanlin.fadp.common.MapUtil.*;

public class FlowRunWorker {
	public static String module=FlowRunWorker.class.getName();
	//处理标记（O(original)未接收；D(doing)办理中；R(recover)已转交但可回收；F(final)已转交且不可回收）
	public static String OL="O";
	public static String D="D";
	public static String R="R";
	public static String F="F";
	
	public static GenericValue createFlowRun(Delegator delegator,Map<String, Object> fields) throws GenericEntityException{
		GenericValue value = delegator.makeValidValue("FadpFlowRun", fields);
		value.set("createTime", UtilDateTime.nowTimestamp());
		putIfMiss(value, "delFlag", OL);
		value.set("userName", UserUtil.getUserByUserNameLoginId(delegator, value.getString("userId")));
		delegator.createSetNextSeqId(value);
		
		//TODO:产生流程第一步
		return value;
	}
	public static GenericValue nextPrcs(Delegator delegator,Map<String, Object> fields,String currentRunPrcsId) throws Exception{
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		GenericValue value = delegator.makeValidValue("FadpFlowRunPrcs", fields);
		if(UtilValidate.isNotEmpty(currentRunPrcsId)){
			GenericValue currentRunPrcs = EntityQuery.use(delegator).from("FadpFlowRunPrcs").where("runPrcsId",currentRunPrcsId).queryOne();
			if(currentRunPrcs==null){
				throw new Exception("流程上一步不存在"+value);
			}else{
				value.set("parentId", currentRunPrcsId);
				
				String currentPrcsId=currentRunPrcs.getString("prcsId");
				GenericValue chilePrcsList = EntityQuery.use(delegator).from("FadpFlowPrcs").where("parentPrcsId",currentPrcsId).queryOne();
				if(UtilValidate.isNotEmpty(chilePrcsList)){
					value.set("prcsFlag", R);
				}
				//更改上一步状态
				currentRunPrcs.set("deliverTime", nowTimestamp);
				currentRunPrcs.set("prcsFlag", R);
			}
			
		}
		
		value.set("createTime", nowTimestamp);
		putIfMiss(value, "delFlag", "N");
		value.set("userName", UserUtil.getUserByUserNameLoginId(delegator, value.getString("userId")));
		delegator.createSetNextSeqId(value);
		
		//TODO:产生流程第一步
		return value;
	}
	
}
