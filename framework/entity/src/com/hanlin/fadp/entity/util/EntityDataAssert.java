
package com.hanlin.fadp.entity.util;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericPK;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.model.ModelEntity;
import org.xml.sax.SAXException;

/**
 * Some utility routines for loading seed data.
 */
public class EntityDataAssert {

    public static final String module = EntityDataAssert.class.getName();

    public static int assertData(URL dataUrl, Delegator delegator, List<Object> errorMessages) throws GenericEntityException, SAXException, ParserConfigurationException, IOException {
        int rowsChecked = 0;

        if (dataUrl == null) {
            String errMsg = "Cannot assert/check data, dataUrl was null";
            errorMessages.add(errMsg);
            Debug.logError(errMsg, module);
            return 0;
        }

        Debug.logVerbose("Loading XML Resource: " + dataUrl.toExternalForm(), module);

        try {
            for (GenericValue checkValue: delegator.readXmlDocument(dataUrl)) {
                checkSingleValue(checkValue, delegator, errorMessages);
                rowsChecked++;
            }
        } catch (GenericEntityException e) {
            String xmlError = "Error checking/asserting XML Resource: " + dataUrl.toExternalForm() + "; Error was: " + e.getMessage();
            Debug.logError(e, xmlError, module);
            // instead of adding this as a message, throw the real exception; then caller has more control
            //errorMessages.add(xmlError);
            throw e;
        }

        return rowsChecked;
    }

    public static void checkValueList(List<GenericValue> valueList, Delegator delegator, List<Object> errorMessages) throws GenericEntityException {
        if (valueList == null) return;

        for (GenericValue checkValue : valueList) {
            checkSingleValue(checkValue, delegator, errorMessages);
        }
    }

    public static void checkSingleValue(GenericValue checkValue, Delegator delegator, List<Object> errorMessages) throws GenericEntityException {
        if (checkValue == null) {
            errorMessages.add("Got a value to check was null");
            return;
        }
        // to check get the PK, find by that, compare all fields
        GenericPK checkPK = null;

        try {
            checkPK = checkValue.getPrimaryKey();
            GenericValue currentValue = EntityQuery.use(delegator).from(checkPK.getEntityName()).where(checkPK).queryOne();
            if (currentValue == null) {
                errorMessages.add("Entity [" + checkPK.getEntityName() + "] record not found for pk: " + checkPK);
                return;
            }

            ModelEntity modelEntity = checkValue.getModelEntity();
            for (String nonpkFieldName: modelEntity.getNoPkFieldNames()) {
                // skip the fields the entity engine maintains
                if (ModelEntity.CREATE_STAMP_FIELD.equals(nonpkFieldName) || ModelEntity.CREATE_STAMP_TX_FIELD.equals(nonpkFieldName) ||
                        ModelEntity.STAMP_FIELD.equals(nonpkFieldName) || ModelEntity.STAMP_TX_FIELD.equals(nonpkFieldName)) {
                    continue;
                }

                Object checkField = checkValue.get(nonpkFieldName);
                Object currentField = currentValue.get(nonpkFieldName);

                if (checkField != null && !checkField.equals(currentField)) {
                    errorMessages.add("Field [" + modelEntity.getEntityName() + "." + nonpkFieldName +
                            "] did not match; file value [" + checkField + "], db value [" + currentField + "] pk [" + checkPK + "]");
                }
            }
        } catch (GenericEntityException e) {
            throw e;
        } catch (Throwable t) {
            String errMsg;
            if (checkPK == null) {
                errMsg = "Error checking value [" + checkValue + "]: " + t.toString();
            } else {
                errMsg = "Error checking entity [" + checkPK.getEntityName() + "] with pk [" + checkPK.getAllFields() + "]: " + t.toString();
            }
            errorMessages.add(errMsg);
            Debug.logError(t, errMsg, module);
        }
    }
}
