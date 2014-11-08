package zyake.libs.sql.expressions;

import org.junit.Before;
import org.junit.Test;
import zyake.libs.sql.AbstractTest;
import zyake.libs.sql.QueryExpression;

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
                } catch (SQLException e) {
                    // ignore
                }
                stmt.execute("CREATE TABLE HOGE(USER_NAME VARCHAR(30), ID INT, PRIMARY KEY(ID))");
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
    public void testBuildUpdateOne() throws Exception {
        run(conn -> {
            DynamicExpressionBuilder builder = new DynamicExpressionBuilder(conn);
            QueryExpression expression = builder.buildUpdateOne("HOGE");
            assertEquals("UPDATE HOGE SET USER_NAME=? WHERE ID=?", expression.getText());
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