
package com.hanlin.fadp.service.engine;

import static com.hanlin.fadp.base.util.UtilGenerics.cast;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ScriptUtil;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.ServiceUtil;

/**
 * Generic Script Service Engine. This service engine uses the javax.script package (JSR-223) to invoke scripts or script functions.
 * <p>The script service engine will put the following artifacts in the script engine's bindings:<br />
 * <ul>
 *   <li><code>parameters</code> - the service attributes <code>Map</code></li>
 *   <li><code>dctx</code> - a <code>DispatchContext</code> instance</li>
 *   <li><code>dispatcher</code> - a <code>LocalDispatcher</code> instance</li>
 *   <li><code>delegator</code> - a <code>Delegator</code> instance</li>
 * </ul></p>
 * <p>If the service definition includes an invoke attribute, then the matching script function/method will be called
 * with a single argument - the bindings <code>Map</code>.</p>
 */
public final class ScriptEngine extends GenericAsyncEngine {

    public static final String module = ScriptEngine.class.getName();
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

    public ScriptEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        Assert.notNull("localName", localName, "modelService.location", modelService.location, "context", context);
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(context);
        context.put(ScriptUtil.PARAMETERS_KEY, params);
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        context.put("dctx", dctx);
        context.put("dispatcher", dctx.getDispatcher());
        context.put("delegator", dispatcher.getDelegator());
        try {
            ScriptContext scriptContext = ScriptUtil.createScriptContext(context, protectedKeys);
            Object resultObj = ScriptUtil.executeScript(getLocation(modelService), modelService.invoke, scriptContext, null);
            if (resultObj == null) {
                resultObj = scriptContext.getAttribute(ScriptUtil.RESULT_KEY);
            }
            if (resultObj != null && resultObj instanceof Map<?, ?>) {
                return cast(resultObj);
            }
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.putAll(modelService.makeValid(scriptContext.getBindings(ScriptContext.ENGINE_SCOPE), "OUT"));
            return result;
        } catch (ScriptException se) {
            return ServiceUtil.returnError(se.getMessage());
        } catch (Exception e) {
            Debug.logWarning(e, "Error invoking service " + modelService.name + ": ", module);
            throw new GenericServiceException(e);
        }
    }

    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }
}
