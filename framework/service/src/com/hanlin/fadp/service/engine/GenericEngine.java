
package com.hanlin.fadp.service.engine;

import java.util.Map;

import com.hanlin.fadp.service.GenericRequester;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;

/**
 * Generic Engine Interface
 */
public interface GenericEngine {

    public static final int ASYNC_MODE = 22;
    public static final int SYNC_MODE = 21;

    /**
     * Run the service synchronously and return the result.
     *
     * @param localName Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException;

    /**
     * Run the service synchronously and IGNORE the result.
     *
     * @param localName Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException;

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     *
     * @param localName Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, GenericRequester requester, boolean persist)
        throws GenericServiceException;

    /**
     * Run the service asynchronously and IGNORE the result.
     *
     * @param localName Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String localName, ModelService modelService, Map<String, Object> context, boolean persist) throws GenericServiceException;

    /**
     * Send the service callbacks
     * @param modelService Service model object
     * @param context Map of name, value pairs composing the context
     * @param mode Service mode (sync or async)
     * @throws GenericServiceException
     */
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, int mode) throws GenericServiceException;
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Map<String, Object> result, int mode) throws GenericServiceException;
    public void sendCallbacks(ModelService modelService, Map<String, Object> context, Throwable t, int mode) throws GenericServiceException;
}

