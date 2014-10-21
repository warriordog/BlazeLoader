package com.blazeloader.api.client.transformers;

import com.blazeloader.api.main.BLMain;
import com.blazeloader.api.transformers.BLEventInjectionTransformer;

/**
 * Injects events into MC classes
 */
public class BLEventInjectionTransformerClient extends BLEventInjectionTransformer {

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
        } catch (Exception e) {
            e.printStackTrace();
            BLMain.instance().shutdown("A fatal exception occurred while injecting BlazeLoader client events!  BlazeLoader will not be able to run!", -1);
        }
    }

}
