
package com.hanlin.fadp.entity.connection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.TransactionManager;

import org.apache.commons.dbcp2.DriverConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.managed.LocalXAConnectionFactory;
import org.apache.commons.dbcp2.managed.ManagedDataSource;
import org.apache.commons.dbcp2.managed.PoolableManagedConnectionFactory;
import org.apache.commons.dbcp2.managed.XAConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.GenericEntityConfException;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.config.model.InlineJdbc;
import com.hanlin.fadp.entity.config.model.JdbcElement;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.transaction.TransactionFactoryLoader;
import com.hanlin.fadp.entity.transaction.TransactionUtil;

/**
 * Apache Commons DBCP connection factory.
 * 
 * @see <a href="http://commons.apache.org/proper/commons-dbcp/">Apache Commons DBCP</a>
 */
public class DBCPConnectionFactory implements ConnectionFactory {

    public static final String module = DBCPConnectionFactory.class.getName();
    protected static final ConcurrentHashMap<String, ManagedDataSource> dsCache = new ConcurrentHashMap<String, ManagedDataSource>();

    public Connection getConnection(GenericHelperInfo helperInfo, JdbcElement abstractJdbc) throws SQLException, GenericEntityException {
        String cacheKey = helperInfo.getHelperFullName();
        ManagedDataSource mds = dsCache.get(cacheKey);
        if (mds != null) {
            return TransactionUtil.getCursorConnection(helperInfo, mds.getConnection());
        }
        if (!(abstractJdbc instanceof InlineJdbc)) {
            throw new GenericEntityConfException("DBCP requires an <inline-jdbc> child element in the <datasource> element");
        }
        InlineJdbc jdbcElement = (InlineJdbc) abstractJdbc;
        // connection properties
        TransactionManager txMgr = TransactionFactoryLoader.getInstance().getTransactionManager();
        String driverName = jdbcElement.getJdbcDriver();

        String jdbcUri = helperInfo.getOverrideJdbcUri(jdbcElement.getJdbcUri());
        String jdbcUsername = helperInfo.getOverrideUsername(jdbcElement.getJdbcUsername());
        String jdbcPassword = helperInfo.getOverridePassword(EntityConfig.getJdbcPassword(jdbcElement));

        // pool settings
        int maxSize = jdbcElement.getPoolMaxsize();
        int minSize = jdbcElement.getPoolMinsize();
        int maxIdle = jdbcElement.getIdleMaxsize();
        // maxIdle must be greater than pool-minsize
        maxIdle = maxIdle > minSize ? maxIdle : minSize;
        // load the driver
        Driver jdbcDriver;
        synchronized (DBCPConnectionFactory.class) {
            // Sync needed for MS SQL JDBC driver. See OFBIZ-5216.
            try {
                jdbcDriver = (Driver) Class.forName(driverName, true, Thread.currentThread().getContextClassLoader()).newInstance();
            } catch (Exception e) {
                Debug.logError(e, module);
                throw new GenericEntityException(e.getMessage(), e);
            }
        }

        // connection factory properties
        Properties cfProps = new Properties();
        cfProps.put("user", jdbcUsername);
        cfProps.put("password", jdbcPassword);

        // create the connection factory
        org.apache.commons.dbcp2.ConnectionFactory cf = new DriverConnectionFactory(jdbcDriver, jdbcUri, cfProps);

        // wrap it with a LocalXAConnectionFactory
        XAConnectionFactory xacf = new LocalXAConnectionFactory(txMgr, cf);

        // create the pool object factory
        PoolableConnectionFactory factory = new PoolableManagedConnectionFactory(xacf, null);
        factory.setValidationQuery(jdbcElement.getPoolJdbcTestStmt());
        factory.setDefaultReadOnly(false);
        String transIso = jdbcElement.getIsolationLevel();
        if (!transIso.isEmpty()) {
            if ("Serializable".equals(transIso)) {
                factory.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            } else if ("RepeatableRead".equals(transIso)) {
                factory.setDefaultTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } else if ("ReadUncommitted".equals(transIso)) {
                factory.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            } else if ("ReadCommitted".equals(transIso)) {
                factory.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } else if ("None".equals(transIso)) {
                factory.setDefaultTransactionIsolation(Connection.TRANSACTION_NONE);
            }
        }

        // configure the pool settings
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxSize);
        // settings for idle connections
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minSize);
        poolConfig.setTimeBetweenEvictionRunsMillis(jdbcElement.getTimeBetweenEvictionRunsMillis());
        poolConfig.setMinEvictableIdleTimeMillis(-1); // disabled in favour of setSoftMinEvictableIdleTimeMillis(...)
        poolConfig.setSoftMinEvictableIdleTimeMillis(jdbcElement.getSoftMinEvictableIdleTimeMillis());
        poolConfig.setNumTestsPerEvictionRun(maxSize); // test all the idle connections
        // settings for when the pool is exhausted
        poolConfig.setBlockWhenExhausted(true); // the thread requesting the connection waits if no connection is available
        poolConfig.setMaxWaitMillis(jdbcElement.getPoolSleeptime()); // throw an exception if, after getPoolSleeptime() ms, no connection is available for the requesting thread
        // settings for the execution of the validation query
        poolConfig.setTestOnCreate(jdbcElement.getTestOnCreate());
        poolConfig.setTestOnBorrow(jdbcElement.getTestOnBorrow());
        poolConfig.setTestOnReturn(jdbcElement.getTestOnReturn());
        poolConfig.setTestWhileIdle(jdbcElement.getTestWhileIdle());

        GenericObjectPool pool = new GenericObjectPool(factory, poolConfig);
        factory.setPool(pool);

        mds = new ManagedDataSource(pool, xacf.getTransactionRegistry());
        //mds = new DebugManagedDataSource(pool, xacf.getTransactionRegistry()); // Useful to debug the usage of connections in the pool
        mds.setAccessToUnderlyingConnectionAllowed(true);

        // cache the pool
        dsCache.putIfAbsent(cacheKey, mds);
        mds = dsCache.get(cacheKey);

        return TransactionUtil.getCursorConnection(helperInfo, mds.getConnection());
    }

    public void closeAll() {
        // no methods on the pool to shutdown; so just clearing for GC
        // Hmm... then how do we close the JDBC connections?
        dsCache.clear();
    }

    public static Map<String, Object> getDataSourceInfo(String helperName) {
        Map<String, Object> dataSourceInfo = new HashMap<String, Object>();
        ManagedDataSource mds = dsCache.get(helperName);
        if (mds instanceof DebugManagedDataSource) {
            dataSourceInfo = ((DebugManagedDataSource)mds).getInfo();
        }
        return dataSourceInfo;
    }

}
