package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.src.*;

import java.lang.reflect.Field;

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

    @Override
    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str) {
        this.convertMapIfNeeded(par1Str);
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);
        for (int var8 = 0; var8 < this.worldServers.length; ++var8)
        {
            byte var9 = 0;

            if (var8 == 1)
            {
                var9 = -1;
            }

            if (var8 == 2)
            {
                var9 = 1;
            }
            if (var8 == 0)
            {
                if (this.isDemo())
                {
                    this.worldServers[var8] = new DemoWorldServerProxy(this, var7, par2Str, var9, this.theProfiler, this.getLogAgent());
                }
                else
                {
                    this.worldServers[var8] = new WorldServerProxy(this, var7, par2Str, var9, getWorldSettings(), this.theProfiler, this.getLogAgent());
                }
            }
            else
            {
                this.worldServers[var8] = new WorldServerMulti(this, var7, par2Str, var9, getWorldSettings(), this.worldServers[0], this.theProfiler, this.getLogAgent());
            }
            this.worldServers[var8].addWorldAccess(new WorldManager(this, this.worldServers[var8]));
            this.getConfigurationManager().setPlayerManager(this.worldServers);
        }
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    protected WorldSettings getWorldSettings(){
        for(Field f : IntegratedServer.class.getDeclaredFields()){
            if(WorldSettings.class.isAssignableFrom(f.getType())){
                try{
                    f.setAccessible(true);
                    return (WorldSettings)f.get(this);
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Could not get WorldSettings field!", e);
                }
            }
        }
        throw new RuntimeException("WorldSettings field was not found!");
    }
}
