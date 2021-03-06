package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GlobalTransformationMap extends TransformationMap {
    private final List<Transformation> transformations = new LinkedList<Transformation>();

    @Override
    public List<Transformation> getTransformations(String className) {
        return getTransformations();
    }

    public List<Transformation> getTransformations() {
        return Collections.unmodifiableList(transformations);
    }

    @Override
    public int getNumTransformations(String className) {
        return getNumTransformations();
    }

    public int getNumTransformations() {
        return transformations.size();
    }

    @Override
    public void addTransformation(String className, Transformation transformation) {
        addTransformation(transformation);
    }

    public void addTransformation(Transformation transformation) {
        if (transformation == null) {
            throw new IllegalArgumentException("Cannot have a null transformation!");
        }
        transformations.add(transformation);
    }
}
