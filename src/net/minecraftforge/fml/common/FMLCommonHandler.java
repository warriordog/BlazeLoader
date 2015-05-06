package net.minecraftforge.fml.common;

import com.blazeloader.bl.main.BLMain;

/**
 * Dummy class is dummy class. :D
 */
public class FMLCommonHandler {
	
	public static FMLCommonHandler instance() {
		BLMain.LOGGER_FULL.logInfo("A wild INSTANCE has appeared!");
		return new FMLCommonHandler();
	}
	
	public void exitJava(int exitCode, boolean hardExit) {
		BLMain.LOGGER_FULL.logInfo("Exit Java?");
		System.exit(exitCode);
	}
}
