
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;field-type&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class FieldType {

    private final String name; // type = xs:string
    private final String loader; // type = xs:string
    private final String location; // type = xs:string

    FieldType(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String name = element.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new GenericEntityConfException("<field-type> element name attribute is empty" + lineNumberText);
        }
        this.name = name;
        String loader = element.getAttribute("loader").intern();
        if (loader.isEmpty()) {
            throw new GenericEntityConfException("<field-type> element loader attribute is empty" + lineNumberText);
        }
        this.loader = loader;
        String location = element.getAttribute("location").intern();
        if (location.isEmpty()) {
            throw new GenericEntityConfException("<field-type> element location attribute is empty" + lineNumberText);
        }
        this.location = location;
    }

    /** Returns the value of the <code>name</code> attribute. */
    public String getName() {
        return this.name;
    }

    /** Returns the value of the <code>loader</code> attribute. */
    public String getLoader() {
        return this.loader;
    }

    /** Returns the value of the <code>location</code> attribute. */
    public String getLocation() {
        return this.location;
    }
}
