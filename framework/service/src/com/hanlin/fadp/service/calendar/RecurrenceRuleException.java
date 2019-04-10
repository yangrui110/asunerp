
package com.hanlin.fadp.service.calendar;

/**
 * Recurrence Rule Exception
 */
@SuppressWarnings("serial")
public class RecurrenceRuleException extends com.hanlin.fadp.base.util.GeneralException {

    public RecurrenceRuleException() {
        super();
    }

    public RecurrenceRuleException(String msg) {
        super(msg);
    }

    public RecurrenceRuleException(String msg, Throwable nested) {
        super(msg, nested);
    }

}
