
package com.hanlin.fadp.entity.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.GenericEntityConfException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;entity-group-reader&gt;</code> element.
 *
 * @see <code>entity-config.xsd</code>
 */
@ThreadSafe
public final class EntityGroupReader {

    private final String name; // type = xs:string
    private final String loader; // type = xs:string
    private final String location; // type = xs:string
    private final List<Resource> resourceList; // <resource>

    EntityGroupReader(Element element) throws GenericEntityConfException {
        String lineNumberText = EntityConfig.createConfigFileLineNumberText(element);
        String name = element.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new GenericEntityConfException("<entity-group-reader> element name attribute is empty" + lineNumberText);
        }
        this.name = name;
        this.loader = element.getAttribute("loader").intern();
        this.location = element.getAttribute("location").intern();
        List<? extends Element> resourceElementList = UtilXml.childElementList(element, "resource");
        if (resourceElementList.isEmpty()) {
            this.resourceList = Collections.emptyList();
        } else {
            List<Resource> resourceList = new ArrayList<Resource>(resourceElementList.size());
            for (Element resourceElement : resourceElementList) {
                resourceList.add(new Resource(resourceElement));
            }
            this.resourceList = Collections.unmodifiableList(resourceList);
        }
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

    /** Returns the <code>&lt;resource&gt;</code> child elements. */
    public List<Resource> getResourceList() {
        return this.resourceList;
    }
}
