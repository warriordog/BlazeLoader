package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.CrashReport;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Session;

import java.io.File;
import java.net.Proxy;

public class MinecraftProxy extends Minecraft {
    public MinecraftProxy(Session par1Session, int par2, int par3, boolean par4, boolean par5, File par6File, File par7File, File par8File, Proxy par9Proxy, String par10Str) {
        super(par1Session, par2, par3, par4, par5, par6File, par7File, par8File, par9Proxy, par10Str);
    }

    @Override
    public void run() {
        ModList.startAllMods();
        super.run();
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    @Override
    public void shutdown() {
        ModList.stopAllMods();
        super.shutdown();
    }

    /**
     * Runs the current tick.
     */
    @Override
    public void runTick() {
        //ModList.tickAllMods(true);
        super.runTick();
        ModList.tickAllMods(false);
    }
}
