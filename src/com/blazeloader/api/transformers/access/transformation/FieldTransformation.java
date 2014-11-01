package com.blazeloader.api.transformers.access.transformation;

public abstract class FieldTransformation extends Transformation {
    public final String fieldName;

    public FieldTransformation(String targetClass, String fieldName) {
        super(targetClass, "*".equals(fieldName));
        this.fieldName = fieldName;
    }

}
