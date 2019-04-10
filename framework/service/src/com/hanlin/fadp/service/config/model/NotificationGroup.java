
package com.hanlin.fadp.service.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;notification-group&gt;</code> element.
 */
@ThreadSafe
public final class NotificationGroup {

    private final String name;
    private final Notification notification;
    private final List<Notify> notifyList;

    NotificationGroup(Element notificationGroupElement) throws ServiceConfigException {
        String name = notificationGroupElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<notification-group> element name attribute is empty");
        }
        this.name = name;
        Element notification = UtilXml.firstChildElement(notificationGroupElement, "notification");
        if (notification == null) {
            throw new ServiceConfigException("<notification> element is missing");
        }
        this.notification = new Notification(notification);
        List<? extends Element> notifyElementList = UtilXml.childElementList(notificationGroupElement, "notify");
        if (notifyElementList.size() < 2) {
            throw new ServiceConfigException("<notify> element(s) missing");
        } else {
            List<Notify> notifyList = new ArrayList<Notify>(notifyElementList.size());
            for (Element notifyElement : notifyElementList) {
                notifyList.add(new Notify(notifyElement));
            }
            this.notifyList = Collections.unmodifiableList(notifyList);
        }
    }

    public String getName() {
        return name;
    }

    public Notification getNotification() {
        return notification;
    }

    public List<Notify> getNotifyList() {
        return this.notifyList;
    }
}
