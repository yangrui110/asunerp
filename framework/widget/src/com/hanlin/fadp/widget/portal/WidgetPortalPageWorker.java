
package com.hanlin.fadp.widget.portal;

import com.hanlin.fadp.base.util.Debug;

/**
 * PortalPageWorker Class
 */
public class WidgetPortalPageWorker {
    public static final String module = WidgetPortalPageWorker.class.getName();
    public static PortalPageWorkerInterface portalPageWorker = null;
    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            // note: loadClass is necessary for these since this class doesn't know anything about them at compile time
            portalPageWorker = (PortalPageWorkerInterface) loader.loadClass("com.hanlin.fadp.widget.portal.PortalPageWorker").newInstance();
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (IllegalAccessException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (InstantiationException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        }
    }
}
