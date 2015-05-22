package zyake.libs.sumo.unsafe;

import zyake.libs.sumo.SUMO;
import zyake.libs.sumo.SUMOException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class SUMOUnsafe {

    private static final Method getDataSource;

    static {
        try {
           getDataSource = SUMO.class.getDeclaredMethod("getDataSource");
            getDataSource.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new SUMOException(e);
        }
    }

    public static DataSource getRuntimeDataSource() {
        try {
            return (DataSource) getDataSource.invoke(null, null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
