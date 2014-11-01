package com.blazeloader.api.transformers.access.transformation;

import com.blazeloader.api.transformers.access.AccessLevel;
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
        //System.out.println("Attempting to apply PUBLIC on: \"" + dotName + "\": \"" + targetClass + "\"/\"" + methodName + "\"/\"" + isGlobal + "\"");
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (MethodNode method : cls.methods) {
                String mName = method.name.concat(" ").concat(method.desc).replace('/', '.');
                //System.out.println(mName);
                if (isGlobal || mName.equals(methodName)) {
                    //System.out.println(mName);
                    //System.out.println("Applying!");
                    //field.access = setAccess(field.access, access);
                    //field.access = setPublic(field.access, access);
                    didApply = true;
                }
            }
        }
        //System.out.println(didApply);
        return didApply;
    }
}
