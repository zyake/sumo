package zyake.libs.sumo;

import zyake.libs.sumo.mappers.MappingFailedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An object that executes SQL queries.
 *
 * <p>
 *     A very thin wrapper of a JDBC connection.
 *     It is paired with a JDBC connection and executes specified typed queries.
 *     It doesn't manage the connection, so you should manage it by DI container, JTA and so on.
 *     It mustn't be referred by object fields because a typical lifecycle of a SQL object is within a method.
 * </p>
 */
public interface SQL<Q extends Query> {

    <T> List<T> query(Q query) throws SQLRuntimeException;

    <T> List<T> query(Q query, ParamSetup setup) throws SQLRuntimeException;

    int update(Q query) throws SQLRuntimeException;

    int update(Q query, ParamSetup setup) throws SQLRuntimeException;

    <T> int[] batch(Q query, Iterable<T> iterable, BatchStatementSetup<T> setup);

    <T> void cursor(Q query, CursorAcceptor<T> acceptor);

    <T> void cursor(Q query, ParamSetup setup, CursorAcceptor<T> acceptor);

    @FunctionalInterface
    public interface ParamSetup {
        public void invoke(ParamBuilder builder);
    }

    @FunctionalInterface
    public interface BatchStatementSetup<T> {
        public void invoke(ParamBuilder builder, T t);
    }

    @FunctionalInterface
    public interface RowMapper<R> {
        public R map(ResultSet resultSet) throws SQLException, MappingFailedException;
    }

    @FunctionalInterface
    public interface CursorAcceptor<R> {
        public void accept(R entity);
    }
}