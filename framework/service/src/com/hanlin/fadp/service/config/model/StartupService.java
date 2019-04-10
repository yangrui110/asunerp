
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;startup-service&gt;</code> element.
 */
@ThreadSafe
public final class StartupService {

    private final String name;
    private final String runInPool;
    private final String runtimeDataId;
    private final int runtimeDelay;

    StartupService(Element startupServiceElement) throws ServiceConfigException {
        String name = startupServiceElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<startup-service> element name attribute is empty");
        }
        this.name = name;
        this.runtimeDataId = startupServiceElement.getAttribute("runtime-data-id").intern();
        String runtimeDelay = startupServiceElement.getAttribute("runtime-delay").intern();
        if (runtimeDelay.isEmpty()) {
            this.runtimeDelay = 0;
        } else {
            try {
                this.runtimeDelay = Integer.parseInt(runtimeDelay);
            } catch (Exception e) {
                throw new ServiceConfigException("<startup-service> element runtime-delay attribute value is invalid");
            }
        }
        this.runInPool = startupServiceElement.getAttribute("run-in-pool").intern();
    }

    public String getName() {
        return name;
    }

    public String getRunInPool() {
        return runInPool;
    }

    public String getRuntimeDataId() {
        return runtimeDataId;
    }

    public int getRuntimeDelay() {
            return runtimeDelay;
    }
}
