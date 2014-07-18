package net.acomputerdog.BlazeLoader.util.java;

public class StringUtils {
    public static String trimFromStart(String str, String prefix) {
        if (str == null) return null;
        if (str.startsWith(prefix)) {
            return str.substring(str.indexOf(prefix));
        }
        return str;
    }
}
