package com.blazeloader.util.transformers.mapping;

import com.blazeloader.util.transformers.transformations.Transformation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultTransformationMap extends TransformationMap {
    private final Map<String, List<Transformation>> transformations = new HashMap<String, List<Transformation>>();

    public List<Transformation> getTransformations(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Class name must not be null!");
        }
        List<Transformation> trans = transformations.get(className);
        if (trans == null) {
            trans = new LinkedList<Transformation>();
            transformations.put(className, trans);
        }
        return trans;
    }

    public int getNumTransformations(String className) {
        return getTransformations(className).size();
    }

    public void addTransformation(String className, Transformation transformation) {
        if (className == null || transformation == null) {
            throw new IllegalArgumentException("Class name and transformation must not be null!");
        }
        getTransformations(className).add(transformation);
    }
}
