package zyake.libs.sumo.setups;

import zyake.libs.sumo.SQL;
import zyake.libs.sumo.util.Classes;

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

    public static SQL.ParamSetup limit(int limit) {
       return builder -> builder.set("__LIMIT__", limit);
    }

    public static SQL.ParamSetup limit(int limit, int page) {
        return builder -> builder.set("__LIMIT__", limit).set("__OFFSET__", limit * page);
    }

    public static SQL.ParamSetup allOf(SQL.ParamSetup... setups) {
        return builder -> {
           for (SQL.ParamSetup setup : setups) {
               setup.invoke(builder);
           }
        };
    }
}
