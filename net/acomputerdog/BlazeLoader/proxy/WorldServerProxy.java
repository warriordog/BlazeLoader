package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

@Deprecated
/**
 * A proxy class for WorldServer.  Gives access to Mod.eventTickServerWorld().
 */
public class WorldServerProxy extends WorldServer {

    public WorldServerProxy(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7ILogAgent) {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7ILogAgent);
    }

    /**
     * Runs a single tick for the world
     */
    @Override
    public void tick() {
        super.tick();
        ModList.eventTickServerWorld(this);
    }
}
