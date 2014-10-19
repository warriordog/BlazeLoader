package com.blazeloader.api.version.type.values;

import com.blazeloader.api.version.BuildType;

public class QuadrupleVersion extends TripleVersion {
    public QuadrupleVersion(String id, String name, BuildType buildType, int[] versionParts) {
        super(id, name, buildType, versionParts);
        if (versionParts.length < 4) {
            throw new IllegalArgumentException("QuadrupleVersion requires at least four versions!");
        }
    }

    public QuadrupleVersion(String id, String name, BuildType buildType, int version1, int version2, int version3, int version4) {
        this(id, name, buildType, new int[]{version1, version2, version3, version4});
    }

    public int getVersion4() {
        return getVersionNum(3);
    }
}
