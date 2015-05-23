package zyake.libs.sumo.mappers;

import zyake.libs.sumo.util.Args;

public class MappingFailedException extends RuntimeException {

    public MappingFailedException(String message) {
        super(message);
        Args.check(message);
    }

    public MappingFailedException(String message, Throwable cause) {
        super(message, cause);
        Args.check(message);
        Args.check(cause);
    }
}
