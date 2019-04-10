
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;service-groups&gt;</code> element.
 */
@ThreadSafe
public final class ServiceGroups {

    private final String loader;
    private final String location;

    ServiceGroups(Element serviceGroupsElement) throws ServiceConfigException {
        String loader = serviceGroupsElement.getAttribute("loader").intern();
        if (loader.isEmpty()) {
            throw new ServiceConfigException("<service-groups> element loader attribute is empty");
        }
        this.loader = loader;
        String location = serviceGroupsElement.getAttribute("location").intern();
        if (location.isEmpty()) {
            throw new ServiceConfigException("<service-groups> element location attribute is empty");
        }
        this.location = location;
    }

    public String getLoader() {
        return loader;
    }

    public String getLocation() {
        return location;
    }
}
