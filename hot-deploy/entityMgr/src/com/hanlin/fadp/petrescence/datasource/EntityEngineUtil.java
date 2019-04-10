package com.hanlin.fadp.petrescence.datasource;

import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class EntityEngineUtil {

    public synchronized static void reInitEntityConfig() throws Exception {
        UtilCache.clearAllCaches();
        Constructor<EntityConfig> constructor = EntityConfig.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        EntityConfig newInstance =constructor.newInstance();

//        changeFinalValue(EntityConfig.class,EntityConfig.class.getDeclaredField("instance"),null);
        Field[] declaredFields = EntityConfig.class.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            field.setAccessible(true);
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if(!isStatic){
                EntityEngineUtil.changeFinalValue(EntityConfig.getInstance(),field,field.get(newInstance));
            }

        }
//        changeFinalValue(null,EntityConfig.class.getDeclaredField("instance"),newInstance);
        System.out.println("newInstance.getDatasourceMap() = " + newInstance.getDatasourceMap().keySet());
        System.out.println("EntityConfig.getInstance().getDatasourceMap() = " + EntityConfig.getInstance().getDatasourceMap().keySet());


    }
    public static void reInitDelegator(String delegatorName) throws Exception {
        reInitEntityConfig();
        Field delegators = DelegatorFactory.class.getDeclaredField("delegators");
        delegators.setAccessible(true);
        ConcurrentHashMap<String, Future<Delegator>> delegatorMap= (ConcurrentHashMap<String, Future<Delegator>>) delegators.get(null);
        delegatorMap.remove(delegatorName);
        UtilCache<Object, Object> modelReaderCache = UtilCache.findCache("entity.ModelReader");
        modelReaderCache.remove(delegatorName);
        UtilCache<Object, Object> modelGroupReaderCache = UtilCache.findCache("entity.ModelGroupReader");
        modelGroupReaderCache.remove(delegatorName);
        DelegatorFactory.getDelegator(delegatorName);

    }

    public static void correctFieldType(ModelEntity modelEntity ) throws Exception {
        Iterator<ModelField> modelFields = modelEntity.getFieldsIterator();
        while (modelFields.hasNext()){
            ModelField modelField = modelFields.next();
            String type = modelField.getType();
            if (type.startsWith("invalid-")){
                type=type.replace("invalid-","");
                String[] split = type.split(":");
                String slqTypeName=split[0];
                int length=Integer.valueOf(split[1]);
                int precision=Integer.valueOf(split[2]);
                String correctedType = correctFieldType(slqTypeName, length, precision);
                Field typeField = ModelField.class.getDeclaredField("type");
                changeFinalValue(modelField,typeField,correctedType);
            }
        }
    }
    public static String correctFieldType(String sqlTypeName, int length, int precision){
        if (sqlTypeName.equalsIgnoreCase("BIGINT") ) {

            return "numeric";
        }  else {
            return "invalid-" + sqlTypeName + ":" + length + ":" + precision;
        }
    }
    public static void changeFinalValue(Object obj,Field field,Object fieldValue) throws Exception {


        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field,field.getModifiers()&~Modifier.FINAL);


        field.setAccessible(true);
        field.set(obj,fieldValue);

//        modifiersField.setInt(field,field.getModifiers()|Modifier.FINAL);
    }

}
