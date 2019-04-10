package com.hanlin.fadp.common;

import java.util.List;
import java.util.Map;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.ServiceUtil;

import javolution.util.FastMap;

public class UserUtil {
	public static Map<String, GenericValue> userMap = FastMap.newInstance();

	public static Map<String, Object> refreshUserMap(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		userMap = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> userList = EntityQuery.use(delegator).from("FadpUser").cache().queryList();
		if (userList != null) {
			for (GenericValue user : userList) {
				userMap.put(user.getString("userLoginId"), user);
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public static GenericValue getUserByUserLoginId(Delegator delegator, String userLoginId) throws GenericEntityException {
		if(userLoginId==null){
			userLoginId="";
		}
		GenericValue user = userMap.get(userLoginId);
		if (user == null) {
			user = EntityQuery.use(delegator).from("FadpUser").where("userLoginId",userLoginId).cache().queryOne();
			if (user != null) {
				userMap.put(user.getString("userLoginId"), user);
			}
		}
		return user;
	}
	public static String getUserByUserNameLoginId(Delegator delegator, String userLoginId) throws GenericEntityException {
		return getUserByUserLoginId(delegator, userLoginId).getString("userName");
	}

}
