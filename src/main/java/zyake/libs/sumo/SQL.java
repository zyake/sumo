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
 * </p>
 */
public interface SQL<Q extends Query> {

    <T> List<T> query(Q query) throws SQLRuntimeException;

    <T> List<T> query(Q query, ParamSetup setup) throws SQLRuntimeException;

    <T> T queryOne(Q query) throws SQLRuntimeException;

    <T> T queryOne(Q query, ParamSetup setup) throws SQLRuntimeException;

    int update(Q query) throws SQLRuntimeException;

    int update(Q query, ParamSetup setup) throws SQLRuntimeException;

    <T> int[] batch(Q query, Iterable<T> iterable, BatchStatementSetup<T> setup) throws SQLRuntimeException;

    <T> void cursor(Q query, CursorAcceptor<T> acceptor) throws SQLRuntimeException;

    <T> void cursor(Q query, ParamSetup setup, CursorAcceptor<T> acceptor) throws SQLRuntimeException;

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