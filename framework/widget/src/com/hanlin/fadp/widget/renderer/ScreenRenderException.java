

package com.hanlin.fadp.widget.renderer;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * Wraps any exceptions encountered during the rendering of
 * a screen.  It is thrown to the top of the recursive
 * rendering process so that we avoid having to log redundant
 * exceptions.
 */
@SuppressWarnings("serial")
public class ScreenRenderException extends GeneralException {

    public ScreenRenderException() {
        super();
    }

    public ScreenRenderException(Throwable nested) {
        super(nested);
    }

    public ScreenRenderException(String str) {
        super(str);
    }

    public ScreenRenderException(String str, Throwable nested) {
        super(str, nested);
    }
}
