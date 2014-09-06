package com.blazeloader.api.core.base.main;

import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.core.client.main.BLMainClient;
import com.blazeloader.api.core.server.main.BLMainServer;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.ELogLevel;

/**
 * BL main class
 */
public abstract class BLMain {
    private static BLMain instance;

    public static final CLogger LOGGER_FULL = new CLogger("BlazeLoader", true, true, ELogLevel.DEBUG);
    public static final CLogger LOGGER_MAIN = new CLogger("BlazeLoader", false, true, ELogLevel.DEBUG);
    public static final CLogger LOGGER_FAST = new CLogger("BlazeLoader", false, false, ELogLevel.DEBUG);

    public static BLMod currActiveMod;

    private final LoaderEnvironment environment;
    private final LoaderProperties properties;

    protected BLMain(LoaderEnvironment environment, LoaderProperties properties) {
        if (instance != null) {
            throw new IllegalStateException("BLMain cannot be created twice!");
        }
        instance = this;
        this.environment = environment;
        this.properties = properties;

        LOGGER_FULL.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_MAIN.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_FAST.setMinimumLogLevel(ELogLevel.valueOf(Settings.minimumLogLevel));

        BLMain.LOGGER_FULL.logInfo("BlazeLoader initialized.");
    }

    public abstract void init();

    public abstract boolean supportsServer();

    public abstract boolean supportsClient();

    public abstract BLMainClient getClient();

    public abstract BLMainServer getServer();

    public static BLMain getInstance() {
        return instance;
    }
}
