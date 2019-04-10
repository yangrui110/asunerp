
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;debug-xa-resources&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class DebugXaResources {

    private final boolean value; // type = xs:string

    DebugXaResources(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String value = element.getAttribute("value").intern();
        if (value.isEmpty()) {
            throw new GenericEntityConfException("<debug-xa-resources> element value attribute is empty" + lineNumberText);
        }
        this.value = "true".equals(value);
    }

    /** Returns the value of the <code>value</code> attribute. */
    public boolean getValue() {
        return this.value;
    }
}
