package com.blazeloader.api;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.bl.main.BlazeLoaderCP;
import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.util.version.Versions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * General API functions
 */
public class ApiGeneral {
    private static final Map<String, Brand> brands = new HashMap<String, Brand>();
    
    /**
     * Location of Minecraft's working directory.
     * <br><br>%APPDATA%/.minecraft/ for windows.
     */
    public static final File mainDir = new File("./");

    /**
     * Shuts down the game with a specified error code.
     * <br><br>Use <b>0</b> for normal shutdown.
     *
     * @param code The error code to return to the system after shutdown.
     */
    public static void shutdown(String message, int code) {
        BLMain.instance().shutdown(message, code);
    }


    /**
     * Adds a mod to the client/server branding
     *
     * @param brand Brand Name
     */
    public static Brand setBrand(String brand) {
    	return setBrand(brand, false);
    }
    
    /**
     * Adds a mod to the client/server branding
     *
     * @param brand			Brand Name
     * @param technical		True if this brand must no be shown in the client/server's brand
     */
    public static Brand setBrand(String brand, boolean technical) {
    	Brand item = null;
    	if (brands.containsKey(brand)) {
    		if (!technical) {
    			item = brands.get(brand);
    			if (item.isTechnical()) {
    				item.tech = false;
    			}
    		}
        } else {
        	item = new Brand(brand, technical);
        	brands.put(brand, item);
        }
    	return item;
    }
    
    /**
     * Checks if a brand exists for the given mod.
     * 
     * @param brand		The name of the brand
     * 
     * @return A brand object representing the given brand name if it exists
     */
    public static Brand hasBrand(String brand) {
    	if (brands.containsKey(brand)) {
    		return brands.get(brand);
    	}
    	return null;
    }
    
    /**
     * Gets the formatted client/server brand name
     */
    public static String getBrand() {
        StringBuilder builder = new StringBuilder();
        builder.append("BlazeLoader");
        if (brands.size() > 0) {
            builder.append(" (");
            for (Brand brand : brands.values()) {
            	if (!brand.isTechnical()) {
	                if (builder.length() > "BlazeLoader (".length()) {
	                    builder.append(", ");
	                }
	                builder.append(brand.getString());
            	}
            }
            builder.append(")");
        }
        return builder.toString();
    }

    /**
     * Checks if the game is a client instance
     *
     * @return true if the game is a client
     */
    public static boolean isClient() {
        return BLMain.isClient;
    }

    /**
     * Checks if the game is a dedicated server instance (an actual server with no client, not an integrated server).  Will return false on client.
     *
     * @return true if the game is a dedicated server
     */
    public static boolean isServer() {
        return !BLMain.isClient;
    }
    
    /**
     * Checks if the current world is a singleplayer one. Always returns false on the server and will return false in lan games.
     * 
     * @return true if the game is in singleplayer
     */
    public static boolean isSinglePlayer() {
    	return BlazeLoaderCP.instance.getGameEngine().isSinglePlayer();
    }

    /**
     * Returns true if a game is currently running. Will always be true on the server.
     */
    public static boolean isInGame() {
        return BlazeLoaderCP.instance.getGameEngine().isInGame();
    }
}
