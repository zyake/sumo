package zyake.libs.sumo.mappers;

import zyake.libs.sumo.SQL;

import java.util.Map;

public class Mappers {

    private Mappers() {
    }

    public static <R> SQL.RowMapper<R> as(Class<R> clazz) {
        return new FieldMapper<>(clazz, true);
    }

    public static SQL.RowMapper<Map<String, Object>> collection() {
        return new CollectionMapper();
    }
}
