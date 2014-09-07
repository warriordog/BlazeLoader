package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

/**
 * Interface for mods that handle world events
 */
public interface WorldEventClientHandler extends BLMod {
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
     * Called when a world if changed.  (place/remove block)
     *
     * @param world The world being changed.
     */
    public void eventWorldChanged(World world);
}
