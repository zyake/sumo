package zyake.libs.sql.expressions;

import zyake.libs.sql.QueryExpression;
import zyake.libs.sql.SQL;

public interface ExpressionParser {

    QueryExpression parse(String sqlQuery, SQL.RowMapper mapper) throws ExpressionParseFailedException;
}
