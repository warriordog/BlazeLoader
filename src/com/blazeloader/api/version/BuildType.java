package com.blazeloader.api.version;

/**
 * Represents the build type of a given version
 */
public enum BuildType {
    /**
     * A stable, release, well tested build
     */
    STABLE,
    /**
     * A mostly stable build intended for testing
     */
    RELEASE_CANDIDATE,
    /**
     * A patch build.  Based on stable code by not highly tested.
     */
    PATCH,
    /**
     * A beta release.  Usable but only for experienced users.  Expect some issues.
     */
    BETA,
    /**
     * An alpha release.  Usable but only for experienced users.  Expect many issues.
     */
    ALPHA,
    /**
     * A preview release.  Should only be used to see what has changed between versions.
     */
    PREVIEW,
    /**
     * A build straight from the newest code.  Completely in development, may not even compile or function.
     */
    DEVELOPMENT
}
