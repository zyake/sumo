package zyake.libs.sumo.setups;

import zyake.libs.sumo.util.Args;

public class ParamSetupFailedException extends RuntimeException {

    public ParamSetupFailedException(String message, Throwable cause) {
        super(message, cause);
        Args.check(message);
        Args.check(cause);
    }

    public ParamSetupFailedException(String message) {
        super(message);
        Args.check(message);
    }
}
