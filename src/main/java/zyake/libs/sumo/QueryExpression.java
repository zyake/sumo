package zyake.libs.sumo;

import java.sql.PreparedStatement;

public interface QueryExpression {

    String getText();

    void evaluate(PreparedStatement statement, ParamBuilder param);

    SQL.RowMapper getMapper();
}