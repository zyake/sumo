package zyake.libs.sql.mappers;

import zyake.libs.sql.SQL;

public class Mappers {

    private Mappers() {
    }

    public static <R> SQL.RowMapper<R> as(Class<R> clazz) {
        return new FieldMapper<>(clazz, true);
    }
}
