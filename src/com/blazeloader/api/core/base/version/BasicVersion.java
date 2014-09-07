package com.blazeloader.api.core.base.version;

/**
 * Class that implements basic functionality of IVersioned.  Automatically adds the version to Version.class
 */
public class BasicVersion implements Versioned {
    private final int[] versionParts;
    private final String id;
    private final String name;
    private final BuildType buildType;

    public BasicVersion(int[] versionParts, String id, String name, BuildType buildType) {
        this.versionParts = versionParts;
        this.id = id;
        this.name = name;
        this.buildType = buildType;
        Version.addVersion(this);
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
     * Gets the major version int of this version.
     *
     * @return The major version
     */
    @Override
    public int getVersion1() {
        return versionParts[0];
    }

    /**
     * Gets the minor version int of this version.
     *
     * @return The minor version
     */
    @Override
    public int getVersion2() {
        return versionParts[1];
    }

    /**
     * Gets the patch version int of this version.
     *
     * @return The patch version
     */
    @Override
    public int getVersion3() {
        return versionParts[2];
    }

    /**
     * Gets the additional version int of this version.
     *
     * @return The additional version
     */
    @Override
    public int getVersion4() {
        return versionParts[3];
    }

    /**
     * Gets the ID that will be used internally to identify this version.
     *
     * @return The ID that will be used internally to identify this version
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * Gets the user-friendly name of this version.
     *
     * @return The user-friendly name of this version.
     */
    @Override
    public String getFriendlyName() {
        return name;
    }

    /**
     * Gets this version as a String, for example "1.2", "1:2:3", or "1.2.3_4"
     *
     * @return Gets this version as a String.
     */
    @Override
    public String getVersionString() {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < versionParts.length; index++) {
            builder.append(index);
            if (index < versionParts.length - 1) {
                builder.append(".");
            }
        }
        return builder.toString();
    }

    /**
     * Gets the build type of this version.
     *
     * @return The build type of this version.
     */
    @Override
    public BuildType getBuildType() {
        return buildType;
    }
}
