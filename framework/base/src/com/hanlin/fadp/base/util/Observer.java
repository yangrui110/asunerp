
package com.hanlin.fadp.base.util;

/**
 * An <code>Observable</code> observer.
 *
 */
public interface Observer {
    /**
     * Called when <code>Observable.notifyObservers</code> is invoked.
     * 
     * @param observable
     * @param arg
     */
    void update(Observable observable, Object arg);
}
