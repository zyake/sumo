package zyake.libs.sql.setups;

public class ParamSetupFailedException extends RuntimeException {

    public ParamSetupFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamSetupFailedException(String message) {
        super(message);
    }
}
