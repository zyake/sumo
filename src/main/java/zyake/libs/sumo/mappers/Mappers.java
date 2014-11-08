package zyake.libs.sumo.mappers;

import zyake.libs.sumo.SQL;

public class Mappers {

    private Mappers() {
    }

    public static <R> SQL.RowMapper<R> as(Class<R> clazz) {
        return new FieldMapper<>(clazz, true);
    }
}
