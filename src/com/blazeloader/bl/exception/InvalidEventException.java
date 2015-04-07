package com.blazeloader.bl.exception;

import com.blazeloader.bl.obf.BLMethodInfo;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;

public class InvalidEventException extends RuntimeException {
    public InvalidEventException(String side, BLMethodInfo method, InjectionPoint injectionPoint) {
        super("Exception whilst registering event:\nSide: " + side + "\nMethod: " + (method == null ? "null" : method.toString()) + "\nInjection Point: " + (injectionPoint == null ? "null" : injectionPoint.toString()));
    }
}
