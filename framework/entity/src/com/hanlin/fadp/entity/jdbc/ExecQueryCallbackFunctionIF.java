
package com.hanlin.fadp.entity.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ExecQueryCallbackFunctionIF {
    public boolean processNextRow(ResultSet rs) throws SQLException;
}
