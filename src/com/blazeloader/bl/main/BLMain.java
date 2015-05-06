package com.blazeloader.bl.main;

import java.util.Collections;
import java.util.List;

import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

import com.blazeloader.bl.interop.ForgeModloader;
import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.api.CustomisationProvider;
import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;

/**
 * BL main class
 *
 * Has odd structure like "getClient" because while this will always be running on a server instance, it may not be running on a client.
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

    public static boolean isClient;
    
    /**
     * true if a game tick is in progress
     */
    public static boolean isInTick = false;
    /**
     * number of ticks that the game has been running
     */
    public static int numTicks = 0;

    public final LoaderEnvironment environment;
    public final LoaderProperties properties;

    private CommandHandler commandHandler;

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
        init();
    }

    public String[] getRequiredTransformers() {
        return new String[]{"com.blazeloader.util.transformers.BLAccessTransformer", "com.blazeloader.event.transformers.BLEventInjectionTransformer"};
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
        return Collections.singletonList((CoreProvider)BlazeLoaderCoreProvider.instance);
    }
    
    public List<InterfaceProvider> getInterfaceProviders() {
    	return Collections.singletonList(new BlazeLoaderInterfaceProvider());
    }

    public List<Observer> getObservers() {
        return null;
    }
    
    public List<CustomisationProvider> getCustomisationProviders() {
        return Collections.singletonList((CustomisationProvider)BlazeLoaderBrandingProvider.instance);
    }

    public List<Observer> getPreInitObservers() {
        return null;
    }

    public void shutdown(String message, int code) {
        try {
            LOGGER_FULL.logFatal("Unexpected shutdown detected!");
            LOGGER_FULL.logFatal("Message: " + message);
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                LOGGER_FULL.logFatal("Shutting down server...");
                server.initiateShutdown();
            } else {
                LOGGER_FULL.logFatal("Server is not running, closing immediately with code " + code + "!");
                ForgeModloader.exitJVM(code);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            ForgeModloader.exitJVM(code);
        }
    }

    public void init() {
        isClient = supportsClient();
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

    public CommandHandler getCommandHandler() {
        return commandHandler == null ? commandHandler = createCommandHandler() : commandHandler;
    }

    protected CommandHandler createCommandHandler() {
    	return new ServerCommandManager();
    }

    public String getPluginChannelName() {
        return "BLAZELOADER";
    }
}
