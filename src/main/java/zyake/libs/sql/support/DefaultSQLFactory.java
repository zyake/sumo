package zyake.libs.sql.support;

import zyake.libs.sql.Query;
import zyake.libs.sql.SQL;
import zyake.libs.sql.SQLFactory;
import zyake.libs.sql.SQLRuntimeException;

import java.sql.Connection;

public class DefaultSQLFactory implements SQLFactory {

    @Override
    public <Q extends Query> SQL newSQL(Connection connection, Class<Q> baseQuery) throws SQLRuntimeException {
        return new DefaultSQL(baseQuery, connection);
    }
}
