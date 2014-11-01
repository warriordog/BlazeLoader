package com.blazeloader.api.transformers.access.transformation;

import com.blazeloader.api.transformers.access.AccessLevel;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FieldPublicTransformation extends FieldTransformation {
    public final AccessLevel access;

    public FieldPublicTransformation(String targetClass, String fieldName, AccessLevel access) {
        super(targetClass, fieldName);
        this.access = access;
    }

    @Override
    public boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        //System.out.println("Attempting to apply PUBLIC on: " + dotName);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                //System.out.println(field.name);
                if (isGlobal || field.name.equals(fieldName)) {
                    //System.out.println("Applying!");
                    field.access = setAccess(field.access, access);
                    //field.access = setPublic(field.access, access);
                    didApply = true;
                }
            }
        }
        //System.out.println(didApply);
        return didApply;
    }
}
