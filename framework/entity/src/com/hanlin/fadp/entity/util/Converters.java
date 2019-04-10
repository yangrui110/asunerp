
package com.hanlin.fadp.entity.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hanlin.fadp.base.conversion.AbstractConverter;
import com.hanlin.fadp.base.conversion.ConversionException;
import com.hanlin.fadp.base.conversion.ConverterLoader;
import com.hanlin.fadp.base.lang.JSON;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.model.ModelFieldType;

/** Entity Engine <code>Converter</code> classes. */
public class Converters implements ConverterLoader {

    public static class JSONToGenericValue extends AbstractConverter<JSON, GenericValue> {
        public JSONToGenericValue() {
            super(JSON.class, GenericValue.class);
        }

        public GenericValue convert(JSON obj) throws ConversionException {
            Map<String, Object> fieldMap;
            try {
                fieldMap = UtilGenerics.<Map<String, Object>>cast(obj.toObject(Map.class));
                String delegatorName = (String) fieldMap.remove("_DELEGATOR_NAME_");
                String entityName = (String) fieldMap.remove("_ENTITY_NAME_");
                if (delegatorName == null || entityName == null) {
                    throw new ConversionException("Invalid JSON object");
                }
                Delegator delegator = DelegatorFactory.getDelegator(delegatorName);
                GenericValue value = delegator.makeValue(entityName);
                for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    ModelField field = value.getModelEntity().getField(fieldName);
                    ModelFieldType type = delegator.getEntityFieldType(value.getModelEntity(), field.getType());
                    value.set(fieldName, ObjectType.simpleTypeConvert(fieldValue, type.getJavaType(), null, null));
                }
                return value;
            } catch (ConversionException e) {
                throw e;
            } catch (Exception e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class GenericValueToJSON extends AbstractConverter<GenericValue, JSON> {
        public GenericValueToJSON() {
            super(GenericValue.class, JSON.class);
        }

        public JSON convert(GenericValue obj) throws ConversionException {
            Map<String, Object> fieldMap = new HashMap<String, Object>(obj);
            fieldMap.put("_DELEGATOR_NAME_", obj.getDelegator().getDelegatorName());
            fieldMap.put("_ENTITY_NAME_", obj.getEntityName());
            try {
                return JSON.from(fieldMap);
            } catch (IOException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class GenericValueToList extends AbstractConverter<GenericValue, List<GenericValue>> {
        public GenericValueToList() {
            super(GenericValue.class, List.class);
        }

        public List<GenericValue> convert(GenericValue obj) throws ConversionException {
            List<GenericValue> tempList = new LinkedList<GenericValue>();
            tempList.add(obj);
            return tempList;
        }
    }

    public static class GenericValueToSet extends AbstractConverter<GenericValue, Set<GenericValue>> {
        public GenericValueToSet() {
            super(GenericValue.class, Set.class);
        }

        public Set<GenericValue> convert(GenericValue obj) throws ConversionException {
            Set<GenericValue> tempSet = new HashSet<GenericValue>();
            tempSet.add(obj);
            return tempSet;
        }
    }

    public static class GenericValueToString extends AbstractConverter<GenericValue, String> {
        public GenericValueToString() {
            super(GenericValue.class, String.class);
        }

        public String convert(GenericValue obj) throws ConversionException {
            return obj.toString();
        }
    }

    public static class NullFieldToObject extends AbstractConverter<GenericEntity.NullField, Object> {
        public NullFieldToObject() {
            super(GenericEntity.NullField.class, Object.class);
        }

        public Object convert(GenericEntity.NullField obj) throws ConversionException {
            return null;
        }
    }

    public static class ObjectToNullField extends AbstractConverter<Object, GenericEntity.NullField> {
        public ObjectToNullField() {
            super(Object.class, GenericEntity.NullField.class);
        }

        public GenericEntity.NullField convert(Object obj) throws ConversionException {
            return GenericEntity.NULL_FIELD;
        }
    }

    public void loadConverters() {
        com.hanlin.fadp.base.conversion.Converters.loadContainedConverters(Converters.class);
    }
}
