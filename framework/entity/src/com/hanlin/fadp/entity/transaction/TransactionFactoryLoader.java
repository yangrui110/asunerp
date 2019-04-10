
package com.hanlin.fadp.entity.transaction;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericEntityConfException;
import com.hanlin.fadp.entity.config.model.EntityConfig;

/**
 * TransactionFactoryLoader - utility class that loads the transaction manager and provides to client code a reference to it (TransactionFactory)
 */
public class TransactionFactoryLoader {

    public static final String module = TransactionFactoryLoader.class.getName();
    private static final TransactionFactory txFactory = createTransactionFactory();

    private static TransactionFactory createTransactionFactory() {
        TransactionFactory instance = null;
        try {
            String className = EntityConfig.getInstance().getTransactionFactory().getClassName();
            if (className == null) {
                throw new IllegalStateException("Could not find transaction factory class name definition");
            }
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> tfClass = loader.loadClass(className);
            instance = (TransactionFactory) tfClass.newInstance();
        } catch (GenericEntityConfException gece) {
            Debug.logError(gece, "Could not find transaction factory class name definition", module);
        } catch (ClassNotFoundException cnfe) {
            Debug.logError(cnfe, "Could not find transaction factory class", module);
        } catch (Exception e) {
            Debug.logError(e, "Unable to instantiate the transaction factory", module);
        }
        return instance;
    }

    public static TransactionFactory getInstance() {
        if (txFactory == null) {
            throw new IllegalStateException("The Transaction Factory is not initialized.");
        }
        return txFactory;
    }
}
