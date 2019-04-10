
package com.hanlin.fadp.service.engine;

import java.net.URL;
import java.util.Map;

import com.hanlin.fadp.base.util.HttpClient;
import com.hanlin.fadp.base.util.HttpClientException;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

/**
 * BSF Service Engine
 */
public class BSFEngine extends GenericAsyncEngine {

    public static final String module = BSFEngine.class.getName();
    private static final UtilCache<String, String> scriptCache = UtilCache.createUtilCache("BSFScripts", 0, 0);

    public BSFEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        Object result = serviceInvoker(localName, modelService, context);

        if (result == null || !(result instanceof Map<?, ?>))
            throw new GenericServiceException("Service did not return expected result");
        return UtilGenerics.checkMap(result);
    }

    // Invoke the BSF Script.
    private Object serviceInvoker(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        if (modelService.location == null || modelService.invoke == null)
            throw new GenericServiceException("Cannot locate service to invoke");

        // get the DispatchContext from the localName and ServiceDispatcher
        DispatchContext dctx = dispatcher.getLocalContext(localName);

        // get the classloader to use
        ClassLoader cl = null;

        if (dctx == null) {
            cl = this.getClass().getClassLoader();
        } else {
            cl = dctx.getClassLoader();
        }

        String location = this.getLocation(modelService);

        // create the manager object and set the classloader
        BSFManager mgr = new BSFManager();
        mgr.setClassLoader(cl);

        mgr.registerBean("dctx", dctx);
        mgr.registerBean("context", context);

        // pre-load the engine to make sure we were called right
        org.apache.bsf.BSFEngine bsfEngine = null;
        try {
            bsfEngine = mgr.loadScriptingEngine(modelService.engineName);
        } catch (BSFException e) {
            throw new GenericServiceException("Problems loading org.apache.bsf.BSFEngine: " + modelService.engineName, e);
        }

        // source the script into a string
        String script = scriptCache.get(localName + "_" + location);

        if (script == null) {
            URL scriptUrl = UtilURL.fromResource(location, cl);

            if (scriptUrl != null) {
                try {
                    HttpClient http = new HttpClient(scriptUrl);
                    script = http.get();
                } catch (HttpClientException e) {
                    throw new GenericServiceException("Cannot read script from resource", e);
                }
            } else {
                throw new GenericServiceException("Cannot read script, resource [" + location + "] not found");
            }
            if (script == null || script.length() < 2) {
                throw new GenericServiceException("Null or empty script");
            }
            script = scriptCache.putIfAbsentAndGet(localName + "_" + location, script);
        }

        // now invoke the script
        try {
            bsfEngine.exec(location, 0, 0, script);
        } catch (BSFException e) {
            throw new GenericServiceException("Script invocation error", e);
        }

        return mgr.lookupBean("response");
    }
}
