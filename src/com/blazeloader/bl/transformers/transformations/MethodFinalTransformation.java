package com.blazeloader.bl.transformers.transformations;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodFinalTransformation extends MethodTransformation {
    public final boolean isFinal;

    public MethodFinalTransformation(String targetClass, String methodName, boolean isFinal) {
        super(targetClass, methodName);
        this.isFinal = isFinal;
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
