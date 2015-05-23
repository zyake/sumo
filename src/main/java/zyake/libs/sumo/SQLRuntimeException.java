package zyake.libs.sumo;

import zyake.libs.sumo.util.Args;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

    private final SQLException sqlException;

    public SQLRuntimeException(SQLException sqlException) {
        Args.check(sqlException);
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return sqlException;
    }
}
