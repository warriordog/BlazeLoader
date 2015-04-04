package com.blazeloader.bl.main;

import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.integrated.IntegratedServerCommandManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Client BLMain.
 */
public class BLMainClient extends BLMain {
    BLMainClient(LoaderEnvironment environment, LoaderProperties properties) {
        super(environment, properties);
    }

    @Override
    public String[] getRequiredTransformers() {
        String[] superRequiredArr = super.getRequiredTransformers();
        List<String> superRequired = new ArrayList<String>();
        if (superRequiredArr != null) {
            superRequired.addAll(Arrays.asList(superRequiredArr));
        }
        superRequired.add("com.blazeloader.event.transformers.BLEventInjectionTransformerClient");
        return superRequired.toArray(new String[superRequired.size()]);
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
    
    //...why?
    //...because.
    @Override
    public BLMainClient getClient() {
        return this;
    }
    
    @Override
    protected CommandHandler createCommandHandler() {
    	return new IntegratedServerCommandManager();
    }
}
