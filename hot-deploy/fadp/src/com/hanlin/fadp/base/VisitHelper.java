package com.hanlin.fadp.base;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.common.HttpReverseProxy;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.webapp.stats.VisitHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class VisitHelper {
    public static final String module = VisitHelper.class.getName();

    /**
     * @param request
     * @param moduleName     模块名
     * @param operateType    操作类型
     * @param operateContent 操作（内容）描述
     */
    public static void writeVisit(HttpServletRequest request, String moduleName, String operateType, String operateContent) {
        GenericDelegator delegator = UtilGenerics.cast(request.getAttribute("delegator"));
        GenericValue userLogin = UtilGenerics.cast(request.getSession().getAttribute("userLogin"));
        GenericValue fadpLog = delegator.makeValue("FadpLog");
        if(userLogin!=null){
            fadpLog.set("userLoginId", userLogin.getString("userLoginId"));
            fadpLog.set("userName", userLogin.getString("userName"));
        }

        fadpLog.set("clientIp", HttpReverseProxy.getIp(request));
        fadpLog.set("visitDateTime", UtilDateTime.nowTimestamp());
        fadpLog.setString("moduleName", moduleName);
        fadpLog.setString("operateType", operateType);
        fadpLog.setString("operateContent", operateContent);
        try {
            fadpLog.create();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

    }

}
