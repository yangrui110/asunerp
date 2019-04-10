
package com.hanlin.fadp.common.status;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

/**
 * StatusWorker
 */
public class StatusWorker {

    public static final String module = StatusWorker.class.getName();

    public static void getStatusItems(PageContext pageContext, String attributeName, String statusTypeId) {
        Delegator delegator = (Delegator) pageContext.getRequest().getAttribute("delegator");

        try {
            List<GenericValue> statusItems = EntityQuery.use(delegator)
                                                        .from("StatusItem")
                                                        .where("statusTypeId", statusTypeId)
                                                        .orderBy("sequenceId")
                                                        .cache(true)
                                                        .queryList();
            if (statusItems != null)
                pageContext.setAttribute(attributeName, statusItems);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    }

    public static void getStatusItems(PageContext pageContext, String attributeName, String statusTypeIdOne, String statusTypeIdTwo) {
        Delegator delegator = (Delegator) pageContext.getRequest().getAttribute("delegator");
        List<GenericValue> statusItems = new LinkedList<GenericValue>();

        try {
             List<GenericValue> calItems = EntityQuery.use(delegator)
                                                      .from("StatusItem")
                                                      .where("statusTypeId", statusTypeIdOne)
                                                      .orderBy("sequenceId")
                                                      .cache(true)
                                                      .queryList();
            if (calItems != null)
                statusItems.addAll(calItems);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        try {
            List<GenericValue> taskItems = EntityQuery.use(delegator)
                                                      .from("StatusItem")
                                                      .where("statusTypeId", statusTypeIdTwo)
                                                      .orderBy("sequenceId")
                                                      .cache(true)
                                                      .queryList();
            if (taskItems != null)
                statusItems.addAll(taskItems);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (statusItems.size() > 0)
            pageContext.setAttribute(attributeName, statusItems);
    }

    public static void getStatusValidChangeToDetails(PageContext pageContext, String attributeName, String statusId) {
        Delegator delegator = (Delegator) pageContext.getRequest().getAttribute("delegator");
        List<GenericValue> statusValidChangeToDetails = null;

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

        if (statusValidChangeToDetails != null)
            pageContext.setAttribute(attributeName, statusValidChangeToDetails);
    }
}
