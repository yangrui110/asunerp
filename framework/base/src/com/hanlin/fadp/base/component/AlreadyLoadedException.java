
package com.hanlin.fadp.base.component;

/**
 * Component Already Loaded Exception
 *
 */
@SuppressWarnings("serial")
public class AlreadyLoadedException extends ComponentException {

    public AlreadyLoadedException() {
        super();
    }

    public AlreadyLoadedException(String str) {
        super(str);
    }

    public AlreadyLoadedException(Throwable nested) {
        super(nested);
    }

    public AlreadyLoadedException(String str, Throwable nested) {
        super(str, nested);
    }
}

