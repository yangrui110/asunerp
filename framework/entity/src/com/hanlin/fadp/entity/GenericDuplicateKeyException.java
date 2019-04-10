
package com.hanlin.fadp.entity;

/**
 * GenericDuplicateKeyException.java
 *
 */
@SuppressWarnings("serial")
public class GenericDuplicateKeyException extends GenericEntityException {

    public GenericDuplicateKeyException() {
        super();
    }

    public GenericDuplicateKeyException(String str) {
        super(str);
    }

    public GenericDuplicateKeyException(String str, Throwable nested) {
        super(str, nested);
    }
}
