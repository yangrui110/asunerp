
package com.hanlin.fadp.minilang;

/**
 * Thrown to indicate a Mini-language error.
 */
@SuppressWarnings("serial")
public class MiniLangException extends com.hanlin.fadp.base.util.GeneralException {

    public MiniLangException() {
        super();
    }

    public MiniLangException(String str) {
        super(str);
    }

    public MiniLangException(String str, Throwable nested) {
        super(str, nested);
    }

    public MiniLangException(Throwable nested) {
        super(nested);
    }
}
