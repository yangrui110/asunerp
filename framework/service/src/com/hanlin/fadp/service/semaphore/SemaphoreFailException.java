
package com.hanlin.fadp.service.semaphore;

import com.hanlin.fadp.service.GenericServiceException;

/**
 * SemaphoreFailException
 */
@SuppressWarnings("serial")
public class SemaphoreFailException extends GenericServiceException {

    public SemaphoreFailException() {
        super();
    }

    public SemaphoreFailException(String str) {
        super(str);
    }

    public SemaphoreFailException(String str, Throwable nested) {
        super(str, nested);
    }

    public SemaphoreFailException(Throwable nested) {
        super(nested);
    }
}
