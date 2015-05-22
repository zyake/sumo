package zyake.libs.sumo.support;

import zyake.libs.sumo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A default implementation of the SQL object.
 */
public class DefaultSQL<Q extends Query> implements SQL<Q> {

    private final Class<Q> baseQuery;

    private final Supplier<Connection> connectionSupplier;

    public DefaultSQL(Class<Q> baseQuery, Supplier<Connection> connectionSupplier) {
        this.baseQuery = baseQuery;
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public <T> List<T> query(Q query) throws SQLRuntimeException {
        validateQuery(query);
        return query(query, p -> {});
    }

    @Override
    public <T> List<T> query(Q query, ParamSetup setup)
            throws SQLRuntimeException {
        validateQuery(query);
        QueryExpression expression = query.expression();
        try ( PreparedStatement stmt = connectionSupplier.get().prepareStatement(expression.getText()) ) {
            ParamBuilder builder = new ParamBuilder();
            setup.invoke(builder);
            expression.evaluate(stmt, builder);

            try ( ResultSet resultSet = stmt.executeQuery() ) {
                List<T> entities = new ArrayList<>();
                while ( resultSet.next() ) {
                    T entity = (T) expression.getMapper().map(resultSet);
                    entities.add(entity);
                }

                return entities;
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public <T> T queryOne(Q query) throws SQLRuntimeException {
        return queryOne(query, (p)->{});
    }

    @Override
    public <T> T queryOne(Q query, ParamSetup setup) throws SQLRuntimeException {
        List<Object> queryResults = query(query, setup);
        if (queryResults.size() > 0) {
            return (T) queryResults.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int update(Q query) throws SQLRuntimeException {
        validateQuery(query);

        return update(query, p -> {
        });
    }

    @Override
    public int update(Q query, ParamSetup setup) throws SQLRuntimeException {
        validateQuery(query);

        QueryExpression expression = query.expression();
        try ( PreparedStatement stmt = connectionSupplier.get().prepareStatement(expression.getText()) ) {
            ParamBuilder builder = new ParamBuilder();
            setup.invoke(builder);
            expression.evaluate(stmt, builder);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public <T> int[] batch(Q query, Iterable<T> iterable, BatchStatementSetup<T> setup) {
        validateQuery(query);

        QueryExpression expression = query.expression();
        try ( PreparedStatement stmt = connectionSupplier.get().prepareStatement(expression.getText()) ) {
            ParamBuilder builder = new ParamBuilder();
            for ( T t : iterable ) {
                setup.invoke(builder, t);
                expression.evaluate(stmt, builder);
                stmt.addBatch();
                builder.clear();
            }

            return stmt.executeBatch();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @Override
    public <T> void cursor(Q query, CursorAcceptor<T> acceptor) {
        validateQuery(query);
        cursor(query, p -> {
        }, acceptor);
    }

    @Override
    public <T> void cursor(Q query, ParamSetup setup, CursorAcceptor<T> acceptor) {
        validateQuery(query);
        QueryExpression expression = query.expression();
        try ( PreparedStatement stmt = connectionSupplier.get().prepareStatement(
                expression.getText(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY) ) {
            ParamBuilder builder = new ParamBuilder();
            setup.invoke(builder);
            expression.evaluate(stmt, builder);

            try ( ResultSet resutSet = stmt.executeQuery() ) {
                while ( resutSet.next() ) {
                    T object = (T) expression.getMapper().map(resutSet);
                    acceptor.accept(object);
                }
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void validateQuery(Q query) {
        if ( ! baseQuery.isInstance(query) ) {
            throw new IllegalArgumentException(
                    "The object \"" + query.getClass() +"\" isn't compatible with " + baseQuery);
        }
    }
}
