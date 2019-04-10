
package com.hanlin.fadp.service.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;notify&gt;</code> element.
 */
@ThreadSafe
public final class Notify {

    private final String content;
    private final String type;

    Notify(Element notifyElement) throws ServiceConfigException {
        String type = notifyElement.getAttribute("type").intern();
        if (type.isEmpty()) {
            throw new ServiceConfigException("<notify> element type attribute is empty");
        }
        this.type = type;
        String content = UtilXml.elementValue(notifyElement);
        if (content == null) {
            content = "";
        }
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

}
