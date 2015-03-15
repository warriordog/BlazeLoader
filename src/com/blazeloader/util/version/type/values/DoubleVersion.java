package com.blazeloader.util.version.type.values;

import com.blazeloader.util.version.BuildType;

public class DoubleVersion extends SingleVersion {
    public DoubleVersion(String id, String name, BuildType buildType, int[] versionParts) {
        super(id, name, buildType, versionParts);
        if (versionParts.length < 2) {
            throw new IllegalArgumentException("DoubleVersion requires at least two versions!");
        }
    }

    public DoubleVersion(String id, String name, BuildType buildType, int version1, int version2) {
        this(id, name, buildType, new int[]{version1, version2});
    }

    public int getVersion2() {
        return getVersionNum(1);
    }
}
