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
        //System.out.println("Attempting to apply FINAL on: " + dotName + ": " + targetClass + "/" + fieldName + "/" + isGlobal);
        boolean didApply = false;
        if (dotName.equals(targetClass)) {
            for (FieldNode field : cls.fields) {
                //System.out.println(field.name);
                if (isGlobal || field.name.equals(fieldName)) {
                    //System.out.println("Applying!");
                    field.access = setAccess(field.access, AccessLevel.PUBLIC, true, isFinal);
                    //field.access = setFinal(field.access, isFinal);
                    didApply = true;
                }
            }
        }
        //System.out.println(didApply);
        return didApply;
    }
}
