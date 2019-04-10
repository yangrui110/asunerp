

package com.hanlin.fadp.entity.cache;

import com.hanlin.fadp.entity.condition.EntityCondition;

public class EntityObjectCache extends AbstractEntityConditionCache<String, Object> {

    public static final String module = EntityObjectCache.class.getName();

    public EntityObjectCache(String delegatorName) {
        super(delegatorName, "object-list");
    }

    @Override
    public Object get(String entityName, EntityCondition condition, String name) {
        return super.get(entityName, condition, name);
    }

    @Override
    public Object put(String entityName, EntityCondition condition, String name, Object value) {
        return super.put(entityName, getFrozenConditionKey(condition), name, value);
    }

    @Override
    public Object remove(String entityName, EntityCondition condition, String name) {
        return super.remove(entityName, condition, name);
    }
}
