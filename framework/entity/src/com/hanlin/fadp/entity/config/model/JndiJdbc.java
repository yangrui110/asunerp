
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;jndi-jdbc&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class JndiJdbc extends JdbcElement {

    private final String jndiServerName; // type = xs:string
    private final String jndiName; // type = xs:string

    JndiJdbc(Element element) throws GenericEntityConfException {
        super(element);
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String jndiServerName = element.getAttribute("jndi-server-name").intern();
        if (jndiServerName.isEmpty()) {
            throw new GenericEntityConfException("<jndi-jdbc> element jndi-server-name attribute is empty" + lineNumberText);
        }
        this.jndiServerName = jndiServerName;
        String jndiName = element.getAttribute("jndi-name").intern();
        if (jndiName.isEmpty()) {
            throw new GenericEntityConfException("<jndi-jdbc> element jndi-name attribute is empty" + lineNumberText);
        }
        this.jndiName = jndiName;
    }

    /** Returns the value of the <code>jndi-server-name</code> attribute. */
    public String getJndiServerName() {
        return this.jndiServerName;
    }

    /** Returns the value of the <code>jndi-name</code> attribute. */
    public String getJndiName() {
        return this.jndiName;
    }
}
