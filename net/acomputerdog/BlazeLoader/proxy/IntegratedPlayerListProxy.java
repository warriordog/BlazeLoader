package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IntegratedPlayerList;
import net.minecraft.src.IntegratedServer;

/**
 * Proxy class for IntegratedPlayerList.
 */
public class IntegratedPlayerListProxy extends IntegratedPlayerList {

    public IntegratedPlayerListProxy(IntegratedServer par1IntegratedServer) {
        super(par1IntegratedServer);
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    @Override
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP) {
        super.playerLoggedIn(par1EntityPlayerMP);
        ModList.eventPlayerLogin(par1EntityPlayerMP);
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
}
