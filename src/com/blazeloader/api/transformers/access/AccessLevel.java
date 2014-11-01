package com.blazeloader.api.transformers.access;

import static org.objectweb.asm.Opcodes.*;

public enum AccessLevel {
    PUBLIC(ACC_PUBLIC),
    PRIVATE(ACC_PRIVATE),
    PROTECTED(ACC_PROTECTED),
    PACKAGE(0);

    private final int value;

    AccessLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
