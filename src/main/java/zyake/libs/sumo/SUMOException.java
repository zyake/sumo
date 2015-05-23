package zyake.libs.sumo;

import zyake.libs.sumo.util.Args;

public class SUMOException extends RuntimeException {

    public SUMOException(String message) {
        super(message);
        Args.check(message);
    }

    public SUMOException(Exception e) {
        super(e);
        Args.check(e);
    }
}
