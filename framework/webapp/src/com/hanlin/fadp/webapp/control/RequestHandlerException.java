
package com.hanlin.fadp.webapp.control;

/**
 * RequestHandlerException
 */
@SuppressWarnings("serial")
public class RequestHandlerException extends com.hanlin.fadp.base.util.GeneralException {

    public RequestHandlerException(String str, Throwable t) {
        super(str, t);
    }

    public RequestHandlerException(Throwable t) {
        super(t);
    }

    public RequestHandlerException(String str) {
        super(str);
    }

    public RequestHandlerException() {
        super();
    }
}

