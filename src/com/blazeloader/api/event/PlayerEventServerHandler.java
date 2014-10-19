package com.blazeloader.api.event;

import com.blazeloader.api.mod.BLMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

/**
 * Server-side player events.
 */
public interface PlayerEventServerHandler extends BLMod {

    /**
     * Called when a player attempts to log in.  This is after the game has already checked if the user is valid.
     *
     * @param username  The username of the player attempting to join.
     * @param isAllowed The result of the game's user check.  True if the player is allowed to join.
     * @return Return true to allow the player to join, false to prevent it.
     */
    public boolean eventMPPlayerLoginAttempt(String username, boolean isAllowed);


    /**
     * Called when a player logs into the game.
     *
     * @param player The player logging in.
     */
    public void eventMPPlayerLogin(ServerConfigurationManager manager, EntityPlayerMP player);

    /**
     * Called when a player logs out of the game.
     *
     * @param player The player logging out.
     */
    public void eventMPPlayerLogout(ServerConfigurationManager manager, EntityPlayerMP player);


    /**
     * Called when a non-local player respawns.  Only works for other players.
     *
     * @param oldPlayer     The player that died.
     * @param dimension     The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void eventMPPlayerRespawn(ServerConfigurationManager manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath);
}
