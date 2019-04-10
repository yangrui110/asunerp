
package com.hanlin.fadp.webapp.event;

/**
 * EventHandlerException.java
 */
@SuppressWarnings("serial")
public class EventHandlerException extends com.hanlin.fadp.base.util.GeneralException {

    public EventHandlerException(String str, Throwable t) {
        super(str, t);
    }

    public EventHandlerException(Throwable t) {
        super(t);
    }

    public EventHandlerException(String str) {
        super(str);
    }

    public EventHandlerException() {
        super();
    }
}

