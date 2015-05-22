package zyake.libs.sumo.expressions;

import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLRuntimeException;
import zyake.libs.sumo.unsafe.SUMOUnsafe;

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

    public static QueryExpression updateWith(String tableName, String whereClause) throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildUpdate(tableName, whereClause);
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

    public static QueryExpression insertWithoutPK(String tableName) throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildInsertWithoutPK(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectOne(String tableName, SQL.RowMapper mapper)
            throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelectOne(tableName, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectWith(String tableName, String whereClause, SQL.RowMapper mapper)
            throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelect(tableName, whereClause, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteOne(String tableName)
            throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildDeleteOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteWith(String tableName, String whereClause)
            throws SQLRuntimeException {
        try ( Connection connection = SUMOUnsafe.getRuntimeDataSource().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildDelete(tableName, whereClause);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static String limit() {
        return " LIMIT {__LIMIT__}";
    }

    public static String limitAndOffset() {
        return " LIMIT {__LIMIT__}, OFFSET {__OFFSET__}";
    }
}
