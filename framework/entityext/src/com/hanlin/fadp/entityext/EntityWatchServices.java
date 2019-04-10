
package com.hanlin.fadp.entityext;

import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.ServiceUtil;

public class EntityWatchServices {

    public static final String module = EntityWatchServices.class.getName();

    /**
     * This service is meant to be called through an Entity ECA (EECA) to watch an entity
     *
     * @param dctx the dispatch context
     * @param context the context
     * @return the result of the service execution
     */
    public static Map<String, Object> watchEntity(DispatchContext dctx, Map<String, ? extends Object> context) {
        GenericValue newValue = (GenericValue) context.get("newValue");
        String fieldName = (String) context.get("fieldName");

        if (newValue == null) {
            return ServiceUtil.returnSuccess();
        }

        GenericValue currentValue = null;
        try {
            currentValue = dctx.getDelegator().findOne(newValue.getEntityName(), newValue.getPrimaryKey(), false);
        } catch (GenericEntityException e) {
            String errMsg = "Error finding currentValue for primary key [" + newValue.getPrimaryKey() + "]: " + e.toString();
            Debug.logError(e, errMsg, module);
        }

        if (currentValue != null) {
            if (UtilValidate.isNotEmpty(fieldName)) {
                // just watch the field
                Object currentFieldValue = currentValue.get(fieldName);
                Object newFieldValue = newValue.get(fieldName);
                boolean changed = false;
                if (currentFieldValue != null) {
                    if (!currentFieldValue.equals(newFieldValue)) {
                        changed = true;
                    }
                } else {
                    if (newFieldValue != null) {
                        changed = true;
                    }
                }

                if (changed) {
                    String errMsg = "Watching entity [" + currentValue.getEntityName() + "] field [" + fieldName + "] value changed from [" + currentFieldValue + "] to [" + newFieldValue + "] for pk [" + newValue.getPrimaryKey() + "]";
                    Debug.logInfo(new Exception(errMsg), errMsg, module);
                }
            } else {
                // watch the whole entity
                if (!currentValue.equals(newValue)) {
                    String errMsg = "Watching entity [" + currentValue.getEntityName() + "] values changed from [" + currentValue + "] to [" + newValue + "] for pk [" + newValue.getPrimaryKey() + "]";
                    Debug.logInfo(new Exception(errMsg), errMsg, module);
                }
            }
        } else {
            if (UtilValidate.isNotEmpty(fieldName)) {
                // just watch the field
                Object newFieldValue = newValue.get(fieldName);
                String errMsg = "Watching entity [" + newValue.getEntityName() + "] field [" + fieldName + "] value changed from [null] to [" + newFieldValue + "] for pk [" + newValue.getPrimaryKey() + "]";
                Debug.logInfo(new Exception(errMsg), errMsg, module);
            } else {
                // watch the whole entity
                String errMsg = "Watching entity [" + newValue.getEntityName() + "] values changed from [null] to [" + newValue + "] for pk [" + newValue.getPrimaryKey() + "]";
                Debug.logInfo(new Exception(errMsg), errMsg, module);
            }
        }

        return ServiceUtil.returnSuccess();
    }
}
