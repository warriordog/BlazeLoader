package com.blazeloader.api.core.base.version.type.values;

import com.blazeloader.api.core.base.version.BuildType;
import com.blazeloader.api.core.base.version.type.BasicVersion;

public class SingleVersion extends BasicVersion {
    public SingleVersion(String id, String name, BuildType buildType, int[] versionParts) {
        super(id, name, buildType, versionParts);
        if (versionParts.length < 1) {
            throw new IllegalArgumentException("SingleVersion requires at least one version!");
        }
    }

    public SingleVersion(String id, String name, BuildType buildType, int version) {
        this(id, name, buildType, new int[]{version});
    }

    public int getVersion1() {
        return getVersionNum(0);
    }
}
