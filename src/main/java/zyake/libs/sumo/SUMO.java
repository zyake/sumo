package zyake.libs.sumo;

import zyake.libs.sumo.support.DefaultSQL;
import zyake.libs.sumo.tx.support.TxResourceManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SUMO {

    private static final AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();

    public static final void init(DataSource dataSource) {
        dataSourceRef.set(dataSource);
    }

    private static DataSource getDataSource() {
        if (dataSourceRef.get() == null) {
            throw new SUMOException("A DataSource must be set!");
        }
        return dataSourceRef.get();
    }

    public static <Q extends  Query> SQL<Q> newSQL(Class<Q> query, Supplier<Connection> connectionSupplier) {
        return new DefaultSQL<>(query, connectionSupplier);
    }

    public static <Q extends  Query> SQL<Q> newTransactionalSQL(Class<Q> query) {
        return new DefaultSQL<>(query, TxResourceManager::getCurrentConnection);
    }

    public static <Q extends  Query> void runSafely(Consumer<SQL<Q>> consumer, Class<Q> query) {
        try (Connection connection = getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            SQL<Q> sql = new DefaultSQL<>(query, () -> connection);
            consumer.accept(sql);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }
}
