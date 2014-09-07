package com.blazeloader.api.direct.client.transformers;

import com.blazeloader.api.direct.base.transformers.BLBaseEventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Injects events into MC classes
 */
public class BLClientEventInjectionTransformer extends BLBaseEventInjectionTransformer {
    private static final String EVENT_HANDLER = "com.blazeloader.api.direct.client.event.EventHandlerClient";
    private static final InjectionPoint methodHead = new MethodHead();

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        try {
            addBLEvent("net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V");
            addBLEvent("net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V");
            addBLEvent("net.minecraft.profiler.Profiler.endSection ()V");
            addBLEvent("net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
            addBLEvent("net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent("net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent("net.minecraft.server.management.ServerConfigurationManager.respawnPlayer (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;");
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader!", e);
        }
    }

    private void addBLEvent(String method) {
        this.addBLEvent(BLMethodInfo.create(BLOBF.getMethodMCP(method)));
    }

    private void addBLEvent(BLMethodInfo method) {
        this.addBLEvent(method, methodHead);
    }

    private void addBLEvent(BLMethodInfo method, InjectionPoint injectionPoint) {
        String name = method.getSimpleName();
        this.addEvent(Event.getOrCreate("BL." + name, false), method, injectionPoint).addListener(new MethodInfo(EVENT_HANDLER, "event" + capitaliseFirst(name)));
    }

    private static String capitaliseFirst(String str) {
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
