package zyake.libs.sumo;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

    private final SQLException sqlException;

    public SQLRuntimeException(SQLException sqlException) {
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return sqlException;
    }
}
