package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

/**
 * Server-side player events.
 */
public interface PlayerListener extends BLMod {

    /**
     * Called when a player attempts to log in.  This is after the game has already checked if the user is valid.
     *
     * @param username  The username of the player attempting to join.
     * @param isAllowed The result of the game's user check.  True if the player is allowed to join.
     * @return Return true to allow the player to join, false to prevent it.
     */
    public boolean onPlayerTryLoginMP(String username, boolean isAllowed);


    /**
     * Called when a player logs into the game.
     *
     * @param player The player logging in.
     */
    public void onPlayerLoginMP(ServerConfigurationManager manager, EntityPlayerMP player);

    /**
     * Called when a player logs out of the game.
     *
     * @param player The player logging out.
     */
    public void onPlayerLogoutMP(ServerConfigurationManager manager, EntityPlayerMP player);


    /**
     * Called when a non-local player respawns.  Only works for other players.
     *
     * @param oldPlayer     The player that died.
     * @param dimension     The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void onPlayerRespawnMP(ServerConfigurationManager manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath);
}
