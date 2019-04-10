
package com.hanlin.fadp.entityext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelViewEntity;
import com.hanlin.fadp.entity.util.EntityQuery;

/**
 * EntityEcaUtil
 */
public class EntityGroupUtil {

    public static final String module = EntityGroupUtil.class.getName();

    public static Set<String> getEntityNamesByGroup(String entityGroupId, Delegator delegator, boolean requireStampFields) throws GenericEntityException {
        Set<String> entityNames = new HashSet<String>();

        List<GenericValue> entitySyncGroupIncludes = EntityQuery.use(delegator).from("EntityGroupEntry").where("entityGroupId", entityGroupId).queryList();
        List<ModelEntity> modelEntities = getModelEntitiesFromRecords(entitySyncGroupIncludes, delegator, requireStampFields);
        for (ModelEntity modelEntity: modelEntities) {
            entityNames.add(modelEntity.getEntityName());
        }

        return entityNames;
    }

    public static List<ModelEntity> getModelEntitiesFromRecords(List<GenericValue> entityGroupEntryValues, Delegator delegator, boolean requireStampFields) throws GenericEntityException {
        List<ModelEntity> entityModelToUseList = new LinkedList<ModelEntity>();

        for (String entityName: delegator.getModelReader().getEntityNames()) {
            ModelEntity modelEntity = delegator.getModelEntity(entityName);

            // if view-entity, throw it out
            if (modelEntity instanceof ModelViewEntity) {
                continue;
            }

            // if it doesn't have either or both of the two update stamp fields, throw it out
            if (requireStampFields && (!modelEntity.isField(ModelEntity.STAMP_FIELD) || !modelEntity.isField(ModelEntity.STAMP_TX_FIELD))) {
                continue;
            }

            // if there are no includes records, always include; otherwise check each one to make sure at least one matches
            if (entityGroupEntryValues.size() == 0) {
                entityModelToUseList.add(modelEntity);
            } else {
                // we have different types of include applications: ESIA_INCLUDE, ESIA_EXCLUDE, ESIA_ALWAYS
                // if we find an always we can break right there because this will always be include regardless of excludes, etc
                // if we find an include or exclude we have to finish going through the rest of them just in case there is something that overrides it (ie an exclude for an include or an always for an exclude)
                boolean matchesInclude = false;
                boolean matchesExclude = false;
                boolean matchesAlways = false;
                Iterator<GenericValue> entitySyncIncludeIter = entityGroupEntryValues.iterator();
                while (entitySyncIncludeIter.hasNext()) {
                    GenericValue entitySyncInclude = entitySyncIncludeIter.next();
                    String entityOrPackage = entitySyncInclude.getString("entityOrPackage");
                    boolean matches = false;
                    if (entityName.equals(entityOrPackage)) {
                        matches = true;
                    } else if (modelEntity.getPackageName().startsWith(entityOrPackage)) {
                        matches = true;
                    }

                    if (matches) {
                        if ("ESIA_INCLUDE".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesInclude = true;
                        } else if ("ESIA_EXCLUDE".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesExclude = true;
                        } else if ("ESIA_ALWAYS".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesAlways = true;
                            break;
                        }
                    }
                }

                if (matchesAlways || (matchesInclude && !matchesExclude)) {
                    // make sure this log message is not checked in uncommented:
                    //Debug.logInfo("In runEntitySync adding [" + modelEntity.getEntityName() + "] to list of Entities to sync", module);
                    entityModelToUseList.add(modelEntity);
                }
            }
        }

        return entityModelToUseList;
    }
}
