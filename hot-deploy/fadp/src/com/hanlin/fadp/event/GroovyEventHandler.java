package com.hanlin.fadp.event;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.RequestMap;
import com.hanlin.fadp.webapp.event.EventHandlerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by cl on 2017/1/11.
 * 使groovy event 也能支持全局事物
 */
public class GroovyEventHandler extends com.hanlin.fadp.webapp.event.GroovyEventHandler {

    public static final String module = GroovyEventHandler.class.getName();

    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {

        boolean begin = false;
        String  result="success";
        try {
            if (event.globalTransaction) {
                begin = TransactionUtil.begin();
            }
             result = super.invoke(event, requestMap, request, response);
            TransactionUtil.commit(begin);
            return result;
        } catch (Exception e) {
            Debug.logError(e, module);
            try {
                TransactionUtil.rollback(begin, e.getMessage(), e);
            } catch (GenericTransactionException e1) {
                e1.printStackTrace();
            }
            result="error";
        }
        return result;

    }
}
