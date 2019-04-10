
package com.hanlin.fadp.entityext.eca;

import com.hanlin.fadp.entity.GenericEntityException;

/**
 * EntityEcaException
 */
@SuppressWarnings("serial")
public class EntityEcaException extends GenericEntityException {

    public EntityEcaException() {
        super();
    }

    public EntityEcaException(String str) {
        super(str);
    }

    public EntityEcaException(String str, Throwable nested) {
        super(str, nested);
    }
}
