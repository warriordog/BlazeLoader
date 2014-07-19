package net.acomputerdog.BlazeLoader.main;

import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.BlazeLoader.mod.BLMod;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.ELogLevel;

/**
 * BL main class
 */
public class BLMain {
    public static final CLogger LOGGER_FULL = new CLogger("BlazeLoader", true, true, ELogLevel.DEBUG);
    public static final CLogger LOGGER_MAIN = new CLogger("BlazeLoader", false, true, ELogLevel.DEBUG);
    public static final CLogger LOGGER_FAST = new CLogger("BlazeLoader", false, false, ELogLevel.DEBUG);

    public static BLMod currActiveMod;

    private static LoaderEnvironment environment;
    private static LoaderProperties properties;

    public static void init(LoaderEnvironment environment, LoaderProperties properties) {
        BLMain.environment = environment;
        BLMain.properties = properties;

        LOGGER_FULL.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_MAIN.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_FAST.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));

        BLMain.LOGGER_FULL.logInfo("BlazeLoader initialized.");
    }
}
