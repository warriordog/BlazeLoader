package com.blazeloader.api.direct.base.transformers;

import com.blazeloader.api.direct.base.obf.BLMethodInfo;
import com.blazeloader.api.direct.base.obf.BLOBF;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Side-independent event injector
 * TODO: split into subclasses
 */
public abstract class BLBaseEventInjectionTransformer extends EventInjectionTransformer {

    protected static final int SIDE_BASE = 0;
    protected static final int SIDE_CLIENT = 1;
    protected static final int SIDE_SERVER = 2;

    protected static final String EVENT_HANDLER_BASE = "com.blazeloader.api.direct.base.event.EventHandlerBase";
    protected static final String EVENT_HANDLER_CLIENT = "com.blazeloader.api.direct.client.event.EventHandlerClient";
    protected static final String EVENT_HANDLER_SERVER = "com.blazeloader.api.direct.server.event.EventHandlerServer";

    protected static final InjectionPoint methodHead = new MethodHead();

    protected void addBLEvent(int side, String method) {
        this.addBLEvent(side, BLMethodInfo.create(BLOBF.getMethodMCP(method)));
    }

    protected void addBLEvent(int side, BLMethodInfo method) {
        this.addBLEvent(side, method, methodHead);
    }

    protected void addBLEvent(int side, BLMethodInfo method, InjectionPoint injectionPoint) {
        String name = method.getSimpleName();
        switch (side) {
            case SIDE_BASE:
                this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_BASE, "event" + capitaliseFirst(name)));
                break;
            case SIDE_CLIENT:
                this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_CLIENT, "event" + capitaliseFirst(name)));
                break;
            case SIDE_SERVER:
                this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_SERVER, "event" + capitaliseFirst(name)));
                break;
            default:
                throw new IllegalArgumentException("Illegal side: " + side + "!  Side must be 0, 1, or 2!");
        }
    }

    protected static String capitaliseFirst(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return str;
        }
        String firstChar = String.valueOf(str.charAt(0)).toUpperCase();
        return firstChar.concat(str.substring(1, str.length()));
    }
}
