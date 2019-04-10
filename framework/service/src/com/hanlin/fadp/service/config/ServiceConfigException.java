
package com.hanlin.fadp.service.config;

import com.hanlin.fadp.base.config.GenericConfigException;

/**
 * Service configuration exception. Thrown when there is a configuration error in the
 * <code>serviceengine.xml</code> file.
 */
@SuppressWarnings("serial")
public class ServiceConfigException extends GenericConfigException {

    public ServiceConfigException() {
        super();
    }

    public ServiceConfigException(String str) {
        super(str);
    }

    public ServiceConfigException(String str, Throwable nested) {
        super(str, nested);
    }
}

