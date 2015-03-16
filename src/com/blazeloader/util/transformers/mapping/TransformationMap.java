package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.List;

public abstract class TransformationMap {

    public abstract List<Transformation> getTransformations(String className);

    public abstract int getNumTransformations(String className);

    public abstract void addTransformation(String className, Transformation transformation);

}
