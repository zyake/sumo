package zyake.libs.sumo;

public class SUMOException extends RuntimeException {

    public SUMOException(String message) {
        super(message);
    }

    public SUMOException(Exception e) {
        super(e);
    }
}
