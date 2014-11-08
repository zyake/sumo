package zyake.libs.sumo;

import java.sql.Connection;

public interface SQLFactory {

   <Q extends Query> SQL newSQL(Connection connection, Class<Q> baseQuery) throws SQLRuntimeException;
}
