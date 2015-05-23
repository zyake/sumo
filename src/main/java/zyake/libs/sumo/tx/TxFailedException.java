package zyake.libs.sumo.tx;

import zyake.libs.sumo.util.Args;

public class TxFailedException extends RuntimeException {

    public TxFailedException(String message, RuntimeException ex) {
        super(message, ex);
        Args.check(message);
        Args.check(ex);
    }
}
