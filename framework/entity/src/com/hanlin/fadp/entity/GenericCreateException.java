
package com.hanlin.fadp.entity;

/**
 * GenericCreateException
 *
 */
@SuppressWarnings("serial")
public class GenericCreateException extends GenericEntityException {

    public GenericCreateException() {
        super();
    }

    public GenericCreateException(String str) {
        super(str);
    }

    public GenericCreateException(String str, Throwable nested) {
        super(str, nested);
    }
}
