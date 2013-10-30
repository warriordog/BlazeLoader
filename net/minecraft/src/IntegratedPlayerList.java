package net.minecraft.src;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;
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

    public IntegratedPlayerList(IntegratedServer par1IntegratedServer)
    {
        super(par1IntegratedServer);
        for(Field f : ServerConfigurationManager.class.getDeclaredFields()){
            if(MinecraftServer.class.isAssignableFrom(f.getType())){
                try{
                    f.setAccessible(true);
                    f.set(this, par1IntegratedServer);
                }catch(Exception e){
                    throw new RuntimeException("Could not replace mcServer!", e);
                }
            }
        }
        this.viewDistance = 10;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP.getCommandSenderName().equals(this.getIntegratedServer().getServerOwner()))
        {
            this.hostPlayerData = new NBTTagCompound();
            par1EntityPlayerMP.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(par1EntityPlayerMP);
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress par1SocketAddress, String par2Str)
    {
        return par2Str.equalsIgnoreCase(this.getIntegratedServer().getServerOwner()) ? "That name is already taken." : super.allowUserToConnect(par1SocketAddress, par2Str);
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
    public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3) {
        EntityPlayerMP player = super.respawnPlayer(par1EntityPlayerMP, par2, par3);
        ModList.eventPlayerSpawn(par1EntityPlayerMP, player, par2, par3);
        return player;
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    @Override
    public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP) {
        super.playerLoggedOut(par1EntityPlayerMP);
        ModList.eventPlayerLogout(par1EntityPlayerMP);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    @Override
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP) {
        super.playerLoggedIn(par1EntityPlayerMP);
        ModList.eventPlayerLogin(par1EntityPlayerMP);
    }
}
