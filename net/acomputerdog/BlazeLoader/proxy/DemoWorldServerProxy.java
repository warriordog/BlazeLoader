package net.acomputerdog.BlazeLoader.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.DemoWorldServer;
import net.minecraft.src.ILogAgent;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.Profiler;

@Deprecated
/**
 * Proxy class for DemoWorldServer.  Does not do anything but needs to exist so demo worlds use BL.
 */
public class DemoWorldServerProxy extends WorldServerProxy {
    public DemoWorldServerProxy(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, Profiler par6Profiler, ILogAgent par7ILogAgent) {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, DemoWorldServer.demoWorldSettings, par6Profiler, par7ILogAgent);
    }
}
