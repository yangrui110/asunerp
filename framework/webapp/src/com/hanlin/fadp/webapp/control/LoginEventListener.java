

package com.hanlin.fadp.webapp.control;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.hanlin.fadp.webapp.control.LoginWorker;

/**
 * HttpSessionListener that finalizes login information
 */
public class LoginEventListener implements HttpSessionListener {
    // Debug module name
    public static final String module = LoginEventListener.class.getName();

    public LoginEventListener() {}

    public void sessionCreated(HttpSessionEvent event) {
        //for this one do nothing when the session is created...
        //HttpSession session = event.getSession();
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        LoginWorker.cleanupExternalLoginKey(session);
    }
}
