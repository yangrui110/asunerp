package com.hanlin.fadp.entity.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.util.EntityUtil;

/**
 * Encapsulates simple expressions used for specifying queries
 *
 */
@SuppressWarnings("serial")
public final class EntityFieldMap extends EntityConditionListBase<EntityExpr> {

    protected final Map<String, ? extends Object> fieldMap;

    /**
     * @Title: makeConditionList 
     * @Description: TODO
     * @param fieldMap 查询参数，成对出现，前者为条件，后者为条件值
     * @param op 比较操作符
     * @return: List<EntityExpr> 字符串+比较操作符+字符串取值形成的一个list
     */
    public static <V> List<EntityExpr> makeConditionList(Map<String, V> fieldMap, EntityComparisonOperator<?,V> op) {
        if (fieldMap == null) {
            return Collections.emptyList();
        }
        List<EntityExpr> list = new ArrayList<EntityExpr>(fieldMap.size());
        for (Map.Entry<String, ? extends Object> entry: fieldMap.entrySet()) {
            list.add(EntityCondition.makeCondition(entry.getKey(), op, entry.getValue()));
        }
        return list;
    }

    public <V> EntityFieldMap(EntityComparisonOperator<?,?> compOp, EntityJoinOperator joinOp, V... keysValues) {
        this(EntityUtil.makeFields(keysValues), UtilGenerics.<EntityComparisonOperator<String,V>>cast(compOp), joinOp);
    }

    public <V> EntityFieldMap(Map<String, V> fieldMap, EntityComparisonOperator<?,?> compOp, EntityJoinOperator joinOp) {
        super(makeConditionList(fieldMap, UtilGenerics.<EntityComparisonOperator<String,V>>cast(compOp)), joinOp);
        this.fieldMap = fieldMap == null ? Collections.<String, Object>emptyMap() : fieldMap;
    }

    public Object getField(String name) {
        return this.fieldMap.get(name);
    }

    public boolean containsField(String name) {
        return this.fieldMap.containsKey(name);
    }

    public Iterator<String> getFieldKeyIterator() {
        return Collections.unmodifiableSet(this.fieldMap.keySet()).iterator();
    }

    public Iterator<Map.Entry<String, ? extends Object>> getFieldEntryIterator() {
        return Collections.<Map.Entry<String, ? extends Object>>unmodifiableSet(this.fieldMap.entrySet()).iterator();
    }

    @Override
    public void accept(EntityConditionVisitor visitor) {
        visitor.acceptEntityFieldMap(this);
    }
}
