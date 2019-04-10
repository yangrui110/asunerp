
package com.hanlin.fadp.service.engine;

import java.util.Map;

import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;

/**
 * RouteEngine.java
 */
public class RouteEngine extends GenericAsyncEngine {

    public RouteEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        return modelService.makeValid(context, ModelService.OUT_PARAM);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        return;
    }

    @Override
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, int mode) throws GenericServiceException {
    }

    @Override
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Map<String, Object> result, int mode) throws GenericServiceException {
    }

    @Override
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Throwable t, int mode) throws GenericServiceException {
    }
}
