package com.blazeloader.api.transformers;

import com.blazeloader.api.main.BLMain;
import com.blazeloader.api.obf.BLMethodInfo;
import com.blazeloader.api.obf.BLOBF;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.BeforeReturn;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Side-independent event injector
 * TODO: Simplify side determining code
 */
public class BLEventInjectionTransformer extends EventInjectionTransformer {
    protected static final int SIDE_CLIENT = 0;
    protected static final int SIDE_SERVER = 1;
    protected static final int SIDE_INTERNAL = 2;

    protected static final String EVENT_HANDLER_SERVER = "com.blazeloader.api.event.EventHandler";
    protected static final String EVENT_HANDLER_CLIENT = "com.blazeloader.api.client.event.EventHandlerClient";
    protected static final String EVENT_HANDLER_INTERNAL = "com.blazeloader.api.main.InternalEventHandler";

    protected static final InjectionPoint methodHead = new MethodHead();
    protected static final InjectionPoint beforeReturn = new BeforeReturn();

    protected void addBLEvent(int side, String method, InjectionPoint injectionPoint) {
        this.addBLEvent(side, BLMethodInfo.create(BLOBF.getMethodMCP(method)), injectionPoint);
    }

    protected void addBLEvent(int side, String method) {
        this.addBLEvent(side, BLMethodInfo.create(BLOBF.getMethodMCP(method)));
    }

    protected void addBLEvent(int side, BLMethodInfo method) {
        this.addBLEvent(side, method, methodHead);
    }

    protected void addBLEvent(int side, BLMethodInfo method, InjectionPoint injectionPoint) {
        String name = method.getSimpleName();
        switch (side) {
            case SIDE_CLIENT:
                this.addEvent(Event.getOrCreate("BL." + name, true), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_CLIENT, "event" + capitaliseFirst(name)));
                break;
            case SIDE_SERVER:
                this.addEvent(Event.getOrCreate("BL." + name, true), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_SERVER, "event" + capitaliseFirst(name)));
                break;
            case SIDE_INTERNAL:
                this.addEvent(Event.getOrCreate("BL." + name, true), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER_INTERNAL, "event" + capitaliseFirst(name)));
                break;
            default:
                throw new IllegalArgumentException("Invalid side: " + side);
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

    @Override
    protected void addEvents() {
        try {
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.recreatePlayerEntity (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;");
            addBLEvent(SIDE_INTERNAL, "net.minecraft.server.MinecraftServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
            addBLEvent(SIDE_INTERNAL, "net.minecraft.server.integrated.IntegratedServer.createNewCommandManager ()Lnet/minecraft/command/ServerCommandManager;", beforeReturn);
        } catch (Exception e) {
            e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader server events!  BlazeLoader will not be able to run!", -1);
        }
    }
}
