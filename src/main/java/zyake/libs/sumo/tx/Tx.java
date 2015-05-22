package zyake.libs.sumo.tx;

import zyake.libs.sumo.SQLRuntimeException;
import zyake.libs.sumo.tx.support.TxResourceManager;
import zyake.libs.sumo.unsafe.SUMOUnsafe;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A Simple transaction runner that supports propagation of transaction boundary.
 */
public final class Tx {

    protected static final AtomicReference<Consumer<Runnable>> txDelegate = new AtomicReference<>() ;

    protected static final AtomicReference<Consumer<Runnable>> txAsNewDelegate = new AtomicReference<>();

    private static final Consumer<Runnable> DEFAULT_TX_AS_NEW_RUNNER = (runnable) -> {
        Connection connection;
        try {
            connection = SUMOUnsafe.getRuntimeDataSource().getConnection();
            connection.setAutoCommit(false);
            TxResourceManager.pushCurrentConnection(connection);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        try {
            try {
                runnable.run();
                connection.commit();
            } catch (RuntimeException ex) {
                connection.rollback();
                throw new TxFailedException("Transaction failed!", ex);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }finally {
            TxResourceManager.popCurrentConnection();
        }
    };

    private static final Consumer<Runnable> DEFAULT_TX_RUNNER = (runnable) -> {
        Connection connection = TxResourceManager.getCurrentConnection();
        boolean useNewConnection = false;
        if ( connection == null ) {
            useNewConnection = true;
            try {
                connection = SUMOUnsafe.getRuntimeDataSource().getConnection();
                connection.setAutoCommit(false);
                TxResourceManager.pushCurrentConnection(connection);
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }
        try {
            try {
                runnable.run();
                if (useNewConnection) {
                    connection.commit();
                }
            } catch (RuntimeException ex) {
                connection.rollback();
                throw new TxFailedException("Transaction failed!", ex);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }finally {
            if (useNewConnection) {
                TxResourceManager.popCurrentConnection();
            }
        }
    };

    static {
        txDelegate.set(DEFAULT_TX_RUNNER);
        txAsNewDelegate.set(DEFAULT_TX_AS_NEW_RUNNER);
    }

    public static void run(Runnable runnable) throws SQLRuntimeException, TxFailedException {
        txDelegate.get().accept(runnable);
    }

    public static void runAsNew(Runnable runnable) throws SQLRuntimeException, TxFailedException {
        txAsNewDelegate.get().accept(runnable);
    }
}
