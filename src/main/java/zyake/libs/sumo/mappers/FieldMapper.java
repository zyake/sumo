package zyake.libs.sumo.mappers;

import zyake.libs.sumo.SQL;
import zyake.libs.sumo.util.Args;
import zyake.libs.sumo.util.Classes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

 /**
 * An object that maps result set values into fields of an object.
 *
 * <p>
  *     There are some constraints for byFieldOf mapping.
 * </p>
  * <ul>
  *     <li>The target as mustn't be final or static.</li>
  *     <li>The target as must be declared in the object(All of super class fields aren't used.)</li>
  * </ul>
  *
 * @param <R>
 */
public final class FieldMapper<R> implements SQL.RowMapper<R> {

    private final Class<R> target;

    private final Map<String, Field> fieldMap;

    private final boolean ignoreCase;

    public FieldMapper(Class<R> target, boolean ignoreCase) {
        Args.check(target);
        this.target = target;
        this.ignoreCase = ignoreCase;
        Map<String, Field> fieldMap = Classes.getFieldMap(target, ignoreCase);

        this.fieldMap = Collections.unmodifiableMap(fieldMap);
    }

     @Override
    public R map(ResultSet resultSet) throws SQLException, MappingFailedException {
         Args.check(resultSet);
        R object = Classes.newObject(target);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for ( int i = 1 ; i <= metaData.getColumnCount() ; i ++ ) {
            String colName = metaData.getColumnName(i);
            if ( ignoreCase ) {
                colName = colName.toUpperCase();
            }
            validateField(colName);
            Object value = resultSet.getObject(i);
            try {
                Field field = fieldMap.get(colName);
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                field.set(object, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new MappingFailedException(
                        "The field mapping failed! : target=" + target  +", field=" + colName + ", value=" + value, e);
            }
        }

        return object;
    }

    private void validateField(String colName) {
        if ( ! fieldMap.containsKey(colName) ) {
            throw new MappingFailedException(
                    "The property \"" + colName + "\" didn't exist in the entity\"" + target + "\"");
        }
    }

    public Class<R> getTarget() {
        return target;
    }
}
