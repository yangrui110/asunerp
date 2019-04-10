
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;resource-loader&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class ResourceLoader {

    private final String name; // type = xs:string
    private final String className; // type = xs:string
    private final String prependEnv; // type = xs:string
    private final String prefix; // type = xs:string

    ResourceLoader(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String name = element.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new GenericEntityConfException("<resource-loader> element name attribute is empty" + lineNumberText);
        }
        this.name = name;
        String className = element.getAttribute("class").intern();
        if (className.isEmpty()) {
            throw new GenericEntityConfException("<resource-loader> element class attribute is empty" + lineNumberText);
        }
        this.className = className;
        this.prependEnv = element.getAttribute("prepend-env").intern();
        this.prefix = element.getAttribute("prefix").intern();
    }

    /** Returns the value of the <code>name</code> attribute. */
    public String getName() {
        return this.name;
    }

    /** Returns the value of the <code>class</code> attribute. */
    public String getClassName() {
        return this.className;
    }

    /** Returns the value of the <code>prepend-env</code> attribute. */
    public String getPrependEnv() {
        return this.prependEnv;
    }

    /** Returns the value of the <code>prefix</code> attribute. */
    public String getPrefix() {
        return this.prefix;
    }
}
