
package com.hanlin.fadp.entity;

/**
 * GenericResultSetClosedException
 *
 */
@SuppressWarnings("serial")
public class GenericResultSetClosedException extends GenericEntityException {

    public GenericResultSetClosedException() {
        super();
    }

    public GenericResultSetClosedException(String str) {
        super(str);
    }

    public GenericResultSetClosedException(String str, Throwable nested) {
        super(str, nested);
    }
}
