
package com.hanlin.fadp.entity.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.config.model.JdbcElement;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;

/**
 * ConnectionFactory
 */
public interface ConnectionFactory {

    public Connection getConnection(GenericHelperInfo helperInfo, JdbcElement jdbcElement) throws SQLException, GenericEntityException;
    public void closeAll();
}
