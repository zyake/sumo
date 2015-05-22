package zyake.libs.sumo.tx.support;

import java.sql.Connection;
import java.util.Stack;

public final class TxResourceManager {

    private static final ThreadLocal<Stack<Connection>> localConnection = new ThreadLocal<>();

    public static Connection getCurrentConnection() {
        if ( localConnection.get() == null ) {
            localConnection.set(new Stack<>());
        }
        return localConnection.get().peek();
    }

    public static void pushCurrentConnection(Connection connection) {
        if ( localConnection.get() == null ) {
            localConnection.set(new Stack<>());
        }
        localConnection.get().push(connection);
    }

    public static void popCurrentConnection() {
        localConnection.get().pop();
    }
}
