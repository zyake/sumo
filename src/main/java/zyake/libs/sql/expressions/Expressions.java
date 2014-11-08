package zyake.libs.sql.expressions;

import zyake.libs.sql.QueryExpression;
import zyake.libs.sql.SQL;
import zyake.libs.sql.SQLRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that generates SQL expressions.
 *
 * <p>
 *     If you want to generate a SQL expression automatically,
 *     you must call the {@link #setDataSource(javax.sql.DataSource)} method
 *     before other methods invocation.
 * </p>
 */
public class Expressions {

    private final static AtomicReference<ExpressionParser> parserRef = new AtomicReference<>(new NamedExpressionParser());

    private final static AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();

    private Expressions() {
    }

    public static void setParserRef(ExpressionParser parser) {
        parserRef.set(parser);
    }

    public static void setDataSource(DataSource dataSource) {
        dataSourceRef.set(dataSource);
    }

    public static QueryExpression query(String exp, SQL.RowMapper mapper) {
        return parserRef.get().parse(exp, mapper);
    }

    public static QueryExpression update(String exp) {
        return parserRef.get().parse(exp, null);
    }

    public static QueryExpression updateOne(String tableName) throws SQLRuntimeException {
        try ( Connection connection = dataSourceRef.get().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildUpdateOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression insertOne(String tableName) throws SQLRuntimeException {
        try ( Connection connection = dataSourceRef.get().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildInsertOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectAll(String tableName, SQL.RowMapper mapper)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try ( Connection connection = dataSourceRef.get().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelectAll(tableName, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression selectOne(String tableName, SQL.RowMapper mapper)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try ( Connection connection = dataSourceRef.get().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildSelectOne(tableName, mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static QueryExpression deleteOne(String tableName)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try ( Connection connection = dataSourceRef.get().getConnection() ) {
            return new DynamicExpressionBuilder(connection, parserRef.get()).buildDeleteOne(tableName);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
