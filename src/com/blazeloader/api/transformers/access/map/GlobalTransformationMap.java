package com.blazeloader.api.transformers.access.map;

import com.blazeloader.api.transformers.access.transformation.Transformation;

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
