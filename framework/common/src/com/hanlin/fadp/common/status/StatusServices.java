
package com.hanlin.fadp.common.status;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;

import static com.hanlin.fadp.base.util.UtilGenerics.checkList;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.ServiceUtil;

/**
 * StatusServices
 */
public class StatusServices {

    public static final String module = StatusServices.class.getName();
    public static final String resource = "CommonUiLabels";

    public static Map<String, Object> getStatusItems(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        List<String> statusTypes = checkList(context.get("statusTypeIds"), String.class);
        Locale locale = (Locale) context.get("locale");
        if (UtilValidate.isEmpty(statusTypes)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonStatusMandatory", locale));
        }

        List<GenericValue> statusItems = new LinkedList<GenericValue>();
        for (String statusTypeId: statusTypes) {
            try {
                List<GenericValue> myStatusItems = EntityQuery.use(delegator)
                                                              .from("StatusItem")
                                                              .where("statusTypeId", statusTypeId)
                                                              .orderBy("sequenceId")
                                                              .cache(true)
                                                              .queryList();
                statusItems.addAll(myStatusItems);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        Map<String, Object> ret =  new LinkedHashMap<String, Object>();
        ret.put("statusItems",statusItems);
        return ret;
    }

    public static Map<String, Object> getStatusValidChangeToDetails(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        List<GenericValue> statusValidChangeToDetails = null;
        String statusId = (String) context.get("statusId");
        try {
            statusValidChangeToDetails = EntityQuery.use(delegator)
                                                    .from("StatusValidChangeToDetail")
                                                    .where("statusId", statusId)
                                                    .orderBy("sequenceId")
                                                    .cache(true)
                                                    .queryList();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        Map<String, Object> ret = ServiceUtil.returnSuccess();
        if (statusValidChangeToDetails != null) {
            ret.put("statusValidChangeToDetails", statusValidChangeToDetails);
        }
        return ret;
    }
}
