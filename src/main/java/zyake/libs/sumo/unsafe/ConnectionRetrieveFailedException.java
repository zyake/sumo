package zyake.libs.sumo.unsafe;

import zyake.libs.sumo.util.Args;

public class ConnectionRetrieveFailedException extends RuntimeException {
    public ConnectionRetrieveFailedException(Throwable cause) {
        super(cause);
        Args.check(cause);
    }
}
