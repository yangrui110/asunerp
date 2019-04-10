package com.hanlin.fadp.event;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.transaction.GenericTransactionException;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.RequestMap;
import com.hanlin.fadp.webapp.event.EventHandlerException;

/**
 * 为了开启全局事务
 * @author 陈林
 *
 */
public class JavaEventHandler extends com.hanlin.fadp.webapp.event.JavaEventHandler {

    public static final String module = JavaEventHandler.class.getName();

    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {

        boolean begin=false;
        String result="success";
        try{
        	if(event.globalTransaction){
        		begin=TransactionUtil.begin();
        	}
        	   result = super.invoke(event, requestMap,request,response);
        	  TransactionUtil.commit(begin);
        	  return result;
        }catch(Exception e){
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
