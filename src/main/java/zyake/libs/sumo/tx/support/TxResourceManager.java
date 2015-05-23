package zyake.libs.sumo.tx.support;

import zyake.libs.sumo.util.Args;

import java.sql.Connection;
import java.util.Stack;

public final class TxResourceManager {

    private static final ThreadLocal<Stack<Connection>> localConnection = new ThreadLocal<>();

    private TxResourceManager() {
    }

    public static Connection getCurrentConnection() {
        if ( localConnection.get() == null ) {
            localConnection.set(new Stack<>());
        }
        if ( localConnection.get().size() > 0 ) {
            return localConnection.get().peek();
        } else {
            return null;
        }
    }

    public static void pushCurrentConnection(Connection connection) {
        Args.check(connection);
        if ( localConnection.get() == null ) {
            localConnection.set(new Stack<>());
        }
        localConnection.get().push(connection);
    }

    public static void popCurrentConnection() {
        localConnection.get().pop();
    }
}
