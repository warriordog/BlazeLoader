package com.blazeloader.api.direct.client.transformers;

import com.blazeloader.api.direct.base.transformers.BLBaseEventInjectionTransformer;

/**
 * Injects events into MC classes
 */
public class BLClientEventInjectionTransformer extends BLBaseEventInjectionTransformer {

    /**
     * Subclasses should register events here
     */
    @Override
    protected void addEvents() {
        try {
            addBLEvent(SIDE_CLIENT, "net.minecraft.client.Minecraft.loadWorld (Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V");
            addBLEvent(SIDE_CLIENT, "net.minecraft.profiler.Profiler.startSection (Ljava/lang/String;)V");
            addBLEvent(SIDE_CLIENT, "net.minecraft.profiler.Profiler.endSection ()V");
            addBLEvent(SIDE_CLIENT, "net.minecraft.client.Minecraft.displayGuiScreen (Lnet/minecraft/client/gui/GuiScreen;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedIn (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.playerLoggedOut (Lnet/minecraft/entity/player/EntityPlayerMP;)V");
            addBLEvent(SIDE_SERVER, "net.minecraft.server.management.ServerConfigurationManager.recreatePlayerEntity (Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;");
        } catch (Exception e) {
            System.err.println("A fatal exception occurred while injecting BlazeLoader!  BlazeLoader will not be able to run!");
            throw new RuntimeException("Exception injecting BlazeLoader events!", e);
        }
    }

}
