package zyake.libs.sql.expressions;

import zyake.libs.sql.QueryExpression;
import zyake.libs.sql.SQL;
import zyake.libs.sql.SQLRuntimeException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An object that builds SQL queries by database metadata.
 */
public class DynamicExpressionBuilder {

    private static final int COLUMN_NAME = 4;

    private final Connection connection;

    private final ExpressionParser parser;

    public DynamicExpressionBuilder(Connection connection, ExpressionParser parser) {
        this.connection = connection;
        this.parser = parser;
    }

    public DynamicExpressionBuilder(Connection connection) {
        this.connection = connection;
        this.parser = new NamedExpressionParser();
    }

    public QueryExpression buildUpdateOne(String tableName) throws SQLRuntimeException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            StringBuilder query = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            String primaryKey = getPrimaryKey(tableName, metaData);
            addSetClause(tableName, metaData, query, primaryKey);
            query.append(" WHERE ");
            addPrimaryKeyClause(tableName, metaData, query);

            return parser.parse(query.toString(), null);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public QueryExpression buildInsertOne(String tableName) throws SQLRuntimeException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append("(");
            List<String> columns = addColumnNames(tableName, metaData, query);
            query.append(")");
            addValues(columns, query);

            return parser.parse(query.toString(), null);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public QueryExpression buildSelectAll(String tableName, SQL.RowMapper mapper)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            StringBuilder query = new StringBuilder("SELECT ");

            addColumnNames(tableName, metaData, query);
            query.append(" FROM ").append(tableName);

            return parser.parse(query.toString(), mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public QueryExpression buildSelectOne(String tableName, SQL.RowMapper mapper)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            StringBuilder query = new StringBuilder("SELECT ");

            addColumnNames(tableName, metaData, query);
            query.append(" FROM ").append(tableName).append(" WHERE ");
            addPrimaryKeyClause(tableName, metaData, query);

            return parser.parse(query.toString(), mapper);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public QueryExpression buildDeleteOne(String tableName)
            throws MultiplePrimaryKeyException, SQLRuntimeException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            StringBuilder query = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .append(" WHERE ");
            addPrimaryKeyClause(tableName, metaData, query);

            return parser.parse(query.toString(), null);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    private void addPrimaryKeyClause(String tableName, DatabaseMetaData metaData, StringBuilder query) throws SQLException {
        try ( ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName) ) {
            primaryKeys.next();
            String columnName = primaryKeys.getString(COLUMN_NAME);
            query.append(columnName).append("={").append(columnName).append("}");
            if ( primaryKeys.next() ) {
                throw new MultiplePrimaryKeyException("Multiple primary key found! : target=" + tableName);
            }
        }
    }

    private String getPrimaryKey(String tableName, DatabaseMetaData metaData) throws SQLException {
        try ( ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName) ) {
            primaryKeys.next();
            String columnName = primaryKeys.getString(COLUMN_NAME);
            if ( primaryKeys.next() ) {
                throw new MultiplePrimaryKeyException("Multiple primary key found! : target=" + tableName);
            }
            return columnName;
        }
    }

    private List<String> addColumnNames(String tableName, DatabaseMetaData metaData, StringBuilder query) throws SQLException {
        List<String> addedColumns = new ArrayList<>();
        try ( ResultSet columns = metaData.getColumns(null, null, tableName, null) ) {
            while ( columns.next() ) {
                String columnName = columns.getString(COLUMN_NAME);
                query.append(columnName).append(",");
                addedColumns.add(columnName);
            }
            if (query.charAt(query.length() - 1) == ',') {
                query.deleteCharAt(query.length() - 1);
            }
        }
        return addedColumns;
    }

    private void addValues(List<String> columns, StringBuilder query) {
        query.append("VALUES(");
        for ( int i = 0 ; i < columns.size() ; i ++ ) {
            query.append("{").append(columns.get(i)).append("},");
        }
        if (  columns.size() > 0 ) {
            query.deleteCharAt(query.length() - 1);
        }
        query.append(")");
    }

    private void addSetClause(String tableName, DatabaseMetaData metaData, StringBuilder query, String primaryKey)
            throws SQLException {
        try ( ResultSet columns = metaData.getColumns(null, null, tableName, null) ) {
            while ( columns.next() ) {
                String columnName = columns.getString(COLUMN_NAME);
                if ( primaryKey.equals(columnName) ) {
                    continue;
                }
                query.append(columnName).append("={").append(columnName).append("},");
            }
            if (query.charAt(query.length() - 1) == ',') {
                query.deleteCharAt(query.length() - 1);
            }
        }
    }
}
