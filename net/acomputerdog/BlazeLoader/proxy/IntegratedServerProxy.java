package net.acomputerdog.BlazeLoader.proxy;

import net.minecraft.src.IntegratedServer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.WorldSettings;

import java.lang.reflect.Field;

public class IntegratedServerProxy extends IntegratedServer {
    public IntegratedServerProxy(Minecraft minecraft, IntegratedServer server){
        super(minecraft, server.getFolderName(), server.getWorldName(), getWorldSettings(server));
    }

    protected static WorldSettings getWorldSettings(IntegratedServer server){
        for(Field f : server.getClass().getDeclaredFields()){
            if(WorldSettings.class.isAssignableFrom(f.getType())){
                f.setAccessible(true);
                try {
                    return (WorldSettings)f.get(server);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not get world settings!", e);
                }
            }
        }
        return null;
    }
}
