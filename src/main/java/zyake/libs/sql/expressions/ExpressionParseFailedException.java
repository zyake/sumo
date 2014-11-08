package zyake.libs.sql.expressions;

public class ExpressionParseFailedException extends RuntimeException {

    public ExpressionParseFailedException(String message) {
        super(message);
    }

    public ExpressionParseFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
