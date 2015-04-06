package com.blazeloader.api.client.world;

import com.blazeloader.api.ApiGeneral;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class ApiWorldClient {

    /**
     * Gets the name of the current world, or connected server if on SMP.  Returns null if no game is active
     *
     * @return the name of the current world.
     */
    public static String getCurrentWorldName() {
        if (ApiGeneral.isClient()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                if (mc.isIntegratedServerRunning()) {
                    return mc.getIntegratedServer().getWorldName(); //Client with SP world
                } else {
                    String name = mc.getCurrentServerData().serverName;
                    if (name == null || name.trim().isEmpty() || "Minecraft Server".equals(name)) {
                        name = mc.getCurrentServerData().serverIP;
                    }
                    return name; //Client on SMP
                }
            } else {
                return null; //Client with no game open
            }
        } else {
            return MinecraftServer.getServer().getWorldName(); //Server with open world
        }
    }
}
