
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;notification&gt;</code> element.
 */
@ThreadSafe
public final class Notification {

    private final String screen;
    private final String service;
    private final String subject;

    Notification(Element notificationElement) throws ServiceConfigException {
        String subject = notificationElement.getAttribute("subject").intern();
        if (subject.isEmpty()) {
            throw new ServiceConfigException("<notification> element subject attribute is empty");
        }
        this.subject = subject;
        String screen = notificationElement.getAttribute("screen").intern();
        if (subject.isEmpty()) {
            throw new ServiceConfigException("<notification> element screen attribute is empty");
        }
        this.screen = screen;
        String service = notificationElement.getAttribute("service").intern();
        if (service.isEmpty()) {
            service = "sendMailFromScreen";
        }
        this.service = service;
        
    }

    public String getScreen() {
        return screen;
    }

    public String getService() {
        return this.service;
    }

    public String getSubject() {
        return subject;
    }
}
