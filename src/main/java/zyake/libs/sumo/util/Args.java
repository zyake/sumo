package zyake.libs.sumo.util;

public final class Args {

    public static void check(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("THE ARGUMENT IS NULL!");
        }
    }
}
