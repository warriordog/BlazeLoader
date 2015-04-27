package com.blazeloader.util.transformers.transformations;

import com.blazeloader.bl.obf.AccessLevel;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FieldFinalTransformation extends FieldTransformation {
    public final boolean finalValue;

    public FieldFinalTransformation(String targetClass, String fieldName, boolean setFinalTo) {
        super(targetClass, fieldName);
        finalValue = setFinalTo;
    }

    @Override
    public boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                if (isGlobal || field.name.equals(fieldName)) {
                    field.access = setAccess(field.access, AccessLevel.PUBLIC, true, finalValue);
                    didApply = true;
                }
            }
        }
        return didApply;
    }
}
