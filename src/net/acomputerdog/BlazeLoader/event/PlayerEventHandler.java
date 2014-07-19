package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;

/**
 * Interface for mods that handle player events
 */
public interface PlayerEventHandler extends BLMod {

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
    public void eventPlayerLogin(EntityPlayerMP player);

    /**
     * Called when a player logs out of the game.
     *
     * @param player The player logging out.
     */
    public void eventMPPlayerLogout(EntityPlayerMP player);


    /**
     * Called when a non-local player respawns.  Only works for other players.
     *
     * @param oldPlayer     The player that died.
     * @param newPlayer     The player being spawned.
     * @param dimension     The dimension to spawn in.
     * @param causedByDeath If the respawn was triggered by death, vs beating the game.
     */
    public void eventMPPlayerRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, int dimension, boolean causedByDeath);

    /**
     * Called when the client player dies.
     */
    public void eventClientPlayerDeath();

    /**
     * Called when the client connects to a server or singleplayer game
     *
     * @param netHandler  The network handler processing loginPacket
     * @param loginPacket The login packet for this login
     */
    public void eventClientJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket);
}
