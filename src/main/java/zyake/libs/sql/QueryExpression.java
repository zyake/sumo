package zyake.libs.sql;

import java.sql.PreparedStatement;
import java.util.Map;

public interface QueryExpression {

    String getText();

    void evaluate(PreparedStatement statement, ParamBuilder param);

    SQL.RowMapper getMapper();
}