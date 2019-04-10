
package com.hanlin.fadp.base.start;

/**
 * StartupException
 *
 */
@SuppressWarnings("serial")
public class StartupException extends Exception {

    /**
     * Creates new <code>StartupException</code> without detail message.
     */
    public StartupException() {
        super();
    }

    /**
     * Constructs an <code>StartupException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public StartupException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>StartupException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     * @param nested the chained exception.
     */
    public StartupException(String msg, Throwable nested) {
        super(msg, nested);
    }

    /**
     * Constructs an <code>StartupException</code> with the specified detail message and nested Exception.
     * @param nested the chained exception.
     */
    public StartupException(Throwable nested) {
        super(nested);
    }

    /** Returns the detail message, including the message from the nested exception if there is one. */
    @Override
    public String getMessage() {
        if (getCause() != null) {
            return super.getMessage() + " (" + getCause().getMessage() + ")";
        } else {
            return super.getMessage();
        }
    }

    /** Returns the detail message, NOT including the message from the nested exception. */
    public String getNonNestedMessage() {
        return super.getMessage();
    }
}
