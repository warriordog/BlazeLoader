package com.blazeloader.api.core.base.util.math;

/**
 * Math utility functions
 */
public class MathUtils {
    /**
     * Checks if a string represents a number.
     *
     * @param str The string to test.
     * @return Return true if the string is an integer.
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }
}
