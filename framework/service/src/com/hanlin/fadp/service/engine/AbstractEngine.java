
package com.hanlin.fadp.service.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.service.GenericServiceCallback;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.config.ServiceConfigUtil;
import com.hanlin.fadp.service.config.model.ServiceLocation;

/**
 * Abstract Service Engine
 */
public abstract class AbstractEngine implements GenericEngine {

    public static final String module = AbstractEngine.class.getName();
    protected static final Map<String, String> locationMap = createLocationMap();

    protected ServiceDispatcher dispatcher = null;

    protected AbstractEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    // creates the location alias map
    protected static Map<String, String> createLocationMap() {
        Map<String, String> tmpMap = new HashMap<String, String>();

        List<ServiceLocation> locationsList = null;
        try {
            locationsList = ServiceConfigUtil.getServiceEngine().getServiceLocations();
        } catch (GenericConfigException e) {
            // FIXME: Refactor API so exceptions can be thrown and caught.
            Debug.logError(e, module);
            throw new RuntimeException(e.getMessage());
        }
        for (ServiceLocation e: locationsList) {
            tmpMap.put(e.getName(), e.getLocation());
        }

        Debug.logInfo("Loaded Service Locations: " + tmpMap, module);
        return Collections.unmodifiableMap(tmpMap);
    }

    // uses the lookup map to determine if the location has been aliased by a service-location element in serviceengine.xml
    protected String getLocation(ModelService model) {
        if (locationMap.containsKey(model.location)) {
            return locationMap.get(model.location);
        } else {
            return model.location;
        }
    }

    /**
     * @see com.hanlin.fadp.service.engine.GenericEngine#sendCallbacks(com.hanlin.fadp.service.ModelService, java.util.Map, int)
     */
    public void sendCallbacks(ModelService model, Map<String, Object> context, int mode) throws GenericServiceException {
        if (!allowCallbacks(model, context, mode)) return;
        List<GenericServiceCallback> callbacks = dispatcher.getCallbacks(model.name);
        if (callbacks != null) {
            Iterator<GenericServiceCallback> i = callbacks.iterator();
            while (i.hasNext()) {
                GenericServiceCallback gsc = i.next();
                if (gsc.isEnabled()) {
                    gsc.receiveEvent(context);
                } else {
                    i.remove();
                }
            }
        }
    }

    public void sendCallbacks(ModelService model, Map<String, Object> context, Throwable t, int mode) throws GenericServiceException {
        if (!allowCallbacks(model, context, mode)) return;
        List<GenericServiceCallback> callbacks = dispatcher.getCallbacks(model.name);
        if (callbacks != null) {
            Iterator<GenericServiceCallback> i = callbacks.iterator();
            while (i.hasNext()) {
                GenericServiceCallback gsc = i.next();
                if (gsc.isEnabled()) {
                    gsc.receiveEvent(context,t);
                } else {
                    i.remove();
                }
            }
        }
    }

    public void sendCallbacks(ModelService model, Map<String, Object> context, Map<String, Object> result, int mode) throws GenericServiceException {
        if (!allowCallbacks(model, context, mode)) return;
        List<GenericServiceCallback> callbacks = dispatcher.getCallbacks(model.name);
        if (callbacks != null) {
            Iterator<GenericServiceCallback> i = callbacks.iterator();
            while (i.hasNext()) {
                GenericServiceCallback gsc = i.next();
                if (gsc.isEnabled()) {
                    gsc.receiveEvent(context, result);
                } else {
                    i.remove();
                }
            }
        }
    }

    protected boolean allowCallbacks(ModelService model, Map<String, Object> context, int mode) throws GenericServiceException {
        return true;
    }
}
