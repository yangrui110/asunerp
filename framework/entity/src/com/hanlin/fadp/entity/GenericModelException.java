
package com.hanlin.fadp.entity;

/**
 * GenericModelException
 *
 */
@SuppressWarnings("serial")
public class GenericModelException extends GenericEntityException {

    public GenericModelException() {
        super();
    }

    public GenericModelException(String str) {
        super(str);
    }

    public GenericModelException(String str, Throwable nested) {
        super(str, nested);
    }
}
