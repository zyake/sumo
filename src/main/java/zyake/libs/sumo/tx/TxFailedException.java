package zyake.libs.sumo.tx;

public class TxFailedException extends RuntimeException {

    public TxFailedException(String message, RuntimeException ex) {
        super(message, ex);
    }
}
