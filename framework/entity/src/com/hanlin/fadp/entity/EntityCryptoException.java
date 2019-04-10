
package com.hanlin.fadp.entity;

@SuppressWarnings("serial")
public class EntityCryptoException extends GenericEntityException {

    public EntityCryptoException() {
        super();
    }

    public EntityCryptoException(Throwable nested) {
        super(nested);
    }

    public EntityCryptoException(String str) {
        super(str);
    }

    public EntityCryptoException(String str, Throwable nested) {
        super(str, nested);
    }
}
