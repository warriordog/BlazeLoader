package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.*;

public class IntegratedServerProxy extends IntegratedServer {
    public IntegratedServerProxy(Minecraft minecraft, String folderName, String worldName, WorldSettings settings){
        super(minecraft, folderName, worldName, settings);
        ((MinecraftProxy)minecraft).theServer = this;
        setConfigurationManager(new IntegratedPlayerListProxy(this));
        mergeCommandHandlers(BlazeLoader.commandManager);
    }
    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    @Override
    public void initiateShutdown() {
        super.initiateShutdown();
        ModList.unloadWorld();
    }

    protected void mergeCommandHandlers(CommandHandler oldManager){
        CommandHandler newManager = (CommandHandler)this.getCommandManager();
        for(Object command : oldManager.getCommands().values()){
            newManager.registerCommand((ICommand)command);
        }
    }
}
