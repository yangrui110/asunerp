
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;transaction-manager-jndi&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class TransactionManagerJndi {

    private final String jndiServerName; // type = xs:string
    private final String jndiName; // type = xs:string

    TransactionManagerJndi(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String jndiServerName = element.getAttribute("jndi-server-name").intern();
        if (jndiServerName.isEmpty()) {
            throw new GenericEntityConfException("<transaction-manager-jndi> element jndi-server-name attribute is empty" + lineNumberText);
        }
        this.jndiServerName = jndiServerName;
        String jndiName = element.getAttribute("jndi-name").intern();
        if (jndiName.isEmpty()) {
            throw new GenericEntityConfException("<transaction-manager-jndi> element jndi-name attribute is empty" + lineNumberText);
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
