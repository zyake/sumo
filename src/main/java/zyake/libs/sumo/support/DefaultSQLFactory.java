package zyake.libs.sumo.support;

import zyake.libs.sumo.Query;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLFactory;
import zyake.libs.sumo.SQLRuntimeException;

import java.sql.Connection;

public class DefaultSQLFactory implements SQLFactory {

    @Override
    public <Q extends Query> SQL newSQL(Connection connection, Class<Q> baseQuery) throws SQLRuntimeException {
        return new DefaultSQL(baseQuery, connection);
    }
}
