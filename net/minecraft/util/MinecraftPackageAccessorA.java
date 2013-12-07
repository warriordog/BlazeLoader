package net.minecraft.util;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;

/**
 * Class that allows access to the various Package Private fields in Minecraft's code.
 */
public class MinecraftPackageAccessorA {

    public static void setTPS(float tps){
        ApiBase.theMinecraft.timer.ticksPerSecond = tps;
    }

    public static float getTPS(){
        return ApiBase.theMinecraft.timer.ticksPerSecond;
    }
}
