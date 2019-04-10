
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An abstract class for <code>&lt;datasource&gt;</code> JDBC child elements.
 *
 * @see <code>entity-config.xsd</code>
 */
public abstract class JdbcElement {

    private final String isolationLevel;
    private final String lineNumber;

    protected JdbcElement(Element element) throws GenericEntityConfException {
        this.isolationLevel = element.getAttribute("isolation-level").intern();
        Object lineNumber = element.getUserData("startLine");
        this.lineNumber = lineNumber == null ? "unknown" : lineNumber.toString();
    }

    /** Returns the value of the <code>isolation-level</code> attribute. */
    public String getIsolationLevel() {
        return this.isolationLevel;
    }

    /**
     * Returns the configuration file line number for this element.
     * @return The configuration file line number for this element
     */
    public String getLineNumber() {
        return this.lineNumber;
    }
}
