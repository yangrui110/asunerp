
import com.hanlin.fadp.petrescence.datasource.EntityEngineUtil;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestReflect {


    static class EntityConfig {

        public static final String ENTITY_ENGINE_XML_FILENAME = "entityengine.xml";

        private static final String module = EntityConfig.class.getName();


        //    private static final EntityConfig instance = createNewInstance();//原版

        private final List<String> resourceLoaderList; // <resource-loader>

        private static final EntityConfig instance = createNewInstance();

        private static EntityConfig createNewInstance() {
            EntityConfig entityConfig = new EntityConfig();
            return entityConfig;
        }

        private EntityConfig()  {
            resourceLoaderList=new ArrayList<>();
            resourceLoaderList.add("hello");
        }
        public static final EntityConfig getInstance()   {
            return instance;
        }

    }

    @Test
    public  void main() throws Exception {
        EntityConfig newInstance =new EntityConfig();
        newInstance.resourceLoaderList.add(0,"word");

//        Field[] declaredFields = EntityConfig.class.getDeclaredFields();
//        for (int i = 0; i < declaredFields.length; i++) {
//            Field field = declaredFields[i];
//            field.setAccessible(true);
//            boolean isStatic = Modifier.isStatic(field.getModifiers());
//            if(!isStatic){
//                EntityEngineUtil.changeFinalValue(EntityConfig.getInstance(),field,field.get(newInstance));
//            }
//
//        }
        EntityEngineUtil.changeFinalValue(null,EntityConfig.class.getDeclaredField("instance"),newInstance);
        EntityConfig instance = EntityConfig.getInstance();
        String str = instance.resourceLoaderList.get(0);
        assert str.equals("word");

    }
}
