package zyake.libs.sql.setups;

import zyake.libs.sql.SQL;
import zyake.libs.sql.util.Classes;

import java.lang.reflect.Field;
import java.util.Map;

public class Setups {

    public static SQL.ParamSetup fieldOf(Object obj) {
        Map<String, Field> fieldMap = Classes.getFieldMap(obj.getClass(), true);
        return builder -> fieldMap.forEach((k, v) -> {
            Object fieldValue = Classes.getField(v, obj);
            builder.set(k, fieldValue);
        });
    }
}
