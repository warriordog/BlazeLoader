package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.api.command.ApiCommand;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.commands.bl.CommandBL;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.*;

import java.lang.reflect.Field;

public class IntegratedServerProxy extends IntegratedServer {
    public IntegratedServerProxy(Minecraft minecraft, IntegratedServer server){
        this(minecraft, server.getFolderName(), server.getWorldName(), getWorldSettings(server));
    }

    public IntegratedServerProxy(Minecraft minecraft, String folderName, String worldName, WorldSettings settings){
        super(minecraft, folderName, worldName, settings);
        setConfigurationManager(new IntegratedPlayerListProxy(this));
        mergeCommandHandlers(BlazeLoader.commandManager);
        ApiCommand.registerCommand(new CommandBL());
    }
    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    @Override
    public void initiateShutdown() {
        super.initiateShutdown();
        ModList.unloadWorld();
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

    protected void mergeCommandHandlers(CommandHandler oldManager){
        CommandHandler newManager = (CommandHandler)this.getCommandManager();
        for(Object command : oldManager.getCommands().values()){
            newManager.registerCommand((ICommand)command);
        }
    }
}
