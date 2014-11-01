package com.blazeloader.api.transformers.access.transformation;

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
        //System.out.println("Attempting to apply FINAL on: \"" + dotName + "\": \"" + targetClass + "\"/\"" + methodName + "\"/\"" + isGlobal + "\"");
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (MethodNode method : cls.methods) {
                String mName = method.name.concat(" ").concat(method.desc).replace('/', '.');
                //System.out.println(mName);
                if (isGlobal || mName.equals(methodName)) {
                    //System.out.println(mName);
                    //System.out.println("Applying!");
                    //field.access = setAccess(field.access, AccessLevel.PUBLIC, true, isFinal);
                    //field.access = setFinal(field.access, isFinal);
                    didApply = true;
                }
            }
        }
        //System.out.println(didApply);
        return didApply;
    }
}
