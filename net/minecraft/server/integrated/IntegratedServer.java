package net.minecraft.server.integrated;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.Version;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.*;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * The single-player server.  Replaces IntegratedServerProxy.
 */
public class IntegratedServer extends MinecraftServer
{
    private static final Logger logger = LogManager.getLogger();
    /** The Minecraft instance. */
    private final Minecraft mc;
    public final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;
    private static final String __OBFID = "CL_00001129";

    public IntegratedServer(Minecraft minecraft, String folderName, String worldName, WorldSettings settings)
    {
        super(new File(minecraft.mcDataDir, "saves"), minecraft.getProxy());
        this.setServerOwner(minecraft.getSession().getUsername());
        this.setFolderName(folderName);
        this.setWorldName(worldName);
        this.setDemo(minecraft.isDemo());
        this.canCreateBonusChest(settings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = minecraft;
        this.theWorldSettings = settings;
        mergeCommandHandlers(BlazeLoader.commandHandler);
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
                    this.worldServers[var8] = new DemoWorldServer(this, var7, worldName, var9, this.theProfiler);
                }
                else
                {
                    this.worldServers[var8] = new WorldServer(this, var7, worldName, var9, this.theWorldSettings, this.theProfiler);
                }
            }
            else
            {
                this.worldServers[var8] = new WorldServerMulti(this, var7, worldName, var9, this.theWorldSettings, this.worldServers[0], this.theProfiler);
            }
            this.worldServers[var8].addWorldAccess(new WorldManager(this, this.worldServers[var8]));
            this.getConfigurationManager().setPlayerManager(this.worldServers);
        }
        this.func_147139_a(this.func_147135_j());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        logger.info("Starting integrated minecraft server version 1.7.2");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        logger.info("Generating keypair");
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
        this.isGamePaused = Minecraft.getMinecraft().func_147114_u() != null && Minecraft.getMinecraft().func_147113_T();

        if (!var1 && this.isGamePaused)
        {
            logger.info("Saving and pausing game...");
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

    public WorldSettings.GameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    /**
     * Defaults to "1" (Easy) for the dedicated server, defaults to "2" (Normal) on the client.
     */
    public EnumDifficulty func_147135_j()
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
        crashReport.getCategory().addCrashSectionCallable("Type", new Callable()
        {
            private static final String __OBFID = "CL_00001130";
            public String call()
            {
                return "Integrated Server (map_client.txt)";
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable()
        {
            private static final String __OBFID = "CL_00001131";
            public String call()
            {
                String var1 = ClientBrandRetriever.getClientModName();

                if (!var1.equals("vanilla"))
                {
                    return "Definitely; Client brand changed to \'" + var1 + "\'";
                }
                else
                {
                    var1 = IntegratedServer.this.getServerModName();
                    return !var1.equals("vanilla") ? "Definitely; Server brand changed to \'" + var1 + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
                }
            }
        });
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
    public String shareToLAN(WorldSettings.GameType gameType, boolean allowCommands)
    {
        try
        {
            int var3 = -1;

            try
            {
                var3 = HttpUtil.func_76181_a();
            }
            catch (IOException var5)
            {
                ;
            }

            if (var3 <= 0)
            {
                var3 = 25564;
            }

            this.func_147137_ag().func_151265_a((InetAddress)null, var3);
            logger.info("Started on " + var3);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), var3 + "");
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(gameType);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCommands);
            return var3 + "";
        }
        catch (IOException var6)
        {
            return null;
        }
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
    public void setGameType(WorldSettings.GameType gameType)
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

    public void mergeCommandHandlers(CommandHandler handlerToMerge){
        CommandHandler newManager = (CommandHandler)this.getCommandManager();
        for(Object command : handlerToMerge.getCommands().values()){
            newManager.registerCommand((ICommand)command);
        }
    }


    /**
     * previously getPlugins
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    @Override
    public String func_147133_T() {
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
