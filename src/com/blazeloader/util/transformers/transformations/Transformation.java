package com.blazeloader.util.transformers.transformations;

import com.blazeloader.util.transformers.AccessLevel;

import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public abstract class Transformation {
    public final String targetClass;
    public final boolean isGlobal;

    public Transformation(String targetClass, boolean isGlobal) {
        this.targetClass = targetClass;
        this.isGlobal = isGlobal;
    }

    public abstract boolean apply(ClassNode cls);

    protected final int setAccess(int currAccess, AccessLevel publicity) {
        return setAccess(currAccess, publicity, false);
    }

    protected final int setAccess(int currAccess, AccessLevel publicity, boolean isFinal) {
        return setAccess(currAccess, publicity, true, isFinal);
    }


    protected final int setAccess(int currAccess, AccessLevel publicity, boolean setFinal, boolean isFinal) {
        int pubValue = publicity.getValue();
        int ret = (currAccess & ~7);

        switch (currAccess & 7) {
            case ACC_PRIVATE:
                ret |= pubValue;
                break;
            case 0:
                ret |= (pubValue != ACC_PRIVATE ? pubValue : 0);
                break;
            case ACC_PROTECTED:
                ret |= (pubValue != ACC_PRIVATE && pubValue != 0 ? pubValue : ACC_PROTECTED);
                break;
            case ACC_PUBLIC:
                ret |= (pubValue != ACC_PRIVATE && pubValue != 0 && pubValue != ACC_PROTECTED ? pubValue : ACC_PUBLIC);
                break;
            default:
                throw new IllegalArgumentException("Non-existent access mode!");
        }

        if (setFinal) {
            if (isFinal) {
                ret |= ACC_FINAL;
            } else {
                ret &= ~ACC_FINAL;
            }
        }
        return ret;
    }

    public static String getDotName(String slashName) {
        return slashName.replace('/', '.');
    }
}
