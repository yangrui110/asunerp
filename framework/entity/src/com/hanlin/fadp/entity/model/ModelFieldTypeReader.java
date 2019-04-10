
package com.hanlin.fadp.entity.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.MainResourceHandler;
import com.hanlin.fadp.base.config.ResourceHandler;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilTimer;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.GenericEntityConfException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.config.model.FieldType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Field Type Definition Reader.
 *
 */
@SuppressWarnings("serial")
public class ModelFieldTypeReader implements Serializable {

    public static final String module = ModelFieldTypeReader.class.getName();
    protected static final UtilCache<String, ModelFieldTypeReader> readers = UtilCache.createUtilCache("entity.ModelFieldTypeReader", 0, 0);

    protected static Map<String, ModelFieldType> createFieldTypeCache(Element docElement, String location) {
        docElement.normalize();
        Map<String, ModelFieldType> fieldTypeMap = new HashMap<String, ModelFieldType>();
        List<? extends Element> fieldTypeList = UtilXml.childElementList(docElement, "field-type-def");
        for (Element curFieldType: fieldTypeList) {
            String fieldTypeName = curFieldType.getAttribute("type");
            if (UtilValidate.isEmpty(fieldTypeName)) {
                Debug.logError("Invalid field-type element, type attribute is missing in file " + location, module);
            } else {
                ModelFieldType fieldType = new ModelFieldType(curFieldType);
                fieldTypeMap.put(fieldTypeName.intern(), fieldType);
            }
        }
        return fieldTypeMap;
    }

    public static ModelFieldTypeReader getModelFieldTypeReader(String helperName) {
        Datasource datasourceInfo = EntityConfig.getDatasource(helperName);
        if (datasourceInfo == null) {
            throw new IllegalArgumentException("Could not find a datasource/helper with the name " + helperName);
        }
        String tempModelName = datasourceInfo.getFieldTypeName();
        ModelFieldTypeReader reader = readers.get(tempModelName);
        while (reader == null) {
            FieldType fieldTypeInfo = null;
            try {
                fieldTypeInfo = EntityConfig.getInstance().getFieldType(tempModelName);
            } catch (GenericEntityConfException e) {
                Debug.logWarning(e, "Exception thrown while getting field type config: ", module);
            }
            if (fieldTypeInfo == null) {
                throw new IllegalArgumentException("Could not find a field-type definition with name \"" + tempModelName + "\"");
            }
            ResourceHandler fieldTypeResourceHandler = new MainResourceHandler(EntityConfig.ENTITY_ENGINE_XML_FILENAME, fieldTypeInfo.getLoader(), fieldTypeInfo.getLocation());
            UtilTimer utilTimer = new UtilTimer();
            utilTimer.timerString("[ModelFieldTypeReader.getModelFieldTypeReader] Reading field types from " + fieldTypeResourceHandler.getLocation());
            Document document = null;
            try {
                document = fieldTypeResourceHandler.getDocument();
            } catch (GenericConfigException e) {
                Debug.logError(e, module);
                throw new IllegalStateException("Error loading field type file " + fieldTypeResourceHandler.getLocation());
            }
            Map<String, ModelFieldType> fieldTypeMap = createFieldTypeCache(document.getDocumentElement(), fieldTypeResourceHandler.getLocation());
            reader = readers.putIfAbsentAndGet(tempModelName, new ModelFieldTypeReader(fieldTypeMap));
            utilTimer.timerString("[ModelFieldTypeReader.getModelFieldTypeReader] Read " + fieldTypeMap.size() + " field types");
        }
        return reader;
    }

    protected final Map<String, ModelFieldType> fieldTypeCache;

    public ModelFieldTypeReader(Map<String, ModelFieldType> fieldTypeMap) {
        this.fieldTypeCache = fieldTypeMap;
    }

    /** Creates a Collection with all of the ModelFieldType names
     * @return A Collection of ModelFieldType names
     */
    public Collection<String> getFieldTypeNames() {
        return this.fieldTypeCache.keySet();
    }

    /** Creates a Collection with all of the ModelFieldTypes
     * @return A Collection of ModelFieldTypes
     */
    public Collection<ModelFieldType> getFieldTypes() {
        return this.fieldTypeCache.values();
    }

    /** Gets an FieldType object based on a definition from the specified XML FieldType descriptor file.
     * @param fieldTypeName The fieldTypeName of the FieldType definition to use.
     * @return An FieldType object describing the specified fieldType of the specified descriptor file.
     */
    public ModelFieldType getModelFieldType(String fieldTypeName) {
        return this.fieldTypeCache.get(fieldTypeName);
    }

}
