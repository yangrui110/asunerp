
package com.hanlin.fadp.service.group;

import java.util.Map;

import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.engine.GenericAsyncEngine;

/**
 * ServiceGroupEngine.java
 */
public class ServiceGroupEngine extends GenericAsyncEngine {

    /**
     * Constructor for ServiceGroupEngine.
     * @param dispatcher
     */
    public ServiceGroupEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        GroupModel groupModel = modelService.internalGroup;
        if (groupModel == null) {
            groupModel = ServiceGroupReader.getGroupModel(this.getLocation(modelService));
        }
        if (groupModel == null) {
            throw new GenericServiceException("GroupModel was null; not a valid ServiceGroup!");
        }

        return groupModel.run(dispatcher, localName, context);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }
}
