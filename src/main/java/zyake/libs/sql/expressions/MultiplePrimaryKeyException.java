package zyake.libs.sql.expressions;

public class MultiplePrimaryKeyException extends RuntimeException {

    public MultiplePrimaryKeyException(String message) {
        super(message);
    }
}
