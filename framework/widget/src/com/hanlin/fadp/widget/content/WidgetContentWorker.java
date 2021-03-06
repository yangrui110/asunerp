
package com.hanlin.fadp.widget.content;

import com.hanlin.fadp.base.util.Debug;

/**
 * WidgetContentWorker Class
 */
public class WidgetContentWorker {
    public static final String module = WidgetContentWorker.class.getName();
    public static ContentWorkerInterface contentWorker = null;
    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            // note: loadClass is necessary for these since this class doesn't know anything about them at compile time
            contentWorker = (ContentWorkerInterface) loader.loadClass("com.hanlin.fadp.content.content.ContentWorker").newInstance();
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (IllegalAccessException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (InstantiationException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        }
    }
}
