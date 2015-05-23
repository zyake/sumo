package zyake.libs.sumo.unsafe;

import zyake.libs.sumo.SUMO;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * YOU MUST NOT TOUCH IT DIRECTLY!!!
 */
public final class DisastrousResourceManager {

    private static final AtomicBoolean debugEnabled = new AtomicBoolean(false);

    private static final AtomicReference<DataSource> getDataSourceRef = new AtomicReference<>();

    private DisastrousResourceManager() {
    }

    public static void setDebug(boolean debug) {
        debugEnabled.set(debug);
    }

    private static Connection getNewConnectionYouMustntCallItDirectly() {
        if (debugEnabled.get()) {
            try {
                return createDebuggableConnection(getDataSourceRef.get().getConnection());
            } catch (SQLException e) {
                throw new ConnectionRetrieveFailedException(e);
            }
        }

        try {
            return getDataSourceRef.get().getConnection();
        } catch (SQLException e) {
            throw new ConnectionRetrieveFailedException(e);
        }
    }

    private static Connection createDebuggableConnection(Connection connection) {
        return (Connection) Proxy.newProxyInstance(SUMO.class.getClassLoader(), new Class[]{Connection.class}, new InvocationHandler() {
            private final Logger LOG = Logger.getLogger(SUMO.class + " - Connection");

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String argDump = args == null ? "" : Arrays.asList(args).toString();
                LOG.info("method called!: method=" + method.getName() + ", args=" + argDump + ", connection=" + connection.toString());
                return method.invoke(connection, args);
            }
        });
    }
}
