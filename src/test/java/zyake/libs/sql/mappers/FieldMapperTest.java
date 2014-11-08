package zyake.libs.sql.mappers;

import org.junit.Before;
import org.junit.Test;
import zyake.libs.sql.AbstractTest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.Assert.assertEquals;

public class FieldMapperTest extends AbstractTest {

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
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
    public void testMap_allMatch() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('TEST', 5)");
        });

        run(conn -> {
            ResultSet resutSet = executeQuery(conn, "SELECT USER_NAME, ID FROM HOGE");
            resutSet.next();

            FieldMapper<Hoge> mapper = new FieldMapper<>(Hoge.class, true);
            Hoge hoge = mapper.map(resutSet);

            assertEquals(hoge.toString(), "Hoge{id=5, user_name=\'TEST\'}");
        });
    }

    @Test(expected = MappingFailedException.class)
    public void testMap_noMatchedField() throws Exception {
        run(conn -> {
            execute(conn, "INSERT INTO HOGE(USER_NAME, ID) VALUES('TEST', 5)");
        });

        run(conn -> {
            ResultSet resutSet = executeQuery(conn, "SELECT USER_NAME, ID FROM HOGE");
            resutSet.next();

            FieldMapper<FieldMapperTest> mapper = new FieldMapper<>(FieldMapperTest.class, true);
            mapper.map(resutSet);
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
}