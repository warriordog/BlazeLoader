package com.blazeloader.api.main;

import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.minecraft.client.Minecraft;

/**
 * Client BLMain.
 */
public class BLMainClient extends BLMain {
    BLMainClient(LoaderEnvironment environment, LoaderProperties properties) {
        super(environment, properties);
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown(String message, int code) {
        try {
            LOGGER_FULL.logFatal("Unexpected shutdown requested!");
            LOGGER_FULL.logFatal("Message: " + message);
            Minecraft minecraft = Minecraft.getMinecraft();
            if (minecraft != null) {
                LOGGER_FULL.logFatal("Calling client shutdown.");
                minecraft.shutdown();
            } else {
                LOGGER_FULL.logFatal("Client is not running, closing immediately with code " + code + "!");
                System.exit(code);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(code);
        }
    }
    @Override
    public boolean supportsClient() {
        return true;
    }

    @Override
    public BLMainClient getClient() {
        return this;
    }
}
