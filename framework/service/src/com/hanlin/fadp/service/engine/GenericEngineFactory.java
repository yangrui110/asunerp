
package com.hanlin.fadp.service.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.config.ServiceConfigUtil;

/**
 * Generic Engine Factory
 */
public class GenericEngineFactory {

    protected ServiceDispatcher dispatcher = null;
    protected Map<String, GenericEngine> engines = null;

    public GenericEngineFactory(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        engines = new HashMap<String, GenericEngine>();
    }

    /**
     * Gets the GenericEngine instance that corresponds to given the name
     *@param engineName Name of the engine
     *@return GenericEngine that corresponds to the engineName
     */
    public GenericEngine getGenericEngine(String engineName) throws GenericServiceException {
        String className = null;
        try {
            className = ServiceConfigUtil.getServiceEngine().getEngine(engineName).getClassName();
        } catch (GenericConfigException e) {
            throw new GenericServiceException(e);
        }

        GenericEngine engine = engines.get(engineName);
        if (engine == null) {
            synchronized (GenericEngineFactory.class) {
                engine = engines.get(engineName);
                if (engine == null) {
                    try {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        Class<?> c = loader.loadClass(className);
                        Constructor<GenericEngine> cn = UtilGenerics.cast(c.getConstructor(ServiceDispatcher.class));
                        engine = cn.newInstance(dispatcher);
                    } catch (Exception e) {
                        throw new GenericServiceException(e.getMessage(), e);
                    }
                    if (engine != null) {
                        engines.put(engineName, engine);
                    }
                }
            }
        }

        return engine;
    }
}

