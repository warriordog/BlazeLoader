package com.blazeloader.util.transformers.transformations;

public abstract class FieldTransformation extends Transformation {
    public final String fieldName;

    public FieldTransformation(String targetClass, String fieldName) {
        super(targetClass, "*".equals(fieldName));
        this.fieldName = fieldName;
    }

}
