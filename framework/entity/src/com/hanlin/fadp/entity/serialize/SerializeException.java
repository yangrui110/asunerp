
package com.hanlin.fadp.entity.serialize;

/**
 * XmlSerializable
 *
 */
@SuppressWarnings("serial")
public class SerializeException extends com.hanlin.fadp.base.util.GeneralException {

    public SerializeException() {
        super();
    }

    public SerializeException(String str) {
        super(str);
    }

    public SerializeException(String str, Throwable nested) {
        super(str, nested);
    }
}
