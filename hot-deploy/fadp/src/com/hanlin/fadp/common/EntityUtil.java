package com.hanlin.fadp.common;

import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityComparisonOperator;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityJoinOperator;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;

import javolution.util.FastList;

/**
 * 实体工具类
 * 
 * @author 陈林
 *
 */
public class EntityUtil {

	/**
	 * map形式的condition 转为EntityCondition
	 * 
	 * @author 陈林
	 *
	 */
	public static class MapConditionParser {
		Delegator delegator;
		String entityName;
		List<ModelField> fieldList;

		public MapConditionParser(Delegator delegator, String entityName) {
			this.delegator = delegator;
			this.entityName = entityName;
			ModelEntity modelEntity = delegator.getModelEntity(entityName);
			this.fieldList = modelEntity.getFieldsUnmodifiable();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public EntityCondition parseToCondition(Map<String, Object> map) {
			if (map == null) {
				return null;
			}
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("conditionList");
			EntityOperator operator = EntityOperator.lookup((String) map.get("operator"));
			if (list != null) {
				List<EntityCondition> conditionList = FastList.newInstance();
				for (Map<String, Object> childMap : list) {
					EntityCondition condition = parseToCondition(childMap);
					if (condition != null) {
						conditionList.add(condition);
					}
				}
				return EntityCondition.makeCondition(conditionList, (EntityJoinOperator) operator);
			} else {
				String lhs = (String) map.get("lhs");
				Object rhs = map.get("rhs");
				if (UtilValidate.isEmpty(lhs)) {
					return null;
				}
				if (UtilValidate.isEmpty(rhs)) {
					// rhs = "%";
					return null;
				}
				GenericValue value = delegator.makeValue(entityName);
				if (rhs instanceof String) {

					value.setString(lhs, rhs.toString());
				} else {
					value.set(lhs, rhs);
				}
				rhs = value.get(lhs);

				EntityCondition condition = new EntityExpr(lhs, (EntityComparisonOperator) operator, rhs);
				return condition;
			}
		}
	}

	/**
	 * 保证一组实体被缓存后，这一组实体中的每个实体都可以通过缓存的方式查询出来
	 * @param delegator
	 * @param entityName
	 * @param entities
	 */
	public static void cacheAllEntity(Delegator delegator, String entityName,  List<GenericValue> entities) {
		for (GenericValue v : entities) {
			List<String> orderBy=null;
			delegator.getCache().put(entityName, EntityCondition.makeCondition(v.getPrimaryKey()), orderBy, UtilMisc.toList(v));
			delegator.putAllInPrimaryKeyCache(entities);
		}
	}
}
