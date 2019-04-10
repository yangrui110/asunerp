
package com.hanlin.fadp.service.engine;

import java.util.Map;

import com.hanlin.fadp.service.GenericRequester;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;

/**
 * InterfaceEngine.java
 */
public class InterfaceEngine implements GenericEngine {

    public InterfaceEngine(ServiceDispatcher dispatcher) { }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        throw new GenericServiceException("Interface services cannot be invoked; try invoking an implementing service.");
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
       throw new GenericServiceException("Interface services cannot be invoked; try invoking an implementing service.");
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runAsync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map, com.hanlin.fadp.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, GenericRequester requester, boolean persist) throws GenericServiceException {
       throw new GenericServiceException("Interface services cannot be invoked; try invoking an implementing service.");
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runAsync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, boolean persist) throws GenericServiceException {
        throw new GenericServiceException("Interface services cannot be invoked; try invoking an implementing service.");
    }

    public void sendCallbacks(ModelService modelService, Map<String, Object> context, int mode) throws GenericServiceException {
    }

    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Map<String, Object> result, int mode) throws GenericServiceException {
    }

    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Throwable t, int mode) throws GenericServiceException {
    }
}
