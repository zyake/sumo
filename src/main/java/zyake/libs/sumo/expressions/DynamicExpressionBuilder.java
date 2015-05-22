package zyake.libs.sumo.expressions;

import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLRuntimeException;

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
            List<String> primaryKeys = getPrimaryKeys(tableName, metaData);
            addSetClause(tableName, metaData, query, primaryKeys);
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

    public QueryExpression buildSelectOne(String tableName, SQL.RowMapper mapper)
            throws SQLRuntimeException {
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
            throws SQLRuntimeException {
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
            if (!primaryKeys.next()) {
                return;
            }

            String columnName = primaryKeys.getString(COLUMN_NAME);
            query.append(columnName).append("={").append(columnName).append("}");
            if ( primaryKeys.next() ) {
                query.append(" AND ");
                String columnName1 = primaryKeys.getString(COLUMN_NAME);
                query.append(columnName1).append("={").append(columnName1).append("}");
            }
        }
    }

    private List<String> getPrimaryKeys(String tableName, DatabaseMetaData metaData) throws SQLException {
        List<String> keys = new ArrayList<>();
        try ( ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName) ) {
            while(primaryKeys.next()) {
                keys.add(primaryKeys.getString(COLUMN_NAME));
            }
            if (keys.size() == 0) {
                throw new NoPrimaryKeyException("At least one primary key has needed!: table=" + tableName);
            }
        }
        return keys;
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

    private void addSetClause(String tableName, DatabaseMetaData metaData, StringBuilder query, List<String> primaryKeys)
            throws SQLException {
        boolean setExists = false;
        try ( ResultSet columns = metaData.getColumns(null, null, tableName, null) ) {
            while ( columns.next() ) {
                String columnName = columns.getString(COLUMN_NAME);
                if ( primaryKeys.contains(columnName) ) {
                    continue;
                }
                setExists = true;
                query.append(columnName).append("={").append(columnName).append("},");
            }
            if (query.charAt(query.length() - 1) == ',') {
                query.deleteCharAt(query.length() - 1);
            }
        }
        if ( !setExists ) {
            throw new NoSetCaluseException("The table that is consisted by a composite key only!: table=" + tableName);
        }
    }
}
