package com.blazeloader.api.transformers.access.transformation;

import com.blazeloader.api.transformers.access.AccessLevel;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FieldFinalTransformation extends FieldTransformation {
    public final boolean isFinal;

    public FieldFinalTransformation(String targetClass, String fieldName, boolean isFinal) {
        super(targetClass, fieldName);
        this.isFinal = isFinal;
    }

    @Override
    public boolean apply(ClassNode cls) {
        String dotName = getDotName(cls.name);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                if (isGlobal || field.name.equals(fieldName)) {
                    field.access = setAccess(field.access, AccessLevel.PUBLIC, true, isFinal);
                    didApply = true;
                }
            }
        }
        return didApply;
    }
}
