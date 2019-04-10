
package com.hanlin.fadp.entity.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.util.EntityUtil;


public class EntityListCache extends AbstractEntityConditionCache<Object, List<GenericValue>> {

    public static final String module = EntityListCache.class.getName();

    public EntityListCache(String delegatorName) {
        super(delegatorName, "entity-list");
    }

    public List<GenericValue> get(String entityName, EntityCondition condition) {
        return this.get(entityName, condition, null);
    }

    public List<GenericValue> get(String entityName, EntityCondition condition, List<String> orderBy) {
        ConcurrentMap<Object, List<GenericValue>> conditionCache = getConditionCache(entityName, condition);
        if (conditionCache == null) return null;
        Object orderByKey = getOrderByKey(orderBy);
        List<GenericValue> valueList = conditionCache.get(orderByKey);
        if (valueList == null) {
            // the valueList was not found for the given ordering, so grab the first one and order it in memory
            Iterator<List<GenericValue>> it = conditionCache.values().iterator();
            if (it.hasNext()) valueList = it.next();

            if (valueList != null) {
                // Does not need to be synchronized; if 2 threads do the same ordering,
                // the result will be exactly the same, and won't actually cause any
                // incorrect results.
                valueList = EntityUtil.orderBy(valueList, orderBy);
                conditionCache.put(orderByKey, valueList);
            }
        }
        return valueList;
    }

    public void put(String entityName, EntityCondition condition, List<GenericValue> entities) {
        this.put(entityName, condition, null, entities);
    }

    public List<GenericValue> put(String entityName, EntityCondition condition, List<String> orderBy, List<GenericValue> entities) {
        ModelEntity entity = this.getDelegator().getModelEntity(entityName);
        if (entity.getNeverCache()) {
            Debug.logWarning("Tried to put a value of the " + entityName + " entity in the cache but this entity has never-cache set to true, not caching.", module);
            return null;
        }
        for (GenericValue memberValue : entities) {
            memberValue.setImmutable();
        }
        Map<Object, List<GenericValue>> conditionCache = getOrCreateConditionCache(entityName, getFrozenConditionKey(condition));
        return conditionCache.put(getOrderByKey(orderBy), entities);
    }

    public List<GenericValue> remove(String entityName, EntityCondition condition, List<String> orderBy) {
        return super.remove(entityName, condition, getOrderByKey(orderBy));
    }

    public static final Object getOrderByKey(List<String> orderBy) {
        return orderBy != null ? (Object) orderBy : "{null}";
    }
}
