package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.ServerConfigurationManager;

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
    private static final String __OBFID = "CL_00001128";

    public IntegratedPlayerList(IntegratedServer par1IntegratedServer)
    {
        super(par1IntegratedServer);
        this.viewDistance = 10;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP.getCommandSenderName().equals(this.getServerInstance().getServerOwner()))
        {
            this.hostPlayerData = new NBTTagCompound();
            par1EntityPlayerMP.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(par1EntityPlayerMP);
    }

    /**
     * previously allowUserToConnect
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String func_148542_a(SocketAddress p_148542_1_, GameProfile p_148542_2_)
    {
        return p_148542_2_.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerForUsername(p_148542_2_.getName()) != null ? "That name is already taken." : super.func_148542_a(p_148542_1_, p_148542_2_);
    }

    /**
     * get the associated Integrated Server
     */
    public IntegratedServer getServerInstance()
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

    /**
     * Determine if the player is allowed to connect based on current server settings.
     */
    @Override
    public boolean isAllowedToLogin(String username) {
        return ModList.eventPlayerLoginAttempt(username, super.isAllowedToLogin(username));
    }
}
