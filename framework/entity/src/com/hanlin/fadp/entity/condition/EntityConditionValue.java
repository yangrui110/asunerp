
package com.hanlin.fadp.entity.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericModelException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;

/**
 * Base class for condition expression values.
 *
 */
@SuppressWarnings("serial")
public abstract class EntityConditionValue extends EntityConditionBase {

    public static EntityConditionValue CONSTANT_NUMBER(Number value) { return new ConstantNumberValue(value); }
    public static final class ConstantNumberValue extends EntityConditionValue {
        private Number value;

        private ConstantNumberValue(Number value) {
            this.value = value;
        }

        @Override
        public void accept(EntityConditionVisitor visitor) {
            visitor.acceptEntityConditionValue(this);
        }

        @Override
        public void addSqlValue(StringBuilder sql, Map<String, String> tableAliases, ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams, boolean includeTableNamePrefix, Datasource datasourceinfo) {
            sql.append(value);
        }

        @Override
        public EntityConditionValue freeze() {
            return this;
        }

        @Override
        public ModelField getModelField(ModelEntity modelEntity) {
            return null;
        }

        @Override
        public Object getValue(Delegator delegator, Map<String, ? extends Object> map) {
            return value;
        }

        @Override
        public void validateSql(com.hanlin.fadp.entity.model.ModelEntity modelEntity) {
        }

        @Override
        public void visit(EntityConditionVisitor visitor) {
            visitor.acceptObject(value);
        }
    }

    public abstract ModelField getModelField(ModelEntity modelEntity);

    public void addSqlValue(StringBuilder sql, ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams, boolean includeTableNamePrefix,
            Datasource datasourceinfo) {
        addSqlValue(sql, emptyAliases, modelEntity, entityConditionParams, includeTableNamePrefix, datasourceinfo);
    }

    public abstract void addSqlValue(StringBuilder sql, Map<String, String> tableAliases, ModelEntity modelEntity, List<EntityConditionParam> entityConditionParams,
            boolean includeTableNamePrefix, Datasource datasourceinfo);

    public abstract void validateSql(ModelEntity modelEntity) throws GenericModelException;

    public Object getValue(GenericEntity entity) {
        if (entity == null) {
            return null;
        }
        return getValue(entity.getDelegator(), entity);
    }

    public abstract Object getValue(Delegator delegator, Map<String, ? extends Object> map);

    public abstract EntityConditionValue freeze();

    public abstract void visit(EntityConditionVisitor visitor);

    public void accept(EntityConditionVisitor visitor) {
        throw new IllegalArgumentException("accept not implemented");
    }

    public void toString(StringBuilder sb) {
        addSqlValue(sb, null, new ArrayList<EntityConditionParam>(), false, null);
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        toString(sql);
        return sql.toString();
    }
}
