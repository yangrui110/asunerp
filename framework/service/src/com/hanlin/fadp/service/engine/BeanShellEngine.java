
package com.hanlin.fadp.service.engine;

import static com.hanlin.fadp.base.util.UtilGenerics.cast;

import java.util.Map;

import com.hanlin.fadp.base.util.BshUtil;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.ServiceUtil;

/**
 * BeanShell Script Service Engine
 */
public final class BeanShellEngine extends GenericAsyncEngine {

    public BeanShellEngine(ServiceDispatcher dispatcher) {
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
        return serviceInvoker(localName, modelService, context);
    }

    // Invoke the BeanShell Script.
    private Map<String, Object> serviceInvoker(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        if (UtilValidate.isEmpty(modelService.location)) {
            throw new GenericServiceException("Cannot run Beanshell service with empty location");
        }

        String location = this.getLocation(modelService);
        context.put("dctx", dispatcher.getLocalContext(localName));

        try {
            Object resultObj = BshUtil.runBshAtLocation(location, context);

            if (resultObj != null && resultObj instanceof Map<?, ?>) {
                Debug.logInfo("Got result Map from script return: " + resultObj, module);
                return cast(resultObj);
            } else if (context.get("result") != null && context.get("result") instanceof Map<?, ?>) {
                Debug.logInfo("Got result Map from context: " + resultObj, module);
                return cast(context.get("result"));
            }
        } catch (GeneralException e) {
            throw new GenericServiceException(e);
        }

        return ServiceUtil.returnSuccess();
    }
}
