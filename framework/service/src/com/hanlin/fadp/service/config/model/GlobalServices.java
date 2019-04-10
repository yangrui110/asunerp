
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;global-services&gt;</code> element.
 */
@ThreadSafe
public final class GlobalServices {

    private final String loader;
    private final String location;

    GlobalServices(Element globalServicesElement) throws ServiceConfigException {
        String loader = globalServicesElement.getAttribute("loader").intern();
        if (loader.isEmpty()) {
            throw new ServiceConfigException("<global-services> element loader attribute is empty");
        }
        this.loader = loader;
        String location = globalServicesElement.getAttribute("location").intern();
        if (location.isEmpty()) {
            throw new ServiceConfigException("<global-services> element location attribute is empty");
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
