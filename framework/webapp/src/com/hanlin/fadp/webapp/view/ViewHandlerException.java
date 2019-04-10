
package com.hanlin.fadp.webapp.view;

/**
 * ViewHandlerException - View Handler Exception
 */
@SuppressWarnings("serial")
public class ViewHandlerException extends com.hanlin.fadp.base.util.GeneralException {

    public ViewHandlerException() {
        super();
    }

    public ViewHandlerException(String msg) {
        super(msg);
    }

    public ViewHandlerException(Throwable t) {
        super(t);
    }

    public ViewHandlerException(String msg, Throwable t) {
        super(msg, t);
    }
}
