package com.hanlin.fadp.workflow;

import com.hanlin.fadp.base.util.GeneralException;

@SuppressWarnings("serial")
public class GenericFadpFlowException extends GeneralException {
    public GenericFadpFlowException() {
        super();
    }

    public GenericFadpFlowException(Throwable nested) {
        super(nested);
    }

    public GenericFadpFlowException(String str) {
        super(str);
    }

    public GenericFadpFlowException(String str, Throwable nested) {
        super(str, nested);
    }
}
