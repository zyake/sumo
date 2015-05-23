package zyake.libs.sumo.expressions;

import zyake.libs.sumo.util.Args;

public class NoPrimaryKeyException extends RuntimeException {

    public NoPrimaryKeyException(String message) {
        super(message);
        Args.check(message);
    }
}
