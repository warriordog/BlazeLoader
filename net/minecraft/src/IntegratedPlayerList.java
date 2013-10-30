package net.minecraft.src;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.server.MinecraftServer;

import java.net.SocketAddress;

/**
 * Manages players on the IntegratedServer.  Replaces IntegratedServerProxy.
 */
public class IntegratedPlayerList extends ServerConfigurationManager
{
    /**
     * Holds the NBT data for the host player's save file, so this can be written to level.dat.
     */
    private NBTTagCompound hostPlayerData;

    public IntegratedPlayerList(IntegratedServer server)
    {
        super(server);
        this.viewDistance = 10;
    }

    /**
     * also stores the NBTTags if this is an integratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP player)
    {
        if (player.getCommandSenderName().equals(this.getIntegratedServer().getServerOwner()))
        {
            this.hostPlayerData = new NBTTagCompound();
            player.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(player);
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress socket, String playerName)
    {
        return playerName.equalsIgnoreCase(this.getIntegratedServer().getServerOwner()) ? "That name is already taken." : super.allowUserToConnect(socket, playerName);
    }

    /**
     * get the associated Integrated Server
     */
    public IntegratedServer getIntegratedServer()
    {
        return (IntegratedServer)super.getServerInstance();
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return this.hostPlayerData;
    }

    public MinecraftServer getServerInstance()
    {
        return this.getIntegratedServer();
    }
    /**
     * creates and returns a respawned player based on the provided PlayerEntity. Args are the PlayerEntityMP to
     * respawn, an INT for the dimension to respawn into (usually 0), and a boolean value that is true if the player
     * beat the game rather than dying
     */
    @Override
    public EntityPlayerMP respawnPlayer(EntityPlayerMP player, int dimension, boolean didWin) {
        EntityPlayerMP newPlayer = super.respawnPlayer(player, dimension, didWin);
        ModList.eventPlayerSpawn(player, newPlayer, dimension, didWin);
        return newPlayer;
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    @Override
    public void playerLoggedOut(EntityPlayerMP player) {
        super.playerLoggedOut(player);
        ModList.eventPlayerLogout(player);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    @Override
    public void playerLoggedIn(EntityPlayerMP player) {
        super.playerLoggedIn(player);
        ModList.eventPlayerLogin(player);
    }
}
