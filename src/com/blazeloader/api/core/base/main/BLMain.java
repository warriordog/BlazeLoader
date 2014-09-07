package com.blazeloader.api.core.base.main;

import com.blazeloader.api.core.base.mod.BLMod;
import com.blazeloader.api.core.client.main.BLMainClient;
import com.blazeloader.api.core.server.main.BLMainServer;
import com.blazeloader.api.direct.client.event.BlazeLoaderIPClient;
import com.blazeloader.api.direct.server.api.command.BLCommandHandler;
import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.ELogLevel;

import java.util.Arrays;
import java.util.List;

/**
 * BL main class
 */
public abstract class BLMain {
    private static BLMain instance;

    /**
     * Logger that logs date and time
     */
    public static final CLogger LOGGER_FULL = new CLogger("BlazeLoader", true, true, ELogLevel.DEBUG);
    /**
     * Logger that logs time but not date
     */
    public static final CLogger LOGGER_MAIN = new CLogger("BlazeLoader", false, true, ELogLevel.DEBUG);
    /**
     * Logger that does not log date or time
     */
    public static final CLogger LOGGER_FAST = new CLogger("BlazeLoader", false, false, ELogLevel.DEBUG);

    public static BLMod currActiveMod; //if an event is in progress, this is a reference to the mod that is currently handling the event.
    public static boolean isInTick = false; //true if a game tick is in progress
    public static int numTicks = 0; //number of ticks that the game has been running

    /**
     * Command handler for mods to register commands with.  Will always exist, although if server does not exist this will not be used.
     */
    public static final BLCommandHandler commandHandler = new BLCommandHandler();

    public final LoaderEnvironment environment;
    public final LoaderProperties properties;

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

    public String[] getRequiredTransformers() {
        return new String[]{"com.blazeloader.api.direct.base.transformers.BLAccessTransformer", "com.blazeloader.api.direct.client.transformers.BLClientEventInjectionTransformer"};
    }

    public String[] getRequiredDownstreamTransformers() {
        return null;
    }

    public String[] getPacketTransformers() {
        return null;
    }

    public List<EnumeratorModule> getEnumeratorModules() {
        return null;
    }

    public List<CoreProvider> getCoreProviders() {
        return Arrays.asList((CoreProvider) BlazeLoaderCP.instance);
    }

    public List<InterfaceProvider> getInterfaceProviders() {
        return Arrays.asList((InterfaceProvider) BlazeLoaderIPClient.instance);
    }

    public List<Observer> getObservers() {
        return null;
    }

    public List<CustomisationProvider> getCustomisationProviders() {
        return Arrays.asList((CustomisationProvider) BlazeLoaderBP.instance);
    }

    public abstract void shutdown(String message, int code);

    public abstract void init();

    public abstract boolean supportsServer();

    public abstract boolean supportsClient();

    public abstract BLMainClient getClient();

    public abstract BLMainServer getServer();

    public static BLMain instance() {
        return instance;
    }
}
