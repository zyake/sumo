package zyake.libs.sql;

/**
 * a typed query object.
 *
 * <p>
 *     You can define own queries by enum class or {@link zyake.libs.sql.expressions.DynamicExpressionBuilder}.
 * </p>
 */
public interface Query {

    /**
     * get an actual SQL query expression.
     *
     * @return
     */
    QueryExpression expression();
}
