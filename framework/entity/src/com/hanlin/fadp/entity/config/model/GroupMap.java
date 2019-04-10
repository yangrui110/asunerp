
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;group-map&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class GroupMap {

    private final String groupName; // type = xs:string
    private final String datasourceName; // type = xs:string

    GroupMap(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String groupName = element.getAttribute("group-name").intern();
        if (groupName.isEmpty()) {
            throw new GenericEntityConfException("<group-map> element group-name attribute is empty" + lineNumberText);
        }
        this.groupName = groupName;
        String datasourceName = element.getAttribute("datasource-name").intern();
        if (datasourceName.isEmpty()) {
            throw new GenericEntityConfException("<group-map> element datasource-name attribute is empty" + lineNumberText);
        }
        this.datasourceName = datasourceName;
    }

    /** Returns the value of the <code>group-name</code> attribute. */
    public String getGroupName() {
        return this.groupName;
    }

    /** Returns the value of the <code>datasource-name</code> attribute. */
    public String getDatasourceName() {
        return this.datasourceName;
    }
}
