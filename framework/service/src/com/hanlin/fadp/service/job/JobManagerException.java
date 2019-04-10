
package com.hanlin.fadp.service.job;

/**
 * Job Scheduler Exception
 */
@SuppressWarnings("serial")
public class JobManagerException extends com.hanlin.fadp.base.util.GeneralException {

    /**
     * Creates new <code>JobManagerException</code> without detail message.
     */
    public JobManagerException() {
        super();
    }

    /**
     * Constructs an <code>JobManagerException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public JobManagerException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>JobManagerException</code> with the specified detail message and nested Exception.
     * @param nested the nested exception.
     */
    public JobManagerException(Throwable nested) {
        super(nested);
    }

    /**
     * Constructs an <code>JobManagerException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     * @param nested the nested exception.
     */
    public JobManagerException(String msg, Throwable nested) {
        super(msg, nested);
    }
}

