
package com.hanlin.fadp.entity;

/**
 * GenericNoSuchEntityException.java
 *
 */
@SuppressWarnings("serial")
public class GenericNoSuchEntityException extends GenericEntityException {

    public GenericNoSuchEntityException() {
        super();
    }

    public GenericNoSuchEntityException(String str) {
        super(str);
    }

    public GenericNoSuchEntityException(String str, Throwable nested) {
        super(str, nested);
    }
}
