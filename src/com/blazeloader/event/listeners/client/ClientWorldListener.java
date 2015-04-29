package com.blazeloader.event.listeners.client;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

/**
 * Interface for mods that handle world events
 */
public interface ClientWorldListener extends BLMod {
    /**
     * Called when a world is loaded.
     *
     * @param world   The world being loaded.
     * @param message The message displayed to the user on the loading screen.
     */
    public void onWorldLoad(Minecraft minecraft, WorldClient world, String message);

    /**
     * Called when a world is unloaded.
     *
     * @param world   The world being unloaded.
     * @param message The message displayed to the user on the loading screen.
     */
    public void onWorldUnload(Minecraft minecraft, WorldClient world, String message);
    
    /**
     * Called when the client's world changes
     * @param world		The new value for Minecraft.worldObj, may be null
     */
    public void onWorldChanged(World world);
}
