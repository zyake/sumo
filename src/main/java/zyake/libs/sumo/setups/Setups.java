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
}
