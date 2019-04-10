
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;service-location&gt;</code> element.
 */
@ThreadSafe
public final class ServiceLocation {

    private final String location;
    private final String name;

    ServiceLocation(Element serviceLocationElement, String location) throws ServiceConfigException {
        String name = serviceLocationElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<service-location> element name attribute is empty");
        }
        this.name = name;
        if (location.isEmpty()) {
            throw new ServiceConfigException("<service-location> element location attribute is empty");
        }
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
