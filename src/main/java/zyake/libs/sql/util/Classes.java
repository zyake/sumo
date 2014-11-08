package zyake.libs.sql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Classes {

    private Classes() {
    }

    public static <T> T newObject(Class<T> target) {
        try {
            return target.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UtilRuntimeException("Instantiation failed! : target=" + target, e);
        }
    }

    public static <T> T getField(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new UtilRuntimeException("The field value couldn't be gotten! : field=" + field + ", object=" + obj, e);
        }

    }

    public static Map<String, Field> getFieldMap(Class target, boolean ignoreCase) {
        Map<String, Field> fieldMap = new HashMap<>();
        Field[] declaredFields = target.getDeclaredFields();
        for ( Field field : declaredFields ) {
            int modifiers = field.getModifiers();
            boolean isNotTarget = Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
            if ( isNotTarget ) {
                continue;
            }

            field.setAccessible(true);
            String fieldName = ignoreCase ? field.getName().toUpperCase() : field.getName();
            fieldMap.put(fieldName, field);
        }
        return fieldMap;
    }
}
