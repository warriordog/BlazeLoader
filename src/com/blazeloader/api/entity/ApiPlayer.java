package com.blazeloader.api.entity;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.util.version.Versions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class ApiPlayer {

    /**
     * Gets the player Entity corresponding to the current user.
     *
     * @return Minecraft.thePlayer if on a client, otherwise returns the server owner.
     */
    public static EntityPlayer getPlayer() {
        if (Versions.isClient()) {
            return ApiClient.getPlayer();
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            String owner = server.getServerOwner();
            for (WorldServer i : server.worldServers) {
                EntityPlayer result = i.getPlayerEntityByName(owner);
                if (result != null) return result;
            }
        }
        return null;
    }

    /**
     * Returns true if the player has been opped or this is running in a singleplayer world.
     */
    public static boolean playerHasOPAbilities(EntityPlayer player) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            return server.getConfigurationManager().canSendCommands(player.getGameProfile());
        }
        return true;
    }

    /**
     * Gets the IP for the current player.
     */
    public static String getPlayerIP(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            return ((EntityPlayerMP) player).getPlayerIP();
        }
        return "LOCALHOST";
    }

    /**
     * Gets an array of all players currently present in the game.
     */
    public static EntityPlayer[] getAllPlayers() {
        List<EntityPlayer> result = new ArrayList<EntityPlayer>();
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            result.addAll(server.getConfigurationManager().playerEntityList);
        }
        return result.toArray(new EntityPlayer[result.size()]);
    }

    public static StatisticsFile getPlayerStatsFile(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            return ((EntityPlayerMP) player).getStatFile();
        }
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            return server.getConfigurationManager().getPlayerStatsFile(player);
        }
        return null;
    }
}
