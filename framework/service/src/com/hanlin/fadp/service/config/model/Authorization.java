
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;authorization&gt;</code> element.
 */
@ThreadSafe
public final class Authorization {

    private final String serviceName;

    Authorization(Element authElement) throws ServiceConfigException {
        String serviceName = authElement.getAttribute("service-name").intern();
        if (serviceName.isEmpty()) {
            throw new ServiceConfigException("<authorization> element service-name attribute is empty");
        }
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
