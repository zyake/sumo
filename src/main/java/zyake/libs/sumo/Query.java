package zyake.libs.sumo;

/**
 * a typed query object.
 */
public interface Query {

    /**
     * get an actual SQL query expression.
     *
     * @return
     */
    QueryExpression expression();
}
