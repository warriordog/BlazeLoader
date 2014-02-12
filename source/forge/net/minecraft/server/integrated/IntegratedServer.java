package net.minecraft.server.integrated;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
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
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
public class IntegratedServer extends MinecraftServer {
    private static final Logger logger = LogManager.getLogger();
    /**
     * The Minecraft instance.
     */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;
    private static final String __OBFID = "CL_00001129";

    public IntegratedServer(Minecraft par1Minecraft, String par2Str, String par3Str, WorldSettings par4WorldSettings) {
        super(new File(par1Minecraft.mcDataDir, "saves"), par1Minecraft.getProxy());
        this.setServerOwner(par1Minecraft.getSession().getUsername());
        this.setFolderName(par2Str);
        this.setWorldName(par3Str);
        this.setDemo(par1Minecraft.isDemo());
        this.canCreateBonusChest(par4WorldSettings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigurationManager(new IntegratedPlayerList(this));
        this.mc = par1Minecraft;
        this.theWorldSettings = par4WorldSettings;
        mergeCommandHandlers(BlazeLoader.commandHandler);
    }

    protected void loadAllWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str) {
        this.convertMapIfNeeded(par1Str);
        ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);

        WorldServer overWorld = (isDemo() ? new DemoWorldServer(this, isavehandler, par2Str, 0, theProfiler) : new WorldServer(this, isavehandler, par2Str, 0, theWorldSettings, theProfiler));
        for (int dim : DimensionManager.getStaticDimensionIDs()) {
            WorldServer world = (dim == 0 ? overWorld : new WorldServerMulti(this, isavehandler, par2Str, dim, theWorldSettings, overWorld, theProfiler));
            world.addWorldAccess(new WorldManager(this, world));

            if (!this.isSinglePlayer()) {
                world.getWorldInfo().setGameType(getGameType());
            }

            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        }

        this.getConfigurationManager().setPlayerManager(new WorldServer[]{overWorld});
        this.func_147139_a(this.func_147135_j());
        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException {
        logger.info("Starting integrated minecraft server version 1.7.2");
        this.setOnlineMode(false);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        logger.info("Generating keypair");
        this.setKeyPair(CryptManager.createNewKeyPair());
        if (!FMLCommonHandler.instance().handleServerAboutToStart(this)) {
            return false;
        }
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.func_82749_j());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        return FMLCommonHandler.instance().handleServerStarting(this);
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick() {
        boolean flag = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();

        if (!flag && this.isGamePaused) {
            logger.info("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused) {
            super.tick();
        }
    }

    public boolean canStructuresSpawn() {
        return false;
    }

    public WorldSettings.GameType getGameType() {
        return this.theWorldSettings.getGameType();
    }

    public EnumDifficulty func_147135_j() {
        return this.mc.gameSettings.difficulty;
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore() {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    protected File getDataDirectory() {
        return this.mc.mcDataDir;
    }

    public boolean isDedicatedServer() {
        return false;
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport par1CrashReport) {
        this.mc.crashed(par1CrashReport);
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport par1CrashReport) {
        par1CrashReport = super.addServerInfoToCrashReport(par1CrashReport);
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new Callable() {
            private static final String __OBFID = "CL_00001130";

            public String call() {
                return "Integrated Server (map_client.txt)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable() {
            private static final String __OBFID = "CL_00001131";

            public String call() {
                String s = ClientBrandRetriever.getClientModName();

                if (!s.equals("vanilla")) {
                    return "Definitely; Client brand changed to \'" + s + "\'";
                } else {
                    s = IntegratedServer.this.getServerModName();
                    return !s.equals("vanilla") ? "Definitely; Server brand changed to \'" + s + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
                }
            }
        });
        return par1CrashReport;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
        super.addServerStatsToSnooper(par1PlayerUsageSnooper);
        par1PlayerUsageSnooper.addData("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled() {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(WorldSettings.GameType par1EnumGameType, boolean par2) {
        try {
            int i = -1;

            try {
                i = HttpUtil.func_76181_a();
            } catch (IOException ioexception) {
                ;
            }

            if (i <= 0) {
                i = 25564;
            }

            this.func_147137_ag().addLanEndpoint((InetAddress) null, i);
            logger.info("Started on " + i);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), i + "");
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(par1EnumGameType);
            this.getConfigurationManager().setCommandsAllowedForAll(par2);
            return i + "";
        } catch (IOException ioexception1) {
            return null;
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer() {
        super.stopServer();

        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown() {
        super.initiateShutdown();

        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic() {
        return this.isPublic;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(WorldSettings.GameType par1EnumGameType) {
        this.getConfigurationManager().setGameType(par1EnumGameType);
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled() {
        return true;
    }

    public int getOpPermissionLevel() {
        return 4;
    }

    public void mergeCommandHandlers(CommandHandler handlerToMerge) {
        CommandHandler newManager = (CommandHandler) this.getCommandManager();
        for (Object command : handlerToMerge.getCommands().values()) {
            newManager.registerCommand((ICommand) command);
        }
    }

    /**
     * previously getPlugins
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    /*
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
    */
    @Override
    public String getServerModName() {
        return "BlazeLoader";
    }
}
