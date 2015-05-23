package zyake.libs.sumo.expressions;

import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLRuntimeException;
import zyake.libs.sumo.SUMOException;
import zyake.libs.sumo.unsafe.DisastrousResourceManager;
import zyake.libs.sumo.util.Args;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that generates SQL expressions.
 */
public final class Expressions {

    private final static Method getNewConnection;

    private final static AtomicReference<ExpressionParser> parserRef = new AtomicReference<>(new NamedExpressionParser());

    static {
        try {
            getNewConnection = DisastrousResourceManager.class.getDeclaredMethod("getNewConnectionYouMustntCallItDirectly");
            getNewConnection.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new SUMOException(e);
        }
    }

    private Expressions() {
    }

    public static void setParserRef(ExpressionParser parser) {
        Args.check(parser);
        parserRef.set(parser);
    }

    public static QueryExpression query(String exp, SQL.RowMapper mapper) {
        Args.check(exp);
        Args.check(mapper);
        return parserRef.get().parse(exp, mapper);
    }

    public static QueryExpression update(String exp) {
        Args.check(exp);
        return parserRef.get().parse(exp, null);
    }

    public static QueryExpression updateOne(String tableName) throws SQLRuntimeException {
        Args.check(tableName);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildUpdateOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression updateWith(String tableName, String whereClause) throws SQLRuntimeException {
        Args.check(tableName);
        Args.check(whereClause);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildUpdate(tableName, whereClause);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression insertOne(String tableName) throws SQLRuntimeException {
        Args.check(tableName);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildInsertOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression insertWithoutPK(String tableName) throws SQLRuntimeException {
        Args.check(tableName);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildInsertWithoutPK(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectOne(String tableName, SQL.RowMapper mapper)
            throws SQLRuntimeException {
        Args.check(tableName);
        Args.check(mapper);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelectOne(tableName, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectWith(String tableName, String whereClause, SQL.RowMapper mapper)
            throws SQLRuntimeException {
        Args.check(tableName);
        Args.check(whereClause);
        Args.check(mapper);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelect(tableName, whereClause, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteOne(String tableName)
            throws SQLRuntimeException {
        Args.check(tableName);
        try ( Connection connection = getNewConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildDeleteOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteWith(String tableName, String whereClause)
            throws SQLRuntimeException {
        Args.check(tableName);
        Args.check(whereClause);
        try ( Connection connection = getNewConnection() ) {
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

    private static Connection getNewConnection() {
        try {
            return (Connection) getNewConnection.invoke(null, null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SUMOException(e);
        }
    }
}
