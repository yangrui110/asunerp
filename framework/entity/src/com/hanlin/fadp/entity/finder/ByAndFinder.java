
package com.hanlin.fadp.entity.finder;

import java.util.HashMap;
import java.util.Map;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelFieldTypeReader;
import org.w3c.dom.Element;

/**
 * Uses the delegator to find entity values by a and
 *
 */
@SuppressWarnings("serial")
public class ByAndFinder extends ListFinder {

    public static final String module = ByAndFinder.class.getName();

    protected Map<FlexibleMapAccessor<Object>, Object> fieldMap;

    public ByAndFinder(Element element) {
        super(element, "and");

        // process field-map
        this.fieldMap = EntityFinderUtil.makeFieldMap(element);
    }

    @Override
    public EntityCondition getWhereEntityCondition(Map<String, Object> context, ModelEntity modelEntity, ModelFieldTypeReader modelFieldTypeReader) {
        // create the by and map
        Map<String, Object> entityContext = new HashMap<String, Object>();
        EntityFinderUtil.expandFieldMapToContext(this.fieldMap, context, entityContext);
        // then convert the types...
        modelEntity.convertFieldMapInPlace(entityContext, modelFieldTypeReader);
        return EntityCondition.makeCondition(entityContext);
    }
}

