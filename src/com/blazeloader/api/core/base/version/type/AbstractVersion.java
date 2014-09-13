package com.blazeloader.api.core.base.version.type;

import com.blazeloader.api.core.base.version.BuildType;
import com.blazeloader.api.core.base.version.Versions;

/**
 * Basic, abstract implementation of non-version parts of Version.
 * Automatically registers itself with Versions.
 */
public abstract class AbstractVersion implements Version {
    private final String id;
    private final String name;
    private final BuildType buildType;

    public AbstractVersion(String id, String name, BuildType buildType) {
        this.id = id;
        this.name = name;
        this.buildType = buildType;
        Versions.addVersion(this);
    }

    public AbstractVersion(String id, BuildType buildType) {
        this(id, id, buildType);
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
     * Gets the build type of this version.
     *
     * @return The build type of this version.
     */
    @Override
    public BuildType getBuildType() {
        return buildType;
    }
}
