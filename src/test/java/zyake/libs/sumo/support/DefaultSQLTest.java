package zyake.libs.sumo.support;

import org.junit.Before;
import org.junit.Test;
import zyake.libs.sumo.*;
import zyake.libs.sumo.setups.Setups;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static zyake.libs.sumo.expressions.Expressions.*;
import static zyake.libs.sumo.mappers.Mappers.as;
import static zyake.libs.sumo.setups.Setups.fieldOf;
import static zyake.libs.sumo.support.DefaultSQLTest.HogeQueries.*;

public class DefaultSQLTest extends AbstractTest {

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
         public void testQuery_selectOne() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
        });

        try ( Connection conn = getConnection() ) {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, () -> conn);
            List<Hoge> hoges = sql.query(SELECT_ALL);

            assertEquals(hoges.toString(), "[Hoge{id=6, user_name=\'NAME\'}]");
        }
    }

    @Test
    public void testQuery_selectTwo() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 8)");
        });

        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            List<Hoge> hoges = sql.query(SELECT_ALL);
            assertEquals(hoges.toString(),
                    "[Hoge{id=6, user_name=\'NAME\'}, Hoge{id=8, user_name='NAME2'}]");
        });
    }

    @Test
    public void testQuery1_selectOne() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 8)");
        });

        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            List<Hoge> hoges = sql.query(SELECT_ALL_ID_GRETER_THAN,
                    p -> p.set("id", 6));

            assertEquals(hoges.toString(),
                    "[Hoge{id=8, user_name='NAME2'}]");
        });
    }

    @Test
    public void testQuery1_withFieldMapper() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 8)");
        });

        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            List<Hoge> hoges = sql.query(SELECT_ALL_ID_GRETER_THAN,
                    p -> p.set("id", 6));

            assertEquals(hoges.toString(),
                    "[Hoge{id=8, user_name='NAME2'}]");
        });
    }

    @Test
    public void testQuery1_withFieldMapperAndSelectOne() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 8)");
        });

        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            Hoge hoge = new Hoge();
            hoge.setId(6);
            hoge.setUser_name("");
            List<Hoge> hoges = sql.query(SELECT_ONE_HOGE,  fieldOf(hoge));

            assertEquals(hoges.toString(),
                    "[Hoge{id=6, user_name='NAME'}]");
        });
    }

    @Test
    public void testExecuteUpdate1() throws Exception {
        run(conn -> {
            Hoge hoge = new Hoge();
            hoge.setUser_name("test");
            hoge.setId(1);
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            int result = sql.update(INSERT_HOGE, fieldOf(hoge));

            assertEquals(1, result);
        });
    }

    @Test
    public void testExecuteUpdate2() throws Exception {
        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);

            Hoge hoge = new Hoge();
            hoge.setUser_name("test");
            hoge.setId(1);
            int result = sql.update(INSERT_HOGE, fieldOf(hoge));

            assertEquals(1, result);

            hoge.setUser_name("CHANGED!");
            int update = sql.update(UPDATE_HOGE, fieldOf(hoge));

            assertEquals(1, update);
        });
    }

    @Test
    public void testBatch1() {
        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);

            AtomicInteger counter = new AtomicInteger();
            List<String> names = Arrays.asList("PIYO", "HOGE", "HUGERA");
            int[] batchResult = sql.batch(HogeQueries.INSERT_HOGE, names, (builder, name) -> {
                builder
                .set("ID", counter.incrementAndGet())
                .set("USER_NAME", name);
            });

            assertEquals(names.size(), batchResult.length);
            assertTrue(Arrays.binarySearch(batchResult, PreparedStatement.EXECUTE_FAILED) < 0);
        });
    }

    @Test
    public void testCursor1() {
        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);

            sql.update(INSERT_HOGE, p -> p.set("USER_NAME", "HAGE").set("ID", 1));
            sql.update(INSERT_HOGE, p -> p.set("USER_NAME", "HOGE").set("ID", 2));
            sql.update(INSERT_HOGE, p -> p.set("USER_NAME", "PIYO").set("ID", 3));

            Set<String> names = new HashSet<>();
            sql.cursor(SELECT_ALL, (Hoge hoge) -> {
                names.add(hoge.getUser_name());
            });

            assertEquals(3, names.size());
            assertTrue(names.contains("HAGE"));
            assertTrue(names.contains("HOGE"));
            assertTrue(names.contains("PIYO"));
        });
    }

    @Test
    public void testDelete1() {
        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            sql.update(INSERT_HOGE, p -> p.set("USER_NAME", "HAGE").set("ID", 1));

            int count = sql.update(DELETE_HOGE, p -> p.set("ID", 1));

            assertEquals(1, count);
        });
    }

    @Test
    public void testQuery_selectFirstTwo() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME', 6)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 8)");
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('NAME2', 10)");
        });

        run(conn -> {
            SQL<HogeQueries> sql = SUMO.newSQL(HogeQueries.class, ()->conn);
            List<Hoge> hoges = sql.query(SELECT_ALL_FIRST_TWO, Setups.allOf(Setups.limit(1), b -> b.set("ID", 6)));
            assertEquals(hoges.toString(),
                    "[Hoge{id=8, user_name='NAME2'}]");
        });
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
        SELECT_ALL_FIRST_TWO(selectWith("HOGE", "ID > {ID} " + limit(), as(Hoge.class))),
        SELECT_ALL(query("SELECT ID, USER_NAME FROM HOGE", as(Hoge.class))),
        SELECT_ALL_ID_GRETER_THAN(selectWith("HOGE", "ID > {id} ORDER BY ID", as(Hoge.class))),
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