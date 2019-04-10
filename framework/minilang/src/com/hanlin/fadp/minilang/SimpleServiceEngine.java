
package com.hanlin.fadp.minilang;

import java.util.Map;

import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.engine.GenericAsyncEngine;

/**
 * Mini-language Service Engine.
 */
public final class SimpleServiceEngine extends GenericAsyncEngine {

    /** Creates new Engine */
    public SimpleServiceEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Run the service synchronously and return the result
     * 
     * @param context
     *            Map of name, value pairs composing the context
     * @return Map of name, value pairs composing the result
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        Map<String, Object> result = serviceInvoker(localName, modelService, context);
        if (result == null)
            throw new GenericServiceException("Service did not return expected result");
        return result;
    }

    /**
     * Run the service synchronously and IGNORE the result
     * 
     * @param context
     *            Map of name, value pairs composing the context
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }

    // Invoke the simple method from a service context
    private Map<String, Object> serviceInvoker(String localName, ModelService modelService, Map<String, ? extends Object> context) throws GenericServiceException {
        // static java service methods should be: public Map methodName(DispatchContext dctx, Map context)
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        // check the package and method names
        if (modelService.location == null || modelService.invoke == null)
            throw new GenericServiceException("Cannot locate service to invoke (location or invoke name missing)");
        // get the classloader to use
        ClassLoader classLoader = null;
        if (dctx != null)
            classLoader = dctx.getClassLoader();
        // if the classLoader is null, no big deal, SimpleMethod will use the
        // current thread's ClassLoader by default if null passed in
        try {
            return SimpleMethod.runSimpleService(this.getLocation(modelService), modelService.invoke, dctx, context, classLoader);
        } catch (MiniLangException e) {
            throw new GenericServiceException("Error running simple method [" + modelService.invoke + "] in XML file [" + modelService.location + "]: ", e);
        }
    }
}
