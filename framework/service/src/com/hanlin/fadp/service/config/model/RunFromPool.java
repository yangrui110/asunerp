
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;run-from-pool&gt;</code> element.
 */
@ThreadSafe
public final class RunFromPool {

    private final String name;

    RunFromPool(Element runFromPoolElement) throws ServiceConfigException {
        String name = runFromPoolElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<run-from-pool> element name attribute is empty");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
