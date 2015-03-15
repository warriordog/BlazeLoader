package com.blazeloader.util.transformers.transformations;

import com.blazeloader.util.transformers.AccessLevel;

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
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                if (isGlobal || field.name.equals(fieldName)) {
                    field.access = setAccess(field.access, access);
                    didApply = true;
                }
            }
        }
        return didApply;
    }
}
