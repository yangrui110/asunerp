
package com.hanlin.fadp.entity;

/**
 * GenericFindException
 *
 */
@SuppressWarnings("serial")
public class GenericFindException extends GenericEntityException {

    public GenericFindException() {
        super();
    }

    public GenericFindException(String str) {
        super(str);
    }

    public GenericFindException(String str, Throwable nested) {
        super(str, nested);
    }
}
