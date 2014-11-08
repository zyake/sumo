package zyake.libs.sumo.mappers;

public class MappingFailedException extends RuntimeException {

    public MappingFailedException(String message) {
        super(message);
    }

    public MappingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
