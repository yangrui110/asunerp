
package com.hanlin.fadp.entity;

/**
 * GenericEntityNotFoundException
 *
 */
@SuppressWarnings("serial")
public class GenericEntityNotFoundException extends GenericEntityException {

    public GenericEntityNotFoundException() {
        super();
    }

    public GenericEntityNotFoundException(String str) {
        super(str);
    }

    public GenericEntityNotFoundException(String str, Throwable nested) {
        super(str, nested);
    }
}
