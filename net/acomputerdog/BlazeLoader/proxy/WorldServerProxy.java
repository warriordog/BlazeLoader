package net.acomputerdog.BlazeLoader.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class WorldServerProxy extends WorldServer {

    public WorldServerProxy(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7ILogAgent) {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7ILogAgent);
    }
}
