
package com.hanlin.fadp.service.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.HttpClient;
import com.hanlin.fadp.base.util.HttpClientException;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.serialize.XmlSerializer;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;

/**
 * HttpEngine.java
 */
public class HttpEngine extends GenericAsyncEngine {

    public static final String module = HttpEngine.class.getName();
    private static final boolean exportAll = false;

    public HttpEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSync(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        String xmlContext = null;

        try {
            if (Debug.verboseOn()) Debug.logVerbose("Serializing Context --> " + context, module);
            xmlContext = XmlSerializer.serialize(context);
        } catch (Exception e) {
            throw new GenericServiceException("Cannot serialize context.", e);
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("serviceName", modelService.invoke);
        if (xmlContext != null)
            parameters.put("serviceContext", xmlContext);

        HttpClient http = new HttpClient(this.getLocation(modelService), parameters);
        String postResult = null;
        try {
            postResult = http.post();
        } catch (HttpClientException e) {
            throw new GenericServiceException("Problems invoking HTTP request", e);
        }

        Map<String, Object> result = null;
        try {
            Object res = XmlSerializer.deserialize(postResult, dctx.getDelegator());
            if (res instanceof Map<?, ?>)
                result = UtilGenerics.checkMap(res);
            else
                throw new GenericServiceException("Result not an instance of Map.");
        } catch (Exception e) {
            throw new GenericServiceException("Problems deserializing result.", e);
        }

        return result;
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#runSyncIgnore(java.lang.String, com.hanlin.fadp.service.ModelService, java.util.Map)
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }

    /**
     * Event for handling HTTP services
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @return null
     */
    public static String httpEngine(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String serviceName = request.getParameter("serviceName");
        String serviceMode = request.getParameter("serviceMode");
        String xmlContext = request.getParameter("serviceContext");

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> context = null;

        if (serviceName == null)
            result.put(ModelService.ERROR_MESSAGE, "Cannot have null service name");

        if (serviceMode == null)
            serviceMode = "SYNC";

        // deserialize the context
        if (!result.containsKey(ModelService.ERROR_MESSAGE)) {
            if (xmlContext != null) {
                try {
                    Object o = XmlSerializer.deserialize(xmlContext, delegator);
                    if (o instanceof Map<?, ?>)
                        context = UtilGenerics.checkMap(o);
                    else {
                        Debug.logError("Context not an instance of Map error", module);
                        result.put(ModelService.ERROR_MESSAGE, "Context not an instance of Map");
                    }
                } catch (Exception e) {
                    Debug.logError(e, "Deserialization error", module);
                    result.put(ModelService.ERROR_MESSAGE, "Error occurred deserializing context: " + e.toString());
                }
            }
        }

        // invoke the service
        if (!result.containsKey(ModelService.ERROR_MESSAGE)) {
            try {
                ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);
                if (model.export || exportAll) {
                    if (serviceMode.equals("ASYNC")) {
                        dispatcher.runAsync(serviceName, context);
                    } else {
                        result = dispatcher.runSync(serviceName, context);
                    }
                } else {
                    Debug.logWarning("Attempt to invoke a non-exported service: " + serviceName, module);
                    throw new GenericServiceException("Cannot find requested service");
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Service invocation error", module);
                result.put(ModelService.ERROR_MESSAGE, "Service invocation error: " + e.toString());
            }
        }

        // backup error message
        StringBuilder errorMessage = new StringBuilder();

        // process the result
        String resultString = null;
        try {
            resultString = XmlSerializer.serialize(result);
        } catch (Exception e) {
            Debug.logError(e, "Cannot serialize result", module);
            if (result.containsKey(ModelService.ERROR_MESSAGE))
                errorMessage.append(result.get(ModelService.ERROR_MESSAGE));
            errorMessage.append("::");
            errorMessage.append(e);
        }

        // handle the response
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("plain/text");

            if (errorMessage.length() > 0) {
                response.setContentLength(errorMessage.length());
                out.write(errorMessage.toString());
            } else {
                response.setContentLength(resultString.length());
                out.write(resultString);
            }

            out.flush();
            response.flushBuffer();
        } catch (IOException e) {
            Debug.logError(e, "Problems w/ getting the servlet writer.", module);
            return "error";
        }

        return null;
    }


}
