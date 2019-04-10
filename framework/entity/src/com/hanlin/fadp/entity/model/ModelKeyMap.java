
package com.hanlin.fadp.entity.model;

import java.io.Serializable;
import java.util.List;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * An object that models the <code>&lt;key-map&gt;</code> element.
 *
 */
@ThreadSafe
@SuppressWarnings("serial")
public final class ModelKeyMap implements Comparable<ModelKeyMap>, Serializable {

    /*
     * Developers - this is an immutable class. Once constructed, the object should not change state.
     * Therefore, 'setter' methods are not allowed. If client code needs to modify the object's
     * state, then it can create a new copy with the changed values.
     */

    /** name of the field in this entity */
    private final String fieldName;

    /** name of the field in related entity */
    private final String relFieldName;

    /** Full name of the key map (fieldName:relFieldName) */
    private final String fullName;

    /** Data Constructor, if relFieldName is null defaults to fieldName */
    public ModelKeyMap(String fieldName, String relFieldName) {
        this.fieldName = fieldName;
        this.relFieldName = UtilXml.checkEmpty(relFieldName, this.fieldName);
        this.fullName = this.fieldName.concat(":").concat(this.relFieldName);
    }

    /** XML Constructor */
    public ModelKeyMap(Element keyMapElement) {
        this.fieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("field-name")).intern();
        // if no relFieldName is specified, use the fieldName; this is convenient for when they are named the same, which is often the case
        this.relFieldName = UtilXml.checkEmpty(keyMapElement.getAttribute("rel-field-name"), this.fieldName).intern();
        this.fullName = this.fieldName.concat(":").concat(this.relFieldName);
    }

    /** Returns the field name. */
    public String getFieldName() {
        return this.fieldName;
    }

    /** Returns the related entity field name. */
    public String getRelFieldName() {
        return this.relFieldName;
    }

    // ======= Some Convenience Oriented Factory Methods =======
    public static List<ModelKeyMap> makeKeyMapList(String fieldName1) {
        return UtilMisc.toList(new ModelKeyMap(fieldName1, null));
    }
    public static List<ModelKeyMap> makeKeyMapList(String fieldName1, String relFieldName1) {
        return UtilMisc.toList(new ModelKeyMap(fieldName1, relFieldName1));
    }
    public static List<ModelKeyMap> makeKeyMapList(String fieldName1, String relFieldName1, String fieldName2, String relFieldName2) {
        return UtilMisc.toList(new ModelKeyMap(fieldName1, relFieldName1), new ModelKeyMap(fieldName2, relFieldName2));
    }
    public static List<ModelKeyMap> makeKeyMapList(String fieldName1, String relFieldName1, String fieldName2, String relFieldName2, String fieldName3, String relFieldName3) {
        return UtilMisc.toList(new ModelKeyMap(fieldName1, relFieldName1), new ModelKeyMap(fieldName2, relFieldName2), new ModelKeyMap(fieldName3, relFieldName3));
    }

    @Override
    public int hashCode() {
        return this.fullName.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ModelKeyMap) {
            ModelKeyMap otherKeyMap = (ModelKeyMap) other;
            return this.fullName.equals(otherKeyMap.fullName);
        }
        return false;
    }

    // TODO: Externalize this.
    public Element toXmlElement(Document document) {
        Element root = document.createElement("key-map");
        root.setAttribute("field-name", this.getFieldName());
        if (!this.getFieldName().equals(this.getRelFieldName())) {
            root.setAttribute("rel-field-name", this.getRelFieldName());
        }
        return root;
    }

    @Override
    public int compareTo(ModelKeyMap other) {
        return this.fullName.compareTo(other.fullName);
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
