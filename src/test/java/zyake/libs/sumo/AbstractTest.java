package zyake.libs.sumo;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import zyake.libs.sumo.unsafe.DisastrousResourceManager;

import java.sql.*;

public abstract class AbstractTest {

    protected static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    }

    @BeforeClass
    public static void setupBase() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:~/test");
        ds.setUser("sa");
        ds.setPassword("");
        SUMO.init(ds);
        DisastrousResourceManager.setDebug(true);
    }

    protected void run(ArbitraryStatementExecutor executor) {
        try ( Connection conn = getConnection() ) {
            executor.run(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void execute(Connection conn, String sql) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute(sql);
    }

    protected ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        return statement.executeQuery();
    }

    @FunctionalInterface
    public interface ArbitraryStatementExecutor {
        void run(Connection conn) throws SQLException;
    }
}
