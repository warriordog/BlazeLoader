package com.blazeloader.api.direct.server.server;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.world.WorldServer;

public interface WorldEventServerHandler extends BLMod {

    /**
     * Called when WorldServer.tickBlocksAndAmbiance is called.
     *
     * @param server The server calling tickBlocksAndAmbiance
     */
    public void eventTickBlocksAndAmbiance(WorldServer server);

    /**
     * Called when a server-side world is ticked.
     *
     * @param world The world being ticked.
     */
    public void eventTickServerWorld(WorldServer world);
}
