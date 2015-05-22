package zyake.libs.sumo.expressions;

import org.junit.Before;
import org.junit.Test;
import zyake.libs.sumo.AbstractTest;
import zyake.libs.sumo.QueryExpression;

import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;

public class DynamicExpressionBuilderTest extends AbstractTest {

    @Before
    public void setup() throws SQLException {
        run(conn -> {
            try (Statement stmt = conn.createStatement()) {
                try {
                    stmt.execute("DROP TABLE HOGE");
                    stmt.execute("DROP TABLE HAGE");
                } catch (SQLException e) {
                    // ignore
                }
                stmt.execute("CREATE TABLE HOGE(USER_NAME VARCHAR(30), ID INT, PRIMARY KEY(ID))");
                stmt.execute("CREATE TABLE HAGE(NUM INT, USER_NAME VARCHAR(30), ID INT, PRIMARY KEY(ID, USER_NAME))");
            }
        });
    }

    @Test
    public void testBuildSelectOne() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildSelectOne("HOGE", r -> { return null; });
            assertEquals("SELECT USER_NAME,ID FROM HOGE WHERE ID=?", expression.getText());
        });
    }

    @Test
    public void testBuildSelectOne_compositeKey() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildSelectOne("HAGE", r -> { return null; });
            assertEquals("SELECT NUM,USER_NAME,ID FROM HAGE WHERE ID=? AND USER_NAME=?", expression.getText());
        });
    }

    @Test
    public void testBuildUpdateOne() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildUpdateOne("HOGE");
            assertEquals("UPDATE HOGE SET USER_NAME=? WHERE ID=?", expression.getText());
        });
    }

    @Test
    public void testBuildUpdateOne_compositeKey() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildUpdateOne("HAGE");
            assertEquals("UPDATE HAGE SET NUM=? WHERE ID=? AND USER_NAME=?", expression.getText());
        });
    }

    @Test
    public void testBuildInsertOne() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildInsertOne("HOGE");
            assertEquals("INSERT INTO HOGE(USER_NAME,ID)VALUES(?,?)", expression.getText());
        });
    }

    @Test
    public void testBuildDeleteOne() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildDeleteOne("HOGE");
            assertEquals("DELETE FROM HOGE WHERE ID=?", expression.getText());
        });
    }
}