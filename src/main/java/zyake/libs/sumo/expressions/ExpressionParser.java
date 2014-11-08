package zyake.libs.sumo.expressions;

import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;

public interface ExpressionParser {

    QueryExpression parse(String sqlQuery, SQL.RowMapper mapper) throws ExpressionParseFailedException;
}
