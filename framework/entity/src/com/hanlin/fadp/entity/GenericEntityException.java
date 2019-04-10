
package com.hanlin.fadp.entity;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * GenericEntityException
 *
 */
@SuppressWarnings("serial")
public class GenericEntityException extends GeneralException {

    public GenericEntityException() {
        super();
    }

    public GenericEntityException(Throwable nested) {
        super(nested);
    }

    public GenericEntityException(String str) {
        super(str);
    }

    public GenericEntityException(String str, Throwable nested) {
        super(str, nested);
    }
}
