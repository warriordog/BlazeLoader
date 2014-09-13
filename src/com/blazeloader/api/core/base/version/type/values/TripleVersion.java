package com.blazeloader.api.core.base.version.type.values;

import com.blazeloader.api.core.base.version.BuildType;

public class TripleVersion extends DoubleVersion {
    public TripleVersion(String id, String name, BuildType buildType, int[] versionParts) {
        super(id, name, buildType, versionParts);
        if (versionParts.length < 3) {
            throw new IllegalArgumentException("TripleVersion requires at least three versions!");
        }
    }

    public TripleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3) {
        this(id, name, buildType, new int[]{version1, version2, version3});
    }

    public int getVersion3() {
        return getVersionNum(2);
    }
}
