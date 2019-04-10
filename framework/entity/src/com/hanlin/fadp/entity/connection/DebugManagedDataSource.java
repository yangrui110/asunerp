

package com.hanlin.fadp.entity.connection;

import org.apache.commons.dbcp2.managed.ManagedDataSource;
import org.apache.commons.dbcp2.managed.TransactionRegistry;
import org.apache.commons.pool2.ObjectPool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import com.hanlin.fadp.base.util.Debug;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DebugManagedDataSource extends ManagedDataSource {

    public static final String module = DebugManagedDataSource.class.getName();

    public DebugManagedDataSource(ObjectPool pool, TransactionRegistry transactionRegistry) {
        super(pool, transactionRegistry);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (Debug.verboseOn()) {
            if (super.getPool() instanceof GenericObjectPool) {
                GenericObjectPool objectPool = (GenericObjectPool)super.getPool();
                Debug.logVerbose("Borrowing a connection from the pool; used/idle/total: " + objectPool.getNumActive() + "/" + objectPool.getNumIdle() + "/" + (objectPool.getNumActive() + objectPool.getNumIdle()) + "; min idle/max idle/max total: " + objectPool.getMinIdle() + "/" + objectPool.getMaxIdle() + "/" + objectPool.getMaxTotal(), module);
            } else {
                Debug.logVerbose("Borrowing a connection from the pool; used/idle/total: " + super.getPool().getNumActive() + "/" + super.getPool().getNumIdle() + "/" + (super.getPool().getNumActive() + super.getPool().getNumIdle()), module);
            }
        }
        return super.getConnection();
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> dataSourceInfo = new HashMap<String, Object>();
        dataSourceInfo.put("poolNumActive", super.getPool().getNumActive());
        dataSourceInfo.put("poolNumIdle", super.getPool().getNumIdle());
        dataSourceInfo.put("poolNumTotal", (super.getPool().getNumIdle() + super.getPool().getNumActive()));
        if (super.getPool() instanceof GenericObjectPool) {
            GenericObjectPool objectPool = (GenericObjectPool)super.getPool();
            dataSourceInfo.put("poolMaxActive", objectPool.getMaxTotal());
            dataSourceInfo.put("poolMaxIdle", objectPool.getMaxIdle());
            dataSourceInfo.put("poolMaxWait", objectPool.getMaxWaitMillis());
            dataSourceInfo.put("poolMinEvictableIdleTimeMillis", objectPool.getMinEvictableIdleTimeMillis());
            dataSourceInfo.put("poolMinIdle", objectPool.getMinIdle());
        }
        return dataSourceInfo;
    }

}
