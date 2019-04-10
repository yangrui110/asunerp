
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;tyrex-dataSource&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class TyrexDataSource extends JdbcElement {

    private final String dataSourceName; // type = xs:string

    TyrexDataSource(Element element) throws GenericEntityConfException {
        super(element);
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String dataSourceName = element.getAttribute("dataSource-name").intern();
        if (dataSourceName.isEmpty()) {
            throw new GenericEntityConfException("<tyrex-dataSource> element dataSource-name attribute is empty" + lineNumberText);
        }
        this.dataSourceName = dataSourceName;
    }

    /** Returns the value of the <code>dataSource-name</code> attribute. */
    public String getDataSourceName() {
        return this.dataSourceName;
    }
}
