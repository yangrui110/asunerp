
package com.hanlin.fadp.base.component;

import com.hanlin.fadp.base.config.GenericConfigException;

/**
 * ComponentException
 *
 */
@SuppressWarnings("serial")
public class ComponentException extends GenericConfigException {

    public ComponentException() {
        super();
    }

    public ComponentException(String str) {
        super(str);
    }

    public ComponentException(Throwable nested) {
        super(nested);
    }

    public ComponentException(String str, Throwable nested) {
        super(str, nested);
    }
}
