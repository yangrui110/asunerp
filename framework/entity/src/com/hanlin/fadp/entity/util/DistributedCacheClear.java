
package com.hanlin.fadp.entity.util;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericPK;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;

/**
 * Distributed Cache Clear interface definition
 */
public interface DistributedCacheClear {

    public void setDelegator(Delegator delegator, String userLoginId);

    public void distributedClearCacheLine(GenericValue value);

    public void distributedClearCacheLineFlexible(GenericEntity dummyPK);

    public void distributedClearCacheLineByCondition(String entityName, EntityCondition condition);

    public void distributedClearCacheLine(GenericPK primaryKey);

    public void clearAllCaches();
}
