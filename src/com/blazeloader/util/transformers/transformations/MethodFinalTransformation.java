package com.blazeloader.util.transformers.transformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodFinalTransformation extends MethodTransformation {
    public final boolean finalValue;

    public MethodFinalTransformation(String targetClass, String methodName, boolean setFinalTo) {
        super(targetClass, methodName);
        finalValue = setFinalTo;
    }

    @Override
    public boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (MethodNode method : cls.methods) {
                String mName = method.name.concat(" ").concat(method.desc).replace('/', '.');
                if (isGlobal || mName.equals(methodName)) {
                    didApply = true;
                }
            }
        }
        return didApply;
    }
}
