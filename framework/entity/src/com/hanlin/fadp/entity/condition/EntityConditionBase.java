
package com.hanlin.fadp.entity.condition;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.jdbc.SqlJdbcUtil;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.model.ModelViewEntity;
import com.hanlin.fadp.entity.model.ModelViewEntity.ModelAlias;

/**
 * Represents the conditions to be used to constrain a query
 * <br/>An EntityCondition can represent various type of constraints, including:
 * <ul>
 *  <li>EntityConditionList: a list of EntityConditions, combined with the operator specified
 *  <li>EntityExpr: for simple expressions or expressions that combine EntityConditions
 *  <li>EntityFieldMap: a map of fields where the field (key) equals the value, combined with the operator specified
 * </ul>
 * These can be used in various combinations using the EntityConditionList and EntityExpr objects.
 *
 */
@SuppressWarnings("serial")
public abstract class EntityConditionBase implements Serializable {

    public static final List<?> emptyList = Collections.emptyList();
    public static final Map<?,?> _emptyMap = Collections.emptyMap();
    public static final Map<String, String> emptyAliases = Collections.unmodifiableMap(new HashMap<String, String>());

    protected ModelField getField(ModelEntity modelEntity, String fieldName) {
        ModelField modelField = null;
        if (modelEntity != null) {
            modelField = modelEntity.getField(fieldName);
        }
        return modelField;
    }

    protected String getColName(Map<String, String> tableAliases, ModelEntity modelEntity, String fieldName, boolean includeTableNamePrefix, Datasource datasourceInfo) {
        if (modelEntity == null) return fieldName;
        return getColName(tableAliases, modelEntity, getField(modelEntity, fieldName), fieldName, includeTableNamePrefix, datasourceInfo);
    }

    protected String getColName(ModelField modelField, String fieldName) {
        String colName = null;
        if (modelField != null) {
            colName = modelField.getColValue();
        } else {
            colName = fieldName;
        }
        return colName;
    }

    protected String getColName(Map<String, String> tableAliases, ModelEntity modelEntity, ModelField modelField, String fieldName, boolean includeTableNamePrefix, Datasource datasourceInfo) {
        if (modelEntity == null || modelField == null) return fieldName;

        // if this is a view entity and we are configured to alias the views, use the alias here instead of the composite (ie table.column) field name
        if (datasourceInfo != null && datasourceInfo.getAliasViewColumns() && modelEntity instanceof ModelViewEntity) {
            ModelViewEntity modelViewEntity = (ModelViewEntity) modelEntity;
            ModelAlias modelAlias = modelViewEntity.getAlias(fieldName);
            if (modelAlias != null) {
                return modelAlias.getColAlias();
            }
        }

        String colName = getColName(modelField, fieldName);
        if (includeTableNamePrefix && datasourceInfo != null) {
            String tableName = modelEntity.getTableName(datasourceInfo);
            if (tableAliases.containsKey(tableName)) {
                tableName = tableAliases.get(tableName);
            }
            colName = tableName + "." + colName;
        }
        return colName;
    }

    protected void addValue(StringBuilder buffer, ModelField field, Object value, List<EntityConditionParam> params) {
        SqlJdbcUtil.addValue(buffer, params == null ? null : field, value, params);
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("equals:" + getClass().getName());
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode: " + getClass().getName());
    }

    protected static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    protected static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static Boolean castBoolean(boolean result) {
        return result ? Boolean.TRUE : Boolean.FALSE;
    }
}
