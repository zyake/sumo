package zyake.libs.sumo.expressions;

import zyake.libs.sumo.util.Args;

public class NoSetCaluseException extends RuntimeException {

    public NoSetCaluseException(String message) {
        super(message);
        Args.check(message);
    }
}
