
package com.hanlin.fadp.service.engine;

import static com.hanlin.fadp.base.util.UtilGenerics.cast;
import groovy.lang.Script;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptContext;

import org.codehaus.groovy.runtime.InvokerHelper;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.GroovyUtil;
import com.hanlin.fadp.base.util.ScriptHelper;
import com.hanlin.fadp.base.util.ScriptUtil;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.ServiceUtil;

/**
 * Groovy Script Service Engine
 */
public final class GroovyEngine extends GenericAsyncEngine {

    public static final String module = GroovyEngine.class.getName();
    protected static final Object[] EMPTY_ARGS = {};
    private static final Set<String> protectedKeys = createProtectedKeys();

    private static Set<String> createProtectedKeys() {
        Set<String> newSet = new HashSet<String>();
        /* Commenting out for now because some scripts write to the parameters Map - which should not be allowed.
        newSet.add(ScriptUtil.PARAMETERS_KEY);
        */
        newSet.add("dctx");
        newSet.add("dispatcher");
        newSet.add("delegator");
        return Collections.unmodifiableSet(newSet);
    }

    public GroovyEngine(ServiceDispatcher dispatcher) {
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

    private Map<String, Object> serviceInvoker(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        if (UtilValidate.isEmpty(modelService.location)) {
            throw new GenericServiceException("Cannot run Groovy service with empty location");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(context);

        Map<String, Object> gContext = new HashMap<String, Object>();
        gContext.putAll(context);
        gContext.put(ScriptUtil.PARAMETERS_KEY, params);

        DispatchContext dctx = dispatcher.getLocalContext(localName);
        gContext.put("dctx", dctx);
        gContext.put("dispatcher", dctx.getDispatcher());
        gContext.put("delegator", dispatcher.getDelegator());
        try {
            ScriptContext scriptContext = ScriptUtil.createScriptContext(gContext, protectedKeys);
            ScriptHelper scriptHelper = (ScriptHelper)scriptContext.getAttribute(ScriptUtil.SCRIPT_HELPER_KEY);
            if (scriptHelper != null) {
                gContext.put(ScriptUtil.SCRIPT_HELPER_KEY, scriptHelper);
            }
            Script script = InvokerHelper.createScript(GroovyUtil.getScriptClassFromLocation(this.getLocation(modelService)), GroovyUtil.getBinding(gContext));
            Object resultObj = null;
            if (UtilValidate.isEmpty(modelService.invoke)) {
                resultObj = script.run();
            } else {
                resultObj = script.invokeMethod(modelService.invoke, EMPTY_ARGS);
            }
            if (resultObj == null) {
                resultObj = scriptContext.getAttribute(ScriptUtil.RESULT_KEY);
            }
            if (resultObj != null && resultObj instanceof Map<?, ?>) {
                return cast(resultObj);
            }
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.putAll(modelService.makeValid(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE), "OUT"));
            return result;
        } catch (GeneralException ge) {
            throw new GenericServiceException(ge);
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }
    }
}
