
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;resource-loader&gt;</code> element.
 */
@ThreadSafe
public final class ResourceLoader {

    private final String className;
    private final String name;
    private final String prefix;
    private final String prependEnv;

    ResourceLoader(Element resourceLoaderElement) throws ServiceConfigException {
        String name = resourceLoaderElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<resource-loader> element name attribute is empty");
        }
        this.name = name;
        String className = resourceLoaderElement.getAttribute("class").intern();
        if (className.isEmpty()) {
            throw new ServiceConfigException("<resource-loader> element class attribute is empty");
        }
        this.className = className;
        this.prependEnv = resourceLoaderElement.getAttribute("prepend-env").intern();
        this.prefix = resourceLoaderElement.getAttribute("prefix").intern();
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrependEnv() {
        return prependEnv;
    }
}
