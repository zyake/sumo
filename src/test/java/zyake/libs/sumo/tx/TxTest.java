package zyake.libs.sumo.tx;

import org.junit.Before;
import org.junit.Test;
import zyake.libs.sumo.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;
import static zyake.libs.sumo.expressions.Expressions.*;
import static zyake.libs.sumo.expressions.Expressions.deleteOne;
import static zyake.libs.sumo.mappers.Mappers.as;

public class TxTest extends AbstractTest {

    @Before
    public void setup2() throws SQLException {
        run(conn -> {
            try (Statement stmt = conn.createStatement()) {
                try {
                    stmt.execute("DROP TABLE HOGE");
                } catch (SQLException e) {
                    // ignore
                }
                stmt.execute("CREATE TABLE HOGE(USER_NAME VARCHAR(30), ID INT, PRIMARY KEY(ID))");
            }
        });
    }

    @Test
    public void testRun_singleStatement() throws Exception {
        Tx.run(() -> {
            SQL<HogeQueries> sql = SUMO.newTransactionalSQL(HogeQueries.class);
            sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 5).set("USER_NAME", "HAGE"));
        });

        try (Connection connection = getConnection()) {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, () -> connection);
            List<Hoge> results = sql.query(HogeQueries.SELECT_ALL);
            assertEquals("[Hoge{id=5, user_name='HAGE'}]", results.toString());
        }
    }

    @Test
    public void testRun_throw() throws Exception {
        try {
            Tx.run(() -> {
                SQL<HogeQueries> sql = SUMO.newTransactionalSQL(HogeQueries.class);
                sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 5).set("USER_NAME", "HAGE"));
                throw new RuntimeException();
            });
            fail();
        } catch (TxFailedException ex) {
        }

        try (Connection connection = getConnection()) {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, () -> connection);
            List<Hoge> results = sql.query(HogeQueries.SELECT_ALL);
            assertEquals("[]", results.toString());
        }
    }

    @Test
    public void testRun_inner() throws Exception {
        Tx.run(() -> {
            SQL<HogeQueries> sql = SUMO.newTransactionalSQL(HogeQueries.class);
            sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 5).set("USER_NAME", "HAGE"));
            Tx.run(() -> {
                sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 4).set("USER_NAME", "HAGE"));
            });
        });

        try (Connection connection = getConnection()) {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, () -> connection);
            List<Hoge> results = sql.query(HogeQueries.SELECT_ALL);
            assertEquals("[Hoge{id=4, user_name='HAGE'}, Hoge{id=5, user_name='HAGE'}]", results.toString());
        }
    }

    @Test
    public void testRun_innerThrow() throws Exception {
        try {
            Tx.run(() -> {
                SQL<HogeQueries> sql = SUMO.newTransactionalSQL(HogeQueries.class);
                sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 5).set("USER_NAME", "HAGE"));
                Tx.run(() -> {
                    sql.update(HogeQueries.INSERT_HOGE, (p) -> p.set("ID", 4).set("USER_NAME", "HAGE"));
                });
                throw new RuntimeException();
            });
            fail();
        } catch (TxFailedException e) {
        }

        try (Connection connection = getConnection()) {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, () -> connection);
            List<Hoge> results = sql.query(HogeQueries.SELECT_ALL);
            assertEquals("[]", results.toString());
        }
    }


    public static class Hoge {

        private int id;

        private String user_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        @Override
        public String toString() {
            return "Hoge{" +
                    "id=" + id +
                    ", user_name='" + user_name + '\'' +
                    '}';
        }
    }

    /**
     * An enum class to aggregate arbitrary queries.
     */
    public enum HogeQueries implements Query {
        SELECT_ALL(query("SELECT ID, USER_NAME FROM HOGE ORDER BY ID", as(Hoge.class))),
        INSERT_HOGE(insertOne("HOGE")),
        SELECT_ONE_HOGE(selectOne("HOGE", as(Hoge.class))),
        UPDATE_HOGE(updateOne("HOGE")),
        DELETE_HOGE(deleteOne("HOGE"));

        private final QueryExpression expression;

        HogeQueries(QueryExpression expression) {
            this.expression = expression;
        }

        @Override
        public QueryExpression expression() {
            return expression;
        }
    }
}