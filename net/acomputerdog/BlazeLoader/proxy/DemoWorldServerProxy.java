package net.acomputerdog.BlazeLoader.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class DemoWorldServerProxy extends WorldServerProxy {
    public DemoWorldServerProxy(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, Profiler par6Profiler, ILogAgent par7ILogAgent) {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, DemoWorldServer.demoWorldSettings, par6Profiler, par7ILogAgent);
    }
}
