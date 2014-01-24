package net.acomputerdog.BlazeLoader.main;

/**
 * Used by Version.class to return the build type of this build of BlazeLoader
 */
public enum EBuildType {
    /**A stable, release, well tested build*/
    STABLE,
    /**A mostly stable build intended for testing*/
    RELEASE_CANDIDATE,
    /**A beta release.  Usable for modding and playing, but only for experienced users.  Expect some issues.*/
    BETA,
    /**An alpha release.  Usable for modding, but only for experienced users.  Expect many issues.*/
    ALPHA,
    /**A preview release.  Should only be used to see what has changed between versions.  Do not use to mod or play!*/
    PREVIEW,
    /**A build straight from the repo.  Completely in development, may not even compile.*/
    DEV
}
