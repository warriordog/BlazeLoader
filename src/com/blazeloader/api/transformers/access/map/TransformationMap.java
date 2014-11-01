package com.blazeloader.api.transformers.access.map;

import com.blazeloader.api.transformers.access.transformation.Transformation;

import java.util.List;

public abstract class TransformationMap {

    public abstract List<Transformation> getTransformations(String className);

    public abstract int getNumTransformations(String className);

    public abstract void addTransformation(String className, Transformation transformation);

}
