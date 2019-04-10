
package com.hanlin.fadp.base.config;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * GenericConfigException
 *
 */
@SuppressWarnings("serial")
public class GenericConfigException extends GeneralException {

    public GenericConfigException() {
        super();
    }

    public GenericConfigException(String str) {
        super(str);
    }

    public GenericConfigException(Throwable nested) {
        super(nested);
    }

    public GenericConfigException(String str, Throwable nested) {
        super(str, nested);
    }
}
