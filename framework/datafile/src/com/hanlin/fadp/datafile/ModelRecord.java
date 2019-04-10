
package com.hanlin.fadp.datafile;


import java.util.ArrayList;
import java.util.List;


/**
 *  ModelRecord
 */

public class ModelRecord {

    public static final String LIMIT_ONE = "one";
    public static final String LIMIT_MANY = "many";

    /** The name of the Record */
    public String name = "";

    /** The type-code of the Record */
    public String typeCode = "";

    /** The minimum type-code of the Record, an alternative to the single type code */
    public String tcMin = "";
    public long tcMinNum = -1;

    /** The maximum type-code of the Record, an alternative to the single type code */
    public String tcMax = "";
    public long tcMaxNum = -1;

    /** specifies whether or not the type min and max are numbers, if so does a number compare, otherwise a String compare */
    public boolean tcIsNum = true;

    /** The position of the type-code of the Record */
    public int tcPosition = -1;

    /** The length of the type-code of the Record - optional */
    public int tcLength = -1;

    /** A free form description of the Record */
    public String description = "";

    /** The name of the parent record for this record, if any */
    public String parentName = "";

    /** The number limit of records to go under the parent, may be one or many */
    public String limit = "";

    public ModelRecord parentRecord = null;
    public List<ModelRecord> childRecords = new ArrayList<ModelRecord>();

    /** List of the fields that compose this record */
    public List<ModelField> fields = new ArrayList<ModelField>();

    ModelField getModelField(String fieldName) {
        for (ModelField curField: fields) {

            if (curField.name.equals(fieldName)) {
                return curField;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<ModelField> getFields() {
        return fields;
    }

    public void setFields(List<ModelField> fields) {
        this.fields = fields;
    }
}

