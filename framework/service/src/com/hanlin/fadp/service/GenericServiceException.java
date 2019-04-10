
package com.hanlin.fadp.service;

/**
 * Generic Service Exception
 */
@SuppressWarnings("serial")
public class GenericServiceException extends com.hanlin.fadp.base.util.GeneralException {

    public GenericServiceException() {
        super();
    }

    public GenericServiceException(String str) {
        super(str);
    }

    public GenericServiceException(String str, Throwable nested) {
        super(str, nested);
    }

    public GenericServiceException(Throwable nested) {
        super(nested);
    }
}
