
package com.hanlin.fadp.entity.condition;

import java.io.Serializable;

import com.hanlin.fadp.entity.model.ModelField;

/**
 * Represents a single parameter to be used in the preparedStatement
 *
 */
@SuppressWarnings("serial")
public class EntityConditionParam implements Serializable {
    protected ModelField modelField;
    protected Object fieldValue;

    protected EntityConditionParam() {}

    public EntityConditionParam(ModelField modelField, Object fieldValue) {
        if (modelField == null) {
            throw new IllegalArgumentException("modelField cannot be null");
        }
        this.modelField = modelField;
        this.fieldValue = fieldValue;
    }

    public ModelField getModelField() {
        return modelField;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    @Override
    public String toString() {
        return modelField.getColName() + "=" + fieldValue.toString();
    }
}
