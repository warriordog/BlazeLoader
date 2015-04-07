package com.blazeloader.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;

import com.blazeloader.bl.main.BlazeLoaderCP;

public class ApiClient {
	/**
	 * Client version of ApiPlayer.getPlayer. Will always return Minecraft.thePlayer
	 * 
	 * @return the client's player
	 */
    public static EntityPlayerSP getPlayer() {
        return getClient().thePlayer;
    }
    
    public static Minecraft getClient() {
    	return (Minecraft)BlazeLoaderCP.instance.getGameEngine().getClient();
    }
    
    public static MinecraftServer getServer() {
    	return (MinecraftServer)BlazeLoaderCP.instance.getGameEngine().getServer();
    }
}
