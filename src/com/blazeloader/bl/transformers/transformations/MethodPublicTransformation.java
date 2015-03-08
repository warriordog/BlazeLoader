package com.blazeloader.bl.transformers.transformations;

import com.blazeloader.bl.transformers.AccessLevel;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodPublicTransformation extends MethodTransformation {
    public final AccessLevel access;

    public MethodPublicTransformation(String targetClass, String methodName, AccessLevel access) {
        super(targetClass, methodName);
        this.access = access;
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
