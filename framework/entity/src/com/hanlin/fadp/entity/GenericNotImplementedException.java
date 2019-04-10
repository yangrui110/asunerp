
package com.hanlin.fadp.entity;

/**
 * GenericNotImplementedException
 *
 */
@SuppressWarnings("serial")
public class GenericNotImplementedException extends GenericEntityException {

    public GenericNotImplementedException() {
        super();
    }

    public GenericNotImplementedException(String str) {
        super(str);
    }

    public GenericNotImplementedException(String str, Throwable nested) {
        super(str, nested);
    }
}
