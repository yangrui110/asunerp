
package com.hanlin.fadp.service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hanlin.fadp.base.container.Container;
import com.hanlin.fadp.base.container.ContainerConfig;
import com.hanlin.fadp.base.container.ContainerException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.service.job.JobManager;

/**
 * A container for the service engine. 
 */
public class ServiceContainer implements Container {
    private static final String module = ServiceContainer.class.getName();
    private static final ConcurrentHashMap<String, LocalDispatcher> dispatcherCache = new ConcurrentHashMap<String, LocalDispatcher>();
    private static LocalDispatcherFactory dispatcherFactory;

    private String name;

    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;
        // initialize the LocalDispatcherFactory
        ContainerConfig.Container cfg = ContainerConfig.getContainer(name, configFile);
        ContainerConfig.Container.Property dispatcherFactoryProperty = cfg.getProperty("dispatcher-factory");
        if (dispatcherFactoryProperty == null || UtilValidate.isEmpty(dispatcherFactoryProperty.value)) {
            throw new ContainerException("Unable to initialize container " + name + ": dispatcher-factory property is not set");
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> c = loader.loadClass(dispatcherFactoryProperty.value);
            dispatcherFactory = (LocalDispatcherFactory) c.newInstance();
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    @Override
    public boolean start() throws ContainerException {
        return true;
    }

    @Override
    public void stop() throws ContainerException {
        JobManager.shutDown();
        Set<String> dispatcherNames = getAllDispatcherNames();
        for (String dispatcherName: dispatcherNames) {
            deregister(dispatcherName);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public static LocalDispatcher getLocalDispatcher(String dispatcherName, Delegator delegator) {
        if (dispatcherName == null) {
            dispatcherName = delegator.getDelegatorName();
            Debug.logWarning("ServiceContainer.getLocalDispatcher method called with a null dispatcherName, defaulting to delegator name.", module);
        }
        if (UtilValidate.isNotEmpty(delegator.getDelegatorTenantId())) {
            dispatcherName = dispatcherName.concat("#").concat(delegator.getDelegatorTenantId());
        }
        LocalDispatcher dispatcher = dispatcherCache.get(dispatcherName);
        if (dispatcher == null) {
            dispatcher = dispatcherFactory.createLocalDispatcher(dispatcherName, delegator);
            dispatcherCache.putIfAbsent(dispatcherName, dispatcher);
            dispatcher = dispatcherCache.get(dispatcherName);
            if (Debug.infoOn()) Debug.logInfo("Created new dispatcher: " + dispatcherName, module);
        }
        return dispatcher;
    }

    public static void deregister(String dispatcherName) {
        LocalDispatcher dispatcher = dispatcherCache.get(dispatcherName);
        if (dispatcher != null) {
            dispatcher.deregister();
        }
    }

    public static LocalDispatcher removeFromCache(String dispatcherName) {
        if (Debug.infoOn()) Debug.logInfo("Removing from cache dispatcher: " + dispatcherName, module);
        return dispatcherCache.remove(dispatcherName);
    }

    public static Set<String> getAllDispatcherNames() {
        return Collections.unmodifiableSet(dispatcherCache.keySet());
    }
}
