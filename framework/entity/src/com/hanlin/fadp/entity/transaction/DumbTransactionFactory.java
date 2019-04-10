

package com.hanlin.fadp.entity.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.jdbc.ConnectionFactoryLoader;

/**
 * A dumb, non-working transaction manager.
 */
public class DumbTransactionFactory implements TransactionFactory {

    public static final String module = DumbTransactionFactory.class.getName();

    public TransactionManager getTransactionManager() {
        return new TransactionManager() {
            public void begin() throws NotSupportedException, SystemException {
            }

            public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
            }

            public int getStatus() throws SystemException {
                return TransactionUtil.STATUS_NO_TRANSACTION;
            }

            public Transaction getTransaction() throws SystemException {
                return null;
            }

            public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
            }

            public void rollback() throws IllegalStateException, SecurityException, SystemException {
            }

            public void setRollbackOnly() throws IllegalStateException, SystemException {
            }

            public void setTransactionTimeout(int i) throws SystemException {
            }

            public Transaction suspend() throws SystemException {
                return null;
            }
        };
    }

    public UserTransaction getUserTransaction() {
        return new UserTransaction() {
            public void begin() throws NotSupportedException, SystemException {
            }

            public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
            }

            public int getStatus() throws SystemException {
                return TransactionUtil.STATUS_NO_TRANSACTION;
            }

            public void rollback() throws IllegalStateException, SecurityException, SystemException {
            }

            public void setRollbackOnly() throws IllegalStateException, SystemException {
            }

            public void setTransactionTimeout(int i) throws SystemException {
            }
        };
    }

    public String getTxMgrName() {
        return "dumb";
    }

    public Connection getConnection(GenericHelperInfo helperInfo) throws SQLException, GenericEntityException {
        Datasource datasourceInfo = EntityConfig.getDatasource(helperInfo.getHelperBaseName());

        if (datasourceInfo.getInlineJdbc() != null) {
            Connection otherCon = ConnectionFactoryLoader.getInstance().getConnection(helperInfo, datasourceInfo.getInlineJdbc());
            return TransactionUtil.getCursorConnection(helperInfo, otherCon);
        } else {
            Debug.logError("Dumb/Empty is the configured transaction manager but no inline-jdbc element was specified in the " + helperInfo.getHelperBaseName() + " datasource. Please check your configuration", module);
            return null;
        }
    }

    public void shutdown() {}
}
