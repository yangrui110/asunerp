
package com.hanlin.fadp.service;

public class ExecutionServiceException extends com.hanlin.fadp.base.util.GeneralException {

    public ExecutionServiceException() {
        super();
    }

    public ExecutionServiceException(String str) {
        super(str);
    }

    public ExecutionServiceException(String str, Throwable nested) {
        super(str, nested);
    }

    public ExecutionServiceException(Throwable nested) {
        super(nested);
    }
}
