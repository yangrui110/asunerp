
package com.hanlin.fadp.entity.jdbc;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.connection.ConnectionFactory;

/**
 * ConnectionFactoryLoader - utility class that loads the connection manager and provides to client code a reference to it (ConnectionFactory)
 *
 */
public class ConnectionFactoryLoader {
    // Debug module name
    public static final String module = ConnectionFactoryLoader.class.getName();
    private static final ConnectionFactory connFactory = createConnectionFactory();

    private static ConnectionFactory createConnectionFactory() {
        ConnectionFactory instance = null;
        try {
            if (EntityConfig.getInstance().getConnectionFactory() == null) {
                return null;
            }
            String className = EntityConfig.getInstance().getConnectionFactory().getClassName();
            if (className == null) {
                throw new IllegalStateException("Could not find connection factory class name definition");
            }
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> tfClass = loader.loadClass(className);
            instance = (ConnectionFactory) tfClass.newInstance();
        } catch (ClassNotFoundException cnfe) {
            Debug.logError(cnfe, "Could not find connection factory class", module);
        } catch (Exception e) {
            Debug.logError(e, "Unable to instantiate the connection factory", module);
        }
        return instance;
    }

    public static ConnectionFactory getInstance() {
        if (connFactory == null) {
            throw new IllegalStateException("The Connection Factory is not initialized.");
        }
        return connFactory;
    }
}
