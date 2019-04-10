
package com.hanlin.fadp.service.config;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.service.config.model.Engine;
import com.hanlin.fadp.service.config.model.ServiceConfig;
import com.hanlin.fadp.service.config.model.ServiceEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A <code>ServiceConfig</code> factory and related utility methods.
 * <p>The <code>ServiceConfig</code> instance models the <code>serviceengine.xml</code> file
 * and the instance is kept in the "service.ServiceConfig" cache. Clearing the cache will reload
 * the service configuration file. Client code that depends on the <code>serviceengine.xml</code>
 * file can be notified when the file is reloaded by implementing <code>ServiceConfigListener</code>
 * and registering itself using the {@link #registerServiceConfigListener(ServiceConfigListener)}
 * method.<p>
 */
public final class ServiceConfigUtil {

    public static final String module = ServiceConfigUtil.class.getName();
    public static final String engine = "default";
    public static final String SERVICE_ENGINE_XML_FILENAME = "serviceengine.xml";
    // Keep the ServiceConfig instance in a cache - so the configuration can be reloaded at run-time. There will be only one ServiceConfig instance in the cache.
    private static final UtilCache<String, ServiceConfig> serviceConfigCache = UtilCache.createUtilCache("service.ServiceConfig", 0, 0, false);
    private static final List<ServiceConfigListener> configListeners = new CopyOnWriteArrayList<ServiceConfigListener>();

    /**
     * Returns the specified parameter value from the specified engine, or <code>null</code>
     * if the engine or parameter are not found.
     *  
     * @param engineName
     * @param parameterName
     * @return
     * @throws GenericConfigException
     */
    public static String getEngineParameter(String engineName, String parameterName) throws GenericConfigException {
        Engine engine = getServiceEngine().getEngine(engineName);
        if (engine != null) {
            return engine.getParameterValue(parameterName);
        }
        return null;
    }

    /**
     * Returns the <code>ServiceConfig</code> instance.
     * @throws GenericConfigException
     */
    public static ServiceConfig getServiceConfig() throws GenericConfigException {
        ServiceConfig instance = serviceConfigCache.get("instance");
        if (instance == null) {
            Element serviceConfigElement = getXmlDocument().getDocumentElement();
            instance = ServiceConfig.create(serviceConfigElement);
            serviceConfigCache.putIfAbsent("instance", instance);
            instance = serviceConfigCache.get("instance");
            for (ServiceConfigListener listener : configListeners) {
                try {
                    listener.onServiceConfigChange(instance);
                } catch (Exception e) {
                    Debug.logError(e, "Exception thrown while notifying listener " + listener + ": ", module);
                }
            }
        }
        return instance;
    }

    /**
     * Returns the default service engine configuration (named "default").
     * @throws GenericConfigException 
     */
    public static ServiceEngine getServiceEngine() throws GenericConfigException {
        return getServiceConfig().getServiceEngine(engine);
    }

    /**
     * Returns the specified <code>ServiceEngine</code> configuration instance,
     * or <code>null</code> if the configuration does not exist.
     * 
     * @throws GenericConfigException
     */
    public static ServiceEngine getServiceEngine(String name) throws GenericConfigException {
        return getServiceConfig().getServiceEngine(name);
    }

    private static Document getXmlDocument() throws GenericConfigException {
        URL confUrl = UtilURL.fromResource(SERVICE_ENGINE_XML_FILENAME);
        if (confUrl == null) {
            throw new GenericConfigException("Could not find the " + SERVICE_ENGINE_XML_FILENAME + " file");
        }
        try {
            return UtilXml.readXmlDocument(confUrl, true, true);
        } catch (Exception e) {
            throw new GenericConfigException("Exception thrown while reading " + SERVICE_ENGINE_XML_FILENAME + ": ", e);
        }
    }

    /**
     * Register a <code>ServiceConfigListener</code> instance. The instance will be notified
     * when the <code>serviceengine.xml</code> file is reloaded.
     * 
     * @param listener
     */
    public static void registerServiceConfigListener(ServiceConfigListener listener) {
        Assert.notNull("listener", listener);
        configListeners.add(listener);
    }
}
