
package com.hanlin.fadp.base.container;

import com.hanlin.fadp.base.config.GenericConfigException;

/**
 * ContainerException
 */
@SuppressWarnings("serial")
public class ContainerException extends GenericConfigException {

    public ContainerException() {
        super();
    }

    public ContainerException(String str) {
        super(str);
    }

    public ContainerException(Throwable t) {
        super(t);
    }

    public ContainerException(String str, Throwable nested) {
        super(str, nested);
    }

}
