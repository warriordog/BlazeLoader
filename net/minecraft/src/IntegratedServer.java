package net.minecraft.src;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * The single-player server.  Replaces IntegratedServerProxy.
 */
public class IntegratedServer extends MinecraftServer
{
    /** The Minecraft instance. */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private final ILogAgent serverLogAgent;

    /** Instance of IntegratedServerListenThread. */
    private IntegratedServerListenThread theServerListeningThread;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;

    public IntegratedServer(Minecraft minecraft, String folderName, String worldName, WorldSettings settings)
    {
        super(new File(minecraft.mcDataDir, "saves"));
        this.serverLogAgent = new LogAgent("Minecraft-Server", " [SERVER]", (new File(minecraft.mcDataDir, "output-server.log")).getAbsolutePath());
        this.setServerOwner(minecraft.getSession().getUsername());
        this.setFolderName(folderName);
        this.setWorldName(worldName);
        this.setDemo(minecraft.isDemo());
        this.canCreateBonusChest(settings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = minecraft;
        this.serverProxy = minecraft.getProxy();
        this.theWorldSettings = settings;

        try
        {
            this.theServerListeningThread = new IntegratedServerListenThread(this);
        }
        catch (IOException var6)
        {
            throw new Error();
        }
        mergeCommandHandlers(BlazeLoader.commandManager);
    }

    protected void loadAllWorlds(String mapName, String worldName, long ignored, WorldType worldType, String ignored_2)
    {
        this.convertMapIfNeeded(mapName);
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler var7 = this.getActiveAnvilConverter().getSaveLoader(mapName, true);
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
                    this.worldServers[var8] = new DemoWorldServer(this, var7, worldName, var9, this.theProfiler, this.getLogAgent());
                }
                else
                {
                    this.worldServers[var8] = new WorldServer(this, var7, worldName, var9, this.theWorldSettings, this.theProfiler, this.getLogAgent());
                }
            }
            else
            {
                this.worldServers[var8] = new WorldServerMulti(this, var7, worldName, var9, this.theWorldSettings, this.worldServers[0], this.theProfiler, this.getLogAgent());
            }
            this.worldServers[var8].addWorldAccess(new WorldManager(this, this.worldServers[var8]));
            this.getConfigurationManager().setPlayerManager(this.worldServers);
        }
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        this.serverLogAgent.logInfo("Starting integrated minecraft server version 1.6.4");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        this.serverLogAgent.logInfo("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        return true;
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick()
    {
        boolean var1 = this.isGamePaused;
        this.isGamePaused = this.theServerListeningThread.isGamePaused();

        if (!var1 && this.isGamePaused)
        {
            this.serverLogAgent.logInfo("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused)
        {
            super.tick();
        }
    }

    public boolean canStructuresSpawn()
    {
        return false;
    }

    public EnumGameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public int getDifficulty()
    {
        return this.mc.gameSettings.difficulty;
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    protected File getDataDirectory()
    {
        return this.mc.mcDataDir;
    }

    public boolean isDedicatedServer()
    {
        return false;
    }

    /**
     * Gets the IntergratedServerListenThread.
     */
    public IntegratedServerListenThread getServerListeningThread()
    {
        return this.theServerListeningThread;
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport crashReport)
    {
        this.mc.crashed(crashReport);
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport crashReport)
    {
        crashReport = super.addServerInfoToCrashReport(crashReport);
        crashReport.getCategory().addCrashSectionCallable("Type", new CallableType3(this));
        crashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableIsModded(this));
        return crashReport;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper snooper)
    {
        super.addServerStatsToSnooper(snooper);
        snooper.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(EnumGameType gameType, boolean allowCommands)
    {
        try
        {
            String var3 = this.theServerListeningThread.func_71755_c();
            this.getLogAgent().logInfo("Started on " + var3);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var3);
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(gameType);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCommands);
            return var3;
        }
        catch (IOException var4)
        {
            return null;
        }
    }

    public ILogAgent getLogAgent()
    {
        return this.serverLogAgent;
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        super.stopServer();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        super.initiateShutdown();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
        ModList.unloadWorld();
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic()
    {
        return this.isPublic;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(EnumGameType gameType)
    {
        this.getConfigurationManager().setGameType(gameType);
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return true;
    }

    public int func_110455_j()
    {
        return 4;
    }

    public NetworkListenThread getNetworkThread()
    {
        return this.getServerListeningThread();
    }

    protected void mergeCommandHandlers(CommandHandler handlerToMerge){
        CommandHandler newManager = (CommandHandler)this.getCommandManager();
        for(Object command : handlerToMerge.getCommands().values()){
            newManager.registerCommand((ICommand)command);
        }
    }

    /**
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    @Override
    public String getPlugins() {
        String plugins = "BlazeLoader " + Version.getStringVersion() + ":";
        for(Mod mod : ModList.getLoadedMods()){
            plugins = plugins.concat(" " + mod.getModName() + " " + mod.getStringModVersion() + ";");
        }
        int lastSemi = plugins.lastIndexOf(";");
        if(lastSemi != -1){
            plugins = plugins.substring(0, lastSemi);
        }
        return plugins;
    }

    @Override
    public String getServerModName() {
        return "BlazeLoader";
    }

}
