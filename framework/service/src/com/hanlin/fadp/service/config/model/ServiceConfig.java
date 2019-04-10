
package com.hanlin.fadp.service.config.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;service-config&gt;</code> element.
 */
@ThreadSafe
public final class ServiceConfig {

    public static ServiceConfig create(Element serviceConfigElement) throws ServiceConfigException {
        Map<String, ServiceEngine> serviceEngineMap = new HashMap<String, ServiceEngine>();
        List<? extends Element> engineElementList = UtilXml.childElementList(serviceConfigElement, "service-engine");
        for (Element engineElement : engineElementList) {
            ServiceEngine engineModel = new ServiceEngine(engineElement);
            serviceEngineMap.put(engineModel.getName(), engineModel);
        }
        return new ServiceConfig(serviceEngineMap);
    }

    private final Map<String, ServiceEngine> serviceEngineMap;

    private ServiceConfig(Map<String, ServiceEngine> serviceEngineMap) {
        this.serviceEngineMap = Collections.unmodifiableMap(serviceEngineMap);
    }

    public Collection<ServiceEngine> getServiceEngines() {
        return this.serviceEngineMap.values();
    }

    public ServiceEngine getServiceEngine(String name) {
        return this.serviceEngineMap.get(name);
    }


}
