package com.blazeloader.api.version;

/**
 * Interface for anything with a version.
 */
public interface Versioned {
    /**
     * The number of version categories this version has.  1 for major only; 2 for major and minor; 3 for major, minor, and patch; 4 for major, minor, patch, and additional.
     *
     * @return The number of version categories this version has.
     */
    public int getVersionDepth();

    /**
     * Gets the major version int of this version.
     *
     * @return The major version
     */
    public int getVersion1();

    /**
     * Gets the minor version int of this version.  Can throw an exception if version does not exist.
     *
     * @return The minor version
     */
    public int getVersion2();

    /**
     * Gets the patch version int of this version.  Can throw an exception if version does not exist.
     *
     * @return The patch version
     */
    public int getVersion3();

    /**
     * Gets the additional version int of this version.  Can throw an exception if version does not exist.
     *
     * @return The additional version
     */
    public int getVersion4();

    /**
     * Gets the ID that will be used internally to identify this version.
     *
     * @return The ID that will be used internally to identify this version
     */
    public String getID();

    /**
     * Gets the user-friendly name of this version.
     *
     * @return The user-friendly name of this version.
     */
    public String getFriendlyName();

    /**
     * Gets this version as a String, for example "1.2", "1:2:3", or "1.2.3_4"
     *
     * @return Gets this version as a String.
     */
    public String getVersionString();

    /**
     * Gets the build type of this version.
     *
     * @return The build type of this version.
     */
    public BuildType getBuildType();
}
