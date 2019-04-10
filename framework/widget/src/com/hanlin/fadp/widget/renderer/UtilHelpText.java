
package com.hanlin.fadp.widget.renderer;

import java.util.Locale;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelReader;

/**
 * Util for working with Help Text
 */
public class UtilHelpText {

    public static final String module = UtilHelpText.class.getName();

    /**
     * Find the help text associated with an entity field.
     * 
     * @param entityName the entity name
     * @param fieldName the field name
     * @param delegator the delegator
     * @param locale the locale
     * @return the help text, or the resource propertyName if no help text exists
     */
    public static String getEntityFieldDescription(final String entityName, final String fieldName, final Delegator delegator, final Locale locale) {

        if (UtilValidate.isEmpty(entityName)) {
            // Debug.logWarning("entityName [" + entityName + "] is empty", module);
            return "";
        }
        if (UtilValidate.isEmpty(fieldName)) {
            Debug.logWarning("fieldName [" + fieldName + "] is empty", module);
            return "";
        }
        ModelReader reader = delegator.getModelReader();
        ModelEntity entity = null;
        try {
            if (!reader.getEntityNames().contains(entityName)) {
                Debug.logWarning("couldn't find entityName [" + entityName + "]", module);
                return "";
            }
            entity = reader.getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting help text for entity=" + entityName + " field " + fieldName, module);
            return "";
        }
        String entityResourceName = entity.getDefaultResourceName();
        String messageId = "FieldDescription." + entityName + "." + fieldName;
        String fieldDescription = UtilProperties.getMessage(entityResourceName, messageId, locale);
        if (fieldDescription.equals(messageId)) {
            messageId = "FieldDescription." + fieldName;
            if (Debug.verboseOn()) {
                Debug.logVerbose("No help text found in [" + entityResourceName + "] with key [" + messageId + "], Trying with: " + messageId, module);
            }
            fieldDescription = UtilProperties.getMessage(entityResourceName, messageId, locale);
            if (fieldDescription.equals(messageId)) {
                if (Debug.verboseOn()) {
                    Debug.logVerbose("No help text found in [" + entityResourceName + "] with key [" + messageId + "]", module);
                }
                return "";
            }
        }
        return fieldDescription;
    }
}
