
package com.hanlin.fadp.entity.config.model;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;read-data&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class ReadData {

    private final String readerName; // type = xs:string

    ReadData(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String readerName = element.getAttribute("reader-name").intern();
        if (readerName.isEmpty()) {
            throw new GenericEntityConfException("<read-data> element reader-name attribute is empty" + lineNumberText);
        }
        this.readerName = readerName;
    }

    /** Returns the value of the <code>reader-name</code> attribute. */
    public String getReaderName() {
        return this.readerName;
    }
}
