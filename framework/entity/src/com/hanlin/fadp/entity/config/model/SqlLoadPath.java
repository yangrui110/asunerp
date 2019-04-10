
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;sql-load-path&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class SqlLoadPath {

    private final String path; // type = xs:string
    private final String prependEnv; // type = xs:string

    SqlLoadPath(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String path = element.getAttribute("path").intern();
        if (path.isEmpty()) {
            throw new GenericEntityConfException("<sql-load-path> element path attribute is empty" + lineNumberText);
        }
        this.path = path;
        this.prependEnv = element.getAttribute("prepend-env").intern();
    }

    /** Returns the value of the <code>path</code> attribute. */
    public String getPath() {
        return this.path;
    }

    /** Returns the value of the <code>prepend-env</code> attribute. */
    public String getPrependEnv() {
        return this.prependEnv;
    }
}
