
package com.hanlin.fadp.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;

/**
 * General Service Exception - base Exception for in-Service Errors
 */
@SuppressWarnings("serial")
public class GeneralServiceException extends com.hanlin.fadp.base.util.GeneralException {

    protected List<Object> errorMsgList = null;
    protected Map<String, ? extends Object> errorMsgMap = null;
    protected Map<String, ? extends Object> nestedServiceResult = null;

    public GeneralServiceException() {
        super();
    }

    public GeneralServiceException(String str) {
        super(str);
    }

    public GeneralServiceException(String str, Throwable nested) {
        super(str, nested);
    }

    public GeneralServiceException(Throwable nested) {
        super(nested);
    }

    public GeneralServiceException(String str, List<? extends Object> errorMsgList, Map<String, ? extends Object> errorMsgMap, Map<String, ? extends Object> nestedServiceResult, Throwable nested) {
        super(str, nested);
        this.errorMsgList = UtilMisc.makeListWritable(errorMsgList);
        this.errorMsgMap = errorMsgMap;
        this.nestedServiceResult = nestedServiceResult;
    }

    public Map<String, Object> returnError(String module) {
        String errMsg = this.getMessage() == null ? "Error in Service" : this.getMessage();
        if (this.getNested() != null) {
            Debug.logError(this.getNested(), errMsg, module);
        }
        return ServiceUtil.returnError(errMsg, this.errorMsgList, this.errorMsgMap, this.nestedServiceResult);
    }

    public void addErrorMessages(List<? extends Object> errMsgs) {
        if (this.errorMsgList == null) {
            this.errorMsgList = new LinkedList<Object>();
        }
        this.errorMsgList.addAll(errMsgs);
    }
}
