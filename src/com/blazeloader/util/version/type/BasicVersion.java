package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;

/**
 * Class that implements basic functionality of IVersioned.  Automatically adds the version to Version.class
 */
public class BasicVersion extends AbstractVersion {
    private final int[] versionParts;
    private final String versionString;

    public BasicVersion(String id, String name, BuildType buildType, int[] versionParts) {
        super(id, name, buildType);
        if (versionParts == null || versionParts.length == 0) {
            throw new IllegalArgumentException("versionParts cannot be null and must have at least one element!");
        }
        this.versionParts = versionParts;
        this.versionString = createVersionString(id, versionParts);
    }

    /**
     * The number of version categories this version has.  1 for major only; 2 for major and minor; 3 for major, minor, and patch; 4 for major, minor, patch, and additional.
     *
     * @return The number of version categories this version has.
     */
    @Override
    public int getVersionDepth() {
        return versionParts.length;
    }

    /**
     * Gets the version of the specified depth.
     *
     * @param num The depth of the version, must be less than getVersionDepth()
     * @return Return the version value for this depth.
     */
    @Override
    public int getVersionNum(int num) {
        if (num < versionParts.length) {
            return versionParts[num];
        }
        throw new IllegalArgumentException("num must be less than getVersionDepth!");
    }

    /**
     * Gets this version as a String, for example "1.2", "1:2:3", or "1.2.3_4"
     *
     * @return Gets this version as a String.
     */
    @Override
    public String getVersionString() {
        return versionString;
    }

    private static String createVersionString(String id, int[] versionParts) {
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append(".");
        if (versionParts != null && versionParts.length > 0) {
            for (int index = 0; index < versionParts.length; index++) {
                builder.append(index);
                if (index < versionParts.length - 1) {
                    builder.append(".");
                }
            }
        } else {
            builder.append("null");
        }
        return builder.toString();

    }
}
