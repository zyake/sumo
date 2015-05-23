package zyake.libs.sumo.mappers;

import zyake.libs.sumo.SQL;
import zyake.libs.sumo.util.Args;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class Mappers {

    private static final SQL.RowMapper<Void> NULL_MAPPER = new SQL.RowMapper() {
        @Override
        public Object map(ResultSet resultSet) throws SQLException, MappingFailedException {
            return null;
        }
    };

    private Mappers() {
    }

    public static <R> SQL.RowMapper<R> as(Class<R> clazz) {
        Args.check(clazz);
        return new FieldMapper<>(clazz, true);
    }

    public static SQL.RowMapper<Void> asNull() {
        return NULL_MAPPER;
    }
}
