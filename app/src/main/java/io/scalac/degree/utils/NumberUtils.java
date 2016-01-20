package io.scalac.degree.utils;

public abstract class NumberUtils {

    public static int compareLong(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    private NumberUtils() {
        // Nothing here.
    }
}
