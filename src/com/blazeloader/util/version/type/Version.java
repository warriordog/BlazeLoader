package com.blazeloader.util.version.type;

import com.blazeloader.util.version.BuildType;

/**
 * Interface for anything with a version.
 */
public interface Version {
    /**
     * The number of version categories this version has.  1 for major only; 2 for major and minor; 3 for major, minor, and patch; 4 for major, minor, patch, and additional.
     *
     * @return The number of version categories this version has.
     */
    public int getVersionDepth();

    /**
     * Gets the version of the specified depth.
     *
     * @param num The depth of the version, must be less than getVersionDepth()
     * @return Return the version value for this depth.
     */
    public int getVersionNum(int num);

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
