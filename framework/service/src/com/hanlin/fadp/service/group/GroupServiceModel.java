
package com.hanlin.fadp.service.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import org.w3c.dom.Element;

/**
 * GroupServiceModel.java
 */
public class GroupServiceModel {

    public static final String module = GroupServiceModel.class.getName();

    private String serviceName, serviceMode;
    private boolean resultToContext = false;
    private boolean optionalParams = false;

    /**
     * Constructor using DOM element
     * @param service DOM element for the service
     */
    public GroupServiceModel(Element service) {
        this.serviceName = service.getAttribute("name");
        this.serviceMode = service.getAttribute("mode");
        this.resultToContext = service.getAttribute("result-to-context").equalsIgnoreCase("true");
        this.optionalParams = service.getAttribute("parameters").equalsIgnoreCase("optional");
    }

    /**
     * Basic constructor
     * @param serviceName name of the service
     * @param serviceMode service invocation mode (sync|async)
     */
    public GroupServiceModel(String serviceName, String serviceMode) {
        this.serviceName = serviceName;
        this.serviceMode = serviceMode;
    }

    /**
     * Getter for the service mode
     * @return String
     */
    public String getMode() {
        return this.serviceMode;
    }

    /**
     * Getter for the service name
     * @return String
     */
    public String getName() {
        return this.serviceName;
    }

    /**
     * Returns true if the results of this service are to go back into the context
     * @return boolean
     */
    public boolean resultToContext() {
        return this.resultToContext;
    }

    /**
     * Returns true of the parameters for this service are to be included as optional
     * @return boolean
     */
    public boolean isOptional() {
        return this.optionalParams;
    }

    /**
     * Invoker method to invoke this service
     * @param dispatcher ServiceDispatcher used for this invocation
     * @param localName Name of the LocalDispatcher used
     * @param context Context for this service (will use only valid parameters)
     * @return Map result Map
     * @throws GenericServiceException
     */
    public Map<String, Object> invoke(ServiceDispatcher dispatcher, String localName, Map<String, Object> context) throws GenericServiceException {
        DispatchContext dctx = dispatcher.getLocalContext(localName);
        ModelService model = dctx.getModelService(getName());
        if (model == null)
            throw new GenericServiceException("Group defined service (" + getName() + ") is not a defined service.");

        Map<String, Object> thisContext = model.makeValid(context, ModelService.IN_PARAM);
        Debug.logInfo("Running grouped service [" + serviceName + "]", module);
        if (getMode().equals("async")) {
            List<String> requiredOut = model.getParameterNames(ModelService.OUT_PARAM, false);
            if (requiredOut.size() > 0) {
                Debug.logWarning("Grouped service (" + getName() + ") requested 'async' invocation; running sync because of required OUT parameters.", module);
                return dispatcher.runSync(localName, model, thisContext);
            } else {
                dispatcher.runAsync(localName, model, thisContext, false);
                return new HashMap<String, Object>();
            }
        } else {
            return dispatcher.runSync(localName, model, thisContext);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getName());
        str.append("::");
        str.append(getMode());
        str.append("::");
        return str.toString();
    }
}
