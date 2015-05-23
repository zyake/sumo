package zyake.libs.sumo.expressions;

import zyake.libs.sumo.util.Args;

public class ExpressionParseFailedException extends RuntimeException {

    public ExpressionParseFailedException(String message) {
        super(message);
        Args.check(message);
    }

    public ExpressionParseFailedException(String message, Throwable cause) {
        super(message, cause);
        Args.check(message);
        Args.check(cause);
    }
}
