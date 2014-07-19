package net.acomputerdog.BlazeLoader.util.obf;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.MethodInfo;

import java.util.regex.Pattern;

/**
 * BL extension of MethodInfo that allows getting all data from a single obfuscation
 */
public class BLMethodInfo extends MethodInfo {
    private BLMethodInfo(Obf owner, String method, String desc) {
        super(owner, method, desc);
    }

    private static final String SPACE = Pattern.quote(" ");
    private static final String PERIOD = Pattern.quote(".");

    public static BLMethodInfo create(String method) {
        if (method == null) {
            return null;
        }
        String[] methAndDesc = method.split(SPACE);
        if (methAndDesc.length < 2) {
            throw new IllegalArgumentException("Method ID must contain method name and descriptor separated by a space!");
        }
        String desc = methAndDesc[1];
        String[] nameParts = methAndDesc[0].split(PERIOD);
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Method name must contain class and method name!");
        }
        String name = nameParts[nameParts.length - 1];
        Obf owner = BLOBF.getClassMCP(getClassName(nameParts));
        return new BLMethodInfo(owner, name, desc);
    }

    private static String getClassName(String[] parts) {
        if (parts == null || parts.length == 0) {
            return null;
        }
        if (parts.length == 1) {
            return "";
        }
        StringBuilder builder = new StringBuilder(parts.length - 1);

        builder.append(parts[0]);
        for (int index = 1; index < parts.length - 1; index++) {
            builder.append(".");
            builder.append(parts[index]);
        }
        return builder.toString();
    }
}
