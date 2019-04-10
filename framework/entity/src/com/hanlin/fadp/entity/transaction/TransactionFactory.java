
package com.hanlin.fadp.entity.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;

/**
 * TransactionFactory - central source for JTA objects
 */
public interface TransactionFactory {

    public TransactionManager getTransactionManager();

    public UserTransaction getUserTransaction();

    public String getTxMgrName();

    public Connection getConnection(GenericHelperInfo helperInfo) throws SQLException, GenericEntityException;

    public void shutdown();
}
