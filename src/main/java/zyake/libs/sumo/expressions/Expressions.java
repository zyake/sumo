package zyake.libs.sumo.expressions;

import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLRuntimeException;
import zyake.libs.sumo.SUMO;
import zyake.libs.sumo.unsafe.SUMOUnsafe;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that generates SQL expressions.
 */
public class Expressions {

    private final static AtomicReference<ExpressionParser> parserRef = new AtomicReference<>(new NamedExpressionParser());

    private Expressions() {
    }

    public static void setParserRef(ExpressionParser parser) {
        parserRef.set(parser);
    }

    public static QueryExpression query(String exp, SQL.RowMapper mapper) {
        return parserRef.get().parse(exp, mapper);
    }

    public static QueryExpression update(String exp) {
        return parserRef.get().parse(exp, null);
    }

    public static QueryExpression updateOne(String tableName) throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildUpdateOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression insertOne(String tableName) throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildInsertOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectOne(String tableName, SQL.RowMapper mapper)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelectOne(tableName, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteOne(String tableName)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildDeleteOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
