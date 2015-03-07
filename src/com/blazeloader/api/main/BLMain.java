package com.blazeloader.api.main;

import com.blazeloader.api.client.event.BlazeLoaderIPClient;
import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * BL main class
 */
public class BLMain {
    private static BLMain instance;

    /**
     * Logger that logs date and time
     */
    public static final CLogger LOGGER_FULL = new CLogger("BlazeLoader", true, true, LogLevel.DEBUG);
    /**
     * Logger that logs time but not date
     */
    public static final CLogger LOGGER_MAIN = new CLogger("BlazeLoader", false, true, LogLevel.DEBUG);
    /**
     * Logger that does not log date or time
     */
    public static final CLogger LOGGER_FAST = new CLogger("BlazeLoader", false, false, LogLevel.DEBUG);

    public static boolean isInTick = false; //true if a game tick is in progress
    public static int numTicks = 0; //number of ticks that the game has been running

    public final LoaderEnvironment environment;
    public final LoaderProperties properties;

    private ServerCommandManager commandHandler;

    BLMain(LoaderEnvironment environment, LoaderProperties properties) {
        if (instance != null) {
            throw new IllegalStateException("BLMain cannot be created twice!");
        }
        instance = this;
        this.environment = environment;
        this.properties = properties;

        LOGGER_FULL.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_MAIN.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));
        LOGGER_FAST.setMinimumLogLevel(LogLevel.valueOf(Settings.minimumLogLevel));

        BLMain.LOGGER_FULL.logInfo("BlazeLoader initialized.");
    }

    public String[] getRequiredTransformers() {
        return new String[]{"com.blazeloader.api.transformers.BLAccessTransformer", "com.blazeloader.api.transformers.BLEventInjectionTransformer"};
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

    public List<Observer> getPreInitObservers() {
        return null;
    }

    public void shutdown(String message, int code) {
        try {
            LOGGER_FULL.logFatal("Unexpected shutdown requested!");
            LOGGER_FULL.logFatal("Message: " + message);
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                LOGGER_FULL.logFatal("Calling server shutdown.");
                server.initiateShutdown();
            } else {
                LOGGER_FULL.logFatal("Server is not running, closing immediately with code " + code + "!");
                System.exit(code);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(code);
        }
    }

    public void init() {

    }

    public boolean supportsClient() {
        return false;
    }

    public BLMainClient getClient() {
        throw new UnsupportedOperationException("This BLMain does not support BLMainClient!");
    }

    public static BLMain instance() {
        return instance;
    }


    public ServerCommandManager getCommandHandler() {
        //TODO: return an IntegratedServerCommandManager if running on client.  Not a high priority as integrated is currently empty...
        return commandHandler == null ? commandHandler = new ServerCommandManager() : commandHandler;
    }

}
