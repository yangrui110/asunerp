
package com.hanlin.fadp.entity.eca;

import java.util.List;
import java.util.Map;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericEntityException;

/**
 * EntityEcaHandler interface
 *
 */
public interface EntityEcaHandler<T> {

    public static final String EV_VALIDATE = "validate";
    public static final String EV_RUN = "run";
    public static final String EV_RETURN = "return";
    /**
     * Invoked after the entity operation, but before the cache is cleared.
     */
    public static final String EV_CACHE_CLEAR = "cache-clear";
    public static final String EV_CACHE_CHECK = "cache-check";
    public static final String EV_CACHE_PUT = "cache-put";

    public static final String OP_CREATE = "create";
    public static final String OP_STORE = "store";
    public static final String OP_REMOVE = "remove";
    public static final String OP_FIND = "find";


    public void setDelegator(Delegator delegator);

    public Map<String, List<T>> getEntityEventMap(String entityName);

    public void evalRules(String currentOperation, Map<String, List<T>> eventMap, String event, GenericEntity value, boolean isError) throws GenericEntityException;
}
