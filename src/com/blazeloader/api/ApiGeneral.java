package com.blazeloader.api;

import java.io.File;
import java.util.ArrayList;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.bl.main.BlazeLoaderCP;

/**
 * General API functions
 */
public class ApiGeneral {
	private static final ArrayList<String> brands = new ArrayList<String>();
	
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
	 * Returns true if a game is currently running. Will always be true on the server.
	 */
	public static boolean isInGame() {
		return BlazeLoaderCP.instance.getGameEngine().isInGame();
	}
	
	/**
	 * Returns true if the current game is a singleplayer one. Is always false on the server.
	 */
	public static boolean isSinglePlayer() {
		return BlazeLoaderCP.instance.getGameEngine().isSinglePlayer();
	}
	
    /**
     * Adds a mod to the client/seerver branding
     *
     * @param brand Brand Name
     */
    public static void setBrand(String brand) {
        if (brand != null && !brand.isEmpty() && !brands.contains(brand)) {
            brands.add(brand);
        }
    }

    /**
     * Gets the formatted client/server brand name
     */
    public static String getBrand() {
    	StringBuilder builder = new StringBuilder();
        builder.append("BlazeLoader");
        if (brands.size() > 0) {
        	builder.append(" (");
	        for (String str : brands) {
	        	if (builder.length() > "BlazeLoader (".length()) { 
	        		builder.append(", ");
	        	}
	            builder.append(str);
	        }
	        builder.append(")");
        }
        return builder.toString();
    }
}
