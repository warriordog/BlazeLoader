package com.blazeloader.api.transformers.access.transformation;

public abstract class MethodTransformation extends Transformation {
    public final String methodName;

    public MethodTransformation(String targetClass, String methodName) {
        super(targetClass, "*".equals(methodName));
        this.methodName = methodName;
    }

}
