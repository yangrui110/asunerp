
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;service-ecas&gt;</code> element.
 */
@ThreadSafe
public final class ServiceEcas {

    private final String loader;
    private final String location;

    ServiceEcas(Element serviceEcasElement) throws ServiceConfigException {
        String loader = serviceEcasElement.getAttribute("loader").intern();
        if (loader.isEmpty()) {
            throw new ServiceConfigException("<service-ecas> element loader attribute is empty");
        }
        this.loader = loader;
        String location = serviceEcasElement.getAttribute("location").intern();
        if (location.isEmpty()) {
            throw new ServiceConfigException("<service-ecas> element location attribute is empty");
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
