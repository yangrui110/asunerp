

package com.hanlin.fadp.entityext.eca;

import org.w3c.dom.Element;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.model.ModelUtil;

import java.util.Map;

/**
 * ServiceEcaSetField
 */
public final class EntityEcaSetField {

    public static final String module = EntityEcaSetField.class.getName();

    private final String fieldName;
    private final String envName;
    private final String value;
    private final String format;

    public EntityEcaSetField(Element set) {
        this.fieldName = set.getAttribute("field-name");
        this.envName = set.getAttribute("env-name");
        this.value = set.getAttribute("value");
        this.format = set.getAttribute("format");
    }

    public void eval(Map<String, Object> context) {
        if (!fieldName.isEmpty()) {
            String valueExpanded = FlexibleStringExpander.expandString(value, context);
            if (!valueExpanded.isEmpty()) {
                context.put(fieldName, this.format(valueExpanded, context));
            } else if (!envName.isEmpty() && context.get(envName) != null) {
                context.put(fieldName, this.format((String) context.get(envName), context));
            }
        }
    }

    private Object format(String s, Map<String, ? extends Object> c) {
        if (UtilValidate.isEmpty(s) || UtilValidate.isEmpty(format)) {
            return s;
        }

        // string formats
        if ("append".equalsIgnoreCase(format) && envName != null) {
            StringBuilder newStr = new StringBuilder();
            if (c.get(envName) != null) {
                newStr.append(c.get(envName));
            }
            newStr.append(s);
            return newStr.toString();
        }
        if ("to-upper".equalsIgnoreCase(format)) {
            return s.toUpperCase();
        }
        if ("to-lower".equalsIgnoreCase(format)) {
            return s.toLowerCase();
        }
        if ("hash-code".equalsIgnoreCase(format)) {
            return Integer.valueOf(s.hashCode());
        }
        if ("long".equalsIgnoreCase(format)) {
            return Long.valueOf(s);
        }
        if ("double".equalsIgnoreCase(format)) {
            return Double.valueOf(s);
        }

        // entity formats
        if ("upper-first-char".equalsIgnoreCase(format)) {
            return ModelUtil.upperFirstChar(s);
        }
        if ("lower-first-char".equalsIgnoreCase(format)) {
            return ModelUtil.lowerFirstChar(s);
        }
        if ("db-to-java".equalsIgnoreCase(format)) {
            return ModelUtil.dbNameToVarName(s);
        }
        if ("java-to-db".equalsIgnoreCase(format)) {
            return ModelUtil.javaNameToDbName(s);
        }

        Debug.logWarning("Format function not found [" + format + "] return string unchanged - " + s, module);
        return s;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getRValue() {
        if (!this.value.isEmpty()) {
            return "\"".concat(this.value).concat("\"");
        }
        return this.envName;
    }
}
