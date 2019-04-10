
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;parameter&gt;</code> element.
 */
@ThreadSafe
public final class Parameter {

    private final String name;
    private final String value;

    Parameter(Element parameterElement) throws ServiceConfigException {
        String name = parameterElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<parameter> element name attribute is empty");
        }
        this.name = name;
        String value = parameterElement.getAttribute("value").intern();
        if (value.isEmpty()) {
            throw new ServiceConfigException("<parameter> element value attribute is empty");
        }
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
