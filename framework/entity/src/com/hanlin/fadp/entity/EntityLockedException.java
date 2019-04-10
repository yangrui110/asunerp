
package com.hanlin.fadp.entity;

/**
 * EntityLockedException
 *
 */
@SuppressWarnings("serial")
public class EntityLockedException extends GenericEntityException {

    public EntityLockedException() {
        super();
    }

    public EntityLockedException(String str) {
        super(str);
    }

    public EntityLockedException(String str, Throwable nested) {
        super(str, nested);
    }
}
