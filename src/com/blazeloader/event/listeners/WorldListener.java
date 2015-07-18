package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.world.WorldServer;

/**
 * Server-side world events
 */
public interface WorldListener extends BLMod {

    /**
     * Called when WorldServer.tickBlocksAndAmbiance is called.
     *
     * @param server The server calling tickBlocksAndAmbiance
     */
    public void onBlocksAndAmbianceTicked(WorldServer server);

    /**
     * Called when a server-side world is ticked.
     *
     * @param world The world being ticked.
     */
    public void onServerTick(WorldServer world);
}
