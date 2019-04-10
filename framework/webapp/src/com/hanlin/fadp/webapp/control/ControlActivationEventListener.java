
package com.hanlin.fadp.webapp.control;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import com.hanlin.fadp.base.util.Debug;

/**
 * HttpSessionListener that gathers and tracks various information and statistics
 */
public class ControlActivationEventListener implements HttpSessionActivationListener {
    // Debug module name
    public static final String module = ControlActivationEventListener.class.getName();

    public ControlActivationEventListener() {}

    public void sessionWillPassivate(HttpSessionEvent event) {
        ControlEventListener.countPassivateSession();
        Debug.logInfo("Passivating session: " + event.getSession().getId(), module);
    }

    public void sessionDidActivate(HttpSessionEvent event) {
        ControlEventListener.countActivateSession();
        Debug.logInfo("Activating session: " + event.getSession().getId(), module);
    }
}
