
package com.hanlin.fadp.entity.transaction;

import com.hanlin.fadp.entity.GenericEntityException;

/**
 * GenericTransactionException
 */
@SuppressWarnings("serial")
public class GenericTransactionException extends GenericEntityException {

    public GenericTransactionException() {
        super();
    }

    public GenericTransactionException(String str) {
        super(str);
    }

    public GenericTransactionException(String str, Throwable nested) {
        super(str, nested);
    }
}
