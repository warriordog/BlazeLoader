package com.blazeloader.api.direct.event;

import com.blazeloader.api.core.mod.BLMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Interface for mods that handle world events
 */
public interface WorldEventHandler extends BLMod {
    /**
     * Called when a world is loaded.
     *
     * @param world   The world being loaded.
     * @param message The message displayed to the user on the loading screen.
     */
    public void eventLoadWorld(Minecraft minecraft, WorldClient world, String message);

    /**
     * Called when a world is unloaded.
     *
     * @param world   The world being unloaded.
     * @param message The message displayed to the user on the loading screen.
     */
    public void eventUnloadWorld(Minecraft minecraft, WorldClient world, String message);

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

    /**
     * Called when a world if changed.  (place/remove block)
     *
     * @param world The world being changed.
     */
    public void eventWorldChanged(World world);
}
