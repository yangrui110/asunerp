
package com.hanlin.fadp.entity;

/**
 * GenericRemoveException
 *
 */
@SuppressWarnings("serial")
public class GenericRemoveException extends GenericEntityException {

    public GenericRemoveException() {
        super();
    }

    public GenericRemoveException(String str) {
        super(str);
    }

    public GenericRemoveException(String str, Throwable nested) {
        super(str, nested);
    }
}
