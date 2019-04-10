
package com.hanlin.fadp.security;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * <code>SecurityConfigurationException</code>
 */
@SuppressWarnings("serial")
public class SecurityConfigurationException extends GeneralException {

    public SecurityConfigurationException(String str, Throwable t) {
        super(str, t);
    }

    public SecurityConfigurationException(String str) {
        super(str);
    }

    public SecurityConfigurationException() {
        super();
    }
}
