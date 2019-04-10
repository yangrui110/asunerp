
package com.hanlin.fadp.entity;

/**
 * GenericStoreException
 *
 */
@SuppressWarnings("serial")
public class GenericStoreException extends GenericEntityException {

    public GenericStoreException() {
        super();
    }

    public GenericStoreException(String str) {
        super(str);
    }

    public GenericStoreException(String str, Throwable nested) {
        super(str, nested);
    }
}
