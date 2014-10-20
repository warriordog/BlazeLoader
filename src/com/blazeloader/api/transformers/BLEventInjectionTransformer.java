package com.blazeloader.api.transformers;

import com.blazeloader.api.obf.BLMethodInfo;
import com.blazeloader.api.obf.BLOBF;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Side-independent event injector
 * TODO: split into subclasses
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    protected static final boolean SIDE_CLIENT = true;
    protected static final boolean SIDE_SERVER = false;

    protected static final String EVENT_HANDLER_SERVER = "com.blazeloader.api.event.EventHandler";
    protected static final String EVENT_HANDLER_CLIENT = "com.blazeloader.api.client.event.EventHandlerClient";

    protected static final InjectionPoint methodHead = new MethodHead();

    protected void addBLEvent(boolean isClient, String method) {
        this.addBLEvent(isClient, BLMethodInfo.create(BLOBF.getMethodMCP(method)));
    }

    protected void addBLEvent(boolean isClient, BLMethodInfo method) {
        this.addBLEvent(isClient, method, methodHead);
    }

    protected void addBLEvent(boolean isClient, BLMethodInfo method, InjectionPoint injectionPoint) {
        String name = method.getSimpleName();
        this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(isClient ? EVENT_HANDLER_CLIENT : EVENT_HANDLER_SERVER, "event" + capitaliseFirst(name)));
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

    @Override
    protected void addEvents() {
        try {
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.recreatePlayerEntity (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;");
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader server events!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader events!", e);
        }
    }
}
