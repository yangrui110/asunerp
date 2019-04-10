
package com.hanlin.fadp.service.job;

@SuppressWarnings("serial")
public class InvalidJobException extends JobManagerException {

    /**
     * Creates new <code>InvalidJobException</code> without detail message.
     */
    public InvalidJobException() {
        super();
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidJobException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message and nested Exception.
     * @param nested the nested exception.
     */
    public InvalidJobException(Throwable nested) {
        super(nested);
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     * @param nested the nested exception.
     */
    public InvalidJobException(String msg, Throwable nested) {
        super(msg, nested);
    }
}
