
package com.hanlin.fadp.service;

/**
 * ServiceAuthException
 */
@SuppressWarnings("serial")
public class ServiceAuthException extends GenericServiceException {

    public ServiceAuthException() {
        super();
    }

    public ServiceAuthException(String str) {
        super(str);
    }

    public ServiceAuthException(String str, Throwable nested) {
        super(str, nested);
    }
}

