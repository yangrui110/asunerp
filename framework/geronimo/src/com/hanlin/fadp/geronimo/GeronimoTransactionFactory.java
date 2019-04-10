
package com.hanlin.fadp.geronimo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.log.UnrecoverableLog;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.TransactionLog;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.jdbc.ConnectionFactoryLoader;
import com.hanlin.fadp.entity.transaction.TransactionFactory;

/**
 * GeronimoTransactionFactory
 */
public class GeronimoTransactionFactory implements TransactionFactory {

    public static final String module = GeronimoTransactionFactory.class.getName();

    private static int defaultTransactionTimeoutSeconds = 60;
    private static TransactionLog transactionLog;
    private static GeronimoTransactionManager geronimoTransactionManager;

    static {
        // creates an instance of Geronimo transaction context, etc with a local transaction factory which is not bound to a registry
        try {
            transactionLog = new UnrecoverableLog();
            geronimoTransactionManager = new GeronimoTransactionManager(defaultTransactionTimeoutSeconds, new XidFactoryImpl(), transactionLog);
        } catch (XAException e) {
            Debug.logError(e, "Error initializing Geronimo transaction manager: " + e.toString(), module);
        }
    }

    /*
     * @see com.hanlin.fadp.entity.transaction.TransactionFactory#getTransactionManager()
     */
    public TransactionManager getTransactionManager() {
        return geronimoTransactionManager;
    }

    /*
     * @see com.hanlin.fadp.entity.transaction.TransactionFactory#getUserTransaction()
     */
    public UserTransaction getUserTransaction() {
        return geronimoTransactionManager;
    }

    public String getTxMgrName() {
        return "geronimo";
    }

    public Connection getConnection(GenericHelperInfo helperInfo) throws SQLException, GenericEntityException {
        Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());

        if (datasourceInfo != null && datasourceInfo.getInlineJdbc() != null) {
            return ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
        } else {
            Debug.logError("Geronimo is the configured transaction manager but no inline-jdbc element was specified in the " + helperInfo.getHelperBaseName() + " datasource. Please check your configuration", module);
            return null;
        }
    }

    public void shutdown() {
        ConnectionFactoryLoader.getInstance().closeAll();
        /*
        if (transactionContextManager != null) {
            // TODO: need to do anything for this?
            transactionContextManager = null;
        }
        */
    }
}
