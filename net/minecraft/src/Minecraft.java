package net.minecraft.src;

import com.google.common.collect.Lists;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.main.fixes.BlockAir;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.util.config.ConfigList;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.server.MinecraftServer;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Minecraft's main class.  Replaces MinecraftProxy.
 */
public class Minecraft implements IPlayerUsage
{
    protected boolean hasFiredLocalDeath = false;

    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == EnumOS.MACOS;

    /** A 10MiB preallocation to ensure the heap is reasonably sized. */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    public final ILogAgent mcLogAgent;
    private final File fileResourcepacks;
    private ServerData currentServerData;

    /** The RenderEngine instance used by Minecraft */
    public TextureManager renderEngine;

    /**
     * Set to 'this' in Minecraft constructor; used by some settings get methods
     */
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean hasCrashed;

    /** Instance of CrashReport. */
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    public Timer timer = new Timer(20.0F);

    /** Instance of PlayerUsageSnooper. */
    public PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getSystemTimeMillis());
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    public EntityClientPlayerMP thePlayer;

    /**
     * The Entity from which the renderer determines the render viewpoint. Currently is always the parent Minecraft
     * class's 'thePlayer' instance. Modification of its location, rotation, or other settings at render time will
     * modify the camera likewise, with the caveat of triggering chunk rebuilds as it moves, making it unsuitable for
     * changing the viewpoint mid-render.
     */
    public EntityLivingBase renderViewEntity;
    public EntityLivingBase pointedEntityLiving;
    public EffectRenderer effectRenderer;
    private final Session session;
    private boolean isGamePaused;

    /** The font renderer used for displaying and measuring text. */
    public FontRenderer fontRenderer;
    public FontRenderer standardGalacticFontRenderer;

    /** The GuiScreen that's being displayed at the moment. */
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;

    /** Mouse left click counter */
    private int leftClickCounter;

    /** Display width */
    private int tempDisplayWidth;

    /** Display height */
    private int tempDisplayHeight;

    /** Instance of IntegratedServer. */
    private IntegratedServer theIntegratedServer;

    /** Gui achievement */
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;

    /** Skip render world */
    public boolean skipRenderWorld;

    /** The ray trace hit that the mouse is over. */
    public MovingObjectPosition objectMouseOver;

    /** The game settings that currently hold effect. */
    public GameSettings gameSettings;
    public SoundManager sndManager;

    /** Mouse helper instance. */
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;

    /**
     * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
     * the usage snooping.
     */
    private static int debugFPS;

    /**
     * When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block.
     */
    private int rightClickDelayTimer;

    /**
     * Checked in Minecraft's while(running) loop, if true it's set to false and the textures refreshed.
     */
    private boolean refreshTexturePacksScheduled;

    /** Stat file writer */
    public StatFileWriter statFileWriter;
    private String serverName;
    private int serverPort;

    /**
     * Makes sure it doesn't keep taking screenshots when both buttons are down.
     */
    boolean isTakingScreenshot;

    /**
     * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
     */
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();

    /** Join player counter */
    private int joinPlayerCounter;
    private final boolean isDemo;
    private INetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;

    /** The profiler instance */
    public final Profiler mcProfiler = new Profiler();
    private long field_83002_am = -1L;
    private ReloadableResourceManager mcResourceManager;
    private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
    private List defaultResourcePacks = Lists.newArrayList();
    private DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;

    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
     */
    volatile boolean running = true;

    /** String that shows the debug information */
    public String debug = "";

    /** Approximate time (in ms) of last update to debug string */
    long debugUpdateTime = getSystemTime();

    /** holds the current fps */
    int fpsCounter;
    long prevFrameTime = -1L;

    /** Profiler currently displayed in the debug screen pie chart */
    private String debugProfilerName = "root";

    public Minecraft(Session session, int width, int height, boolean fullscreen, boolean demo, File dataDIR, File assetsDIR, File resourcePacksDIR, Proxy netProxy, String version)
    {
        theMinecraft = this;
        this.mcLogAgent = new LogAgent("Minecraft-Client", " [CLIENT]", (new File(dataDIR, "output-client.log")).getAbsolutePath());
        this.mcDataDir = dataDIR;
        this.fileAssets = assetsDIR;
        this.fileResourcepacks = resourcePacksDIR;
        this.launchedVersion = version;
        this.mcDefaultResourcePack = new DefaultResourcePack(this.fileAssets);
        this.addDefaultResourcePack();
        this.proxy = netProxy;
        this.startTimerHackThread();
        this.session = session;
        this.mcLogAgent.logInfo("Setting user: " + session.getUsername());
        this.mcLogAgent.logInfo("(Session ID is " + session.getSessionID() + ")");
        this.isDemo = demo;
        this.displayWidth = width;
        this.displayHeight = height;
        this.tempDisplayWidth = width;
        this.tempDisplayHeight = height;
        this.fullscreen = fullscreen;
        ImageIO.setUseCache(false);
        StatList.nopInit();
        ApiBase.theMinecraft = this;
        ApiBase.globalLogger = getLogAgent();
        ApiBase.theProfiler = mcProfiler;
    }

    private void startTimerHackThread()
    {
        ThreadClientSleep var1 = new ThreadClientSleep(this, "Timer hack thread");
        var1.setDaemon(true);
        var1.start();
    }

    public void crashed(CrashReport crashReport)
    {
        this.hasCrashed = true;
        this.crashReporter = crashReport;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport crashReport)
    {
        File var2 = new File(getMinecraft().mcDataDir, "crash-reports");
        File var3 = new File(var2, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        System.out.println(crashReport.getCompleteReport());

        if (crashReport.getFile() != null)
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
            System.exit(-1);
        }
        else if (crashReport.saveToFile(var3, this.getLogAgent()))
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + var3.getAbsolutePath());
            System.exit(-1);
        }
        else
        {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public void setServer(String serverName, int port)
    {
        this.serverName = serverName;
        this.serverPort = port;
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void startGame() throws LWJGLException
    {
        this.gameSettings = new GameSettings(this, this.mcDataDir);

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        if (this.fullscreen)
        {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();

            if (this.displayWidth <= 0)
            {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0)
            {
                this.displayHeight = 1;
            }
        }
        else
        {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setResizable(true);
        Display.setTitle("Minecraft 1.6.4");
        this.getLogAgent().logInfo("LWJGL Version: " + Sys.getVersion());

        if (Util.getOSType() != EnumOS.MACOS)
        {
            try
            {
                Display.setIcon(new ByteBuffer[] {this.readImage(new File(this.fileAssets, "/icons/icon_16x16.png")), this.readImage(new File(this.fileAssets, "/icons/icon_32x32.png"))});
            }
            catch (IOException var5)
            {
                var5.printStackTrace();
            }
        }

        try
        {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException var4)
        {
            var4.printStackTrace();

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ignored)
            {}

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }

            Display.create();
        }

        OpenGlHelper.initializeTextures();
        this.guiAchievement = new GuiAchievement(this);
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.sndManager = new SoundManager(this.mcResourceManager, this.gameSettings, this.fileAssets);
        this.mcResourceManager.registerReloadListener(this.sndManager);
        this.loadScreen();
        this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRenderer.setUnicodeFlag(this.mcLanguageManager.isCurrentLocaleUnicode());
            this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRenderer);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.entityRenderer = new EntityRenderer(this);
        this.statFileWriter = new StatFileWriter(this.session, this.mcDataDir);
        AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(this));
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        this.checkGLError("Startup");
        this.renderGlobal = new RenderGlobal(this);
        this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, new TextureMap(0, "textures/blocks"));
        this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        if (this.serverName != null)
        {
            this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
        }
        else
        {
            this.displayGuiScreen(new GuiMainMenu());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        BlockAir.injectBlockAir();
        ModList.start();
    }

    public void refreshResources()
    {
        ArrayList var1 = Lists.newArrayList(this.defaultResourcePacks);

        for (Object o : this.mcResourcePackRepository.getRepositoryEntries()) {
            ResourcePackRepositoryEntry var3 = (ResourcePackRepositoryEntry) o;
            var1.add(var3.getResourcePack());
        }

        this.mcLanguageManager.parseLanguageMetadata(var1);
        this.mcResourceManager.reloadResources(var1);

        if (this.renderGlobal != null)
        {
            this.renderGlobal.loadRenderers();
        }
    }

    private void addDefaultResourcePack()
    {
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    }

    private ByteBuffer readImage(File imageFile) throws IOException
    {
        BufferedImage var2 = ImageIO.read(imageFile);
        int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), null, 0, var2.getWidth());
        ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);
        int var6 = var3.length;

        for (int var8 : var3) {
            var4.putInt(var8 << 8 | var8 >> 24 & 255);
        }

        var4.flip();
        return var4;
    }

    private void updateDisplayMode() throws LWJGLException
    {
        HashSet var1 = new HashSet();
        Collections.addAll(var1, Display.getAvailableDisplayModes());
        DisplayMode var2 = Display.getDesktopDisplayMode();

        if (!var1.contains(var2) && Util.getOSType() == EnumOS.MACOS)
        {

            for (Object macDisplayMode : macDisplayModes) {
                DisplayMode var4 = (DisplayMode) macDisplayMode;
                boolean var5 = true;
                Iterator var6 = var1.iterator();
                DisplayMode var7;

                while (var6.hasNext()) {
                    var7 = (DisplayMode) var6.next();

                    if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() && var7.getHeight() == var4.getHeight()) {
                        var5 = false;
                        break;
                    }
                }

                if (!var5) {
                    var6 = var1.iterator();

                    while (var6.hasNext()) {
                        var7 = (DisplayMode) var6.next();

                        if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() / 2 && var7.getHeight() == var4.getHeight() / 2) {
                            var2 = var7;
                            break;
                        }
                    }
                }
            }
        }

        Display.setDisplayMode(var2);
        this.displayWidth = var2.getWidth();
        this.displayHeight = var2.getHeight();
    }

    /**
     * Displays a new screen.
     */
    private void loadScreen() throws LWJGLException
    {
        ScaledResolution var1 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, var1.getScaledWidth_double(), var1.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_FOG);
        this.renderEngine.bindTexture(locationMojangPng);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(16777215);
        var2.addVertexWithUV(0.0D, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double)this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var2.setColorOpaque_I(16777215);
        short var3 = 256;
        short var4 = 256;
        this.scaledTessellator((var1.getScaledWidth() - var3) / 2, (var1.getScaledHeight() - var4) / 2, 0, 0, var3, var4);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        Display.update();
    }

    /**
     * Loads Tessellator with a scaled resolution
     */
    public void scaledTessellator(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1), (double)(par2 + par6), 0.0D, (double)((float)(par3) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4) * var8));
        var9.addVertexWithUV((double)(par1), (double)(par2), 0.0D, (double)((float)(par3) * var7), (double)((float)(par4) * var8));
        var9.draw();
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader()
    {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen screen)
    {
        boolean doHandle = false;
        if (screen == null && this.thePlayer != null && this.thePlayer.getHealth() <= 0.0F){
            if(!hasFiredLocalDeath){
                hasFiredLocalDeath = true;
                ModList.eventClientPlayerDeath();
            }else{
                hasFiredLocalDeath = false;
            }
        }
        if(screen != null){
            GuiScreen gui = ModList.onGui(screen);
            if(gui != null){
                doHandle = true;
            }
        }else{
            screen = null;
            doHandle = true;
        }

        if(doHandle){
            if (this.currentScreen != null)
            {
                this.currentScreen.onGuiClosed();
            }

            this.statFileWriter.syncStats();

            if (screen == null && this.theWorld == null)
            {
                screen = new GuiMainMenu();
            }
            else if (screen == null && this.thePlayer.getHealth() <= 0.0F)
            {
                screen = new GuiGameOver();
            }

            if (screen instanceof GuiMainMenu)
            {
                this.gameSettings.showDebugInfo = false;
                this.ingameGUI.getChatGUI().clearChatMessages();
            }

            this.currentScreen = screen;

            if (screen != null)
            {
                this.setIngameNotInFocus();
                ScaledResolution var2 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
                int var3 = var2.getScaledWidth();
                int var4 = var2.getScaledHeight();
                (screen).setWorldAndResolution(this, var3, var4);
                this.skipRenderWorld = false;
            }
            else
            {
                this.setIngameFocus();
            }
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String errorLocation)
    {
        int var2 = GL11.glGetError();

        if (var2 != 0)
        {
            String var3 = GLU.gluErrorString(var2);
            this.getLogAgent().logSevere("########## GL ERROR ##########");
            this.getLogAgent().logSevere("@ " + errorLocation);
            this.getLogAgent().logSevere(var2 + ": " + var3);
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            this.statFileWriter.syncStats();
            this.getLogAgent().logInfo("Stopping!");

            try
            {
                this.loadWorld(null);
            }
            catch (Throwable ignored)
            {}

            try
            {
                GLAllocation.deleteTexturesAndDisplayLists();
            }
            catch (Throwable ignored)
            {}

            this.sndManager.cleanup();
        }
        finally
        {
            Display.destroy();

            if (!this.hasCrashed)
            {
                System.exit(0);
            }
        }

        System.gc();
    }

    public void run()
    {
        BlazeLoader.init(new File(System.getProperty("user.dir")));

        this.running = true;
        CrashReport var2;

        try
        {
            this.startGame();
        }
        catch (Throwable var11)
        {
            var2 = CrashReport.makeCrashReport(var11, "Initializing game");
            var2.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(var2));
            return;
        }

        try
        {
            while (this.running)
            {
                if (this.running)
                {
                    if (this.hasCrashed && this.crashReporter != null)
                    {
                        this.displayCrashReport(this.crashReporter);
                        return;
                    }

                    if (this.refreshTexturePacksScheduled)
                    {
                        this.refreshTexturePacksScheduled = false;
                        this.refreshResources();
                    }

                    try
                    {
                        this.runGameLoop();
                    }
                    catch (OutOfMemoryError var10)
                    {
                        this.freeMemory();
                        this.displayGuiScreen(new GuiMemoryErrorScreen());
                        System.gc();
                    }

                }
            }
        }
        catch (MinecraftError ignored)
        {
        }
        catch (ReportedException var13)
        {
            this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
            this.freeMemory();
            var13.printStackTrace();
            this.displayCrashReport(var13.getCrashReport());
        }
        catch (Throwable var14)
        {
            var2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
            this.freeMemory();
            var14.printStackTrace();
            this.displayCrashReport(var2);
        }
        finally
        {
            this.shutdownMinecraftApplet();
        }
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop()
    {
        AxisAlignedBB.getAABBPool().cleanPool();

        if (this.theWorld != null)
        {
            this.theWorld.getWorldVec3Pool().clear();
        }

        this.mcProfiler.startSection("root");

        if (Display.isCloseRequested())
        {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null)
        {
            float var1 = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = var1;
        }
        else
        {
            this.timer.updateTimer();
        }

        long var6 = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int var3 = 0; var3 < this.timer.elapsedTicks; ++var3)
        {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long var7 = System.nanoTime() - var6;
        this.checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        this.sndManager.setListener(this.thePlayer, this.timer.renderPartialTicks);

        if (!this.isGamePaused)
        {
            this.sndManager.func_92071_g();
        }

        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        this.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (!Keyboard.isKeyDown(65))
        {
            Display.update();
        }

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock())
        {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld)
        {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
            this.mcProfiler.endSection();
        }

        GL11.glFlush();
        this.mcProfiler.endSection();

        if (!Display.isActive() && this.fullscreen)
        {
            this.toggleFullscreen();
        }

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart)
        {
            if (!this.mcProfiler.profilingEnabled)
            {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(var7);
        }
        else
        {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.mcProfiler.startSection("root");
        Thread.yield();

        if (Keyboard.isKeyDown(65))
        {
            Display.update();
        }

        this.screenshotListener();

        if (!this.fullscreen && Display.wasResized())
        {
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth <= 0)
            {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0)
            {
                this.displayHeight = 1;
            }

            this.resize(this.displayWidth, this.displayHeight);
        }

        this.checkGLError("Post render");
        ++this.fpsCounter;
        boolean var5 = this.isGamePaused;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();

        if (this.isIntegratedServerRunning() && this.thePlayer != null && this.thePlayer.sendQueue != null && this.isGamePaused != var5)
        {
            ((MemoryConnection)this.thePlayer.sendQueue.getNetManager()).setGamePaused(this.isGamePaused);
        }

        while (getSystemTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.startSnooper();
            }
        }

        this.mcProfiler.endSection();

        if (this.getLimitFramerate() > 0)
        {
            Display.sync(EntityRenderer.performanceToFps(this.getLimitFramerate()));
        }
    }

    private int getLimitFramerate()
    {
        return this.currentScreen != null && this.currentScreen instanceof GuiMainMenu ? 2 : this.gameSettings.limitFramerate;
    }

    public void freeMemory()
    {
        try
        {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable ignored)
        {}

        try
        {
            System.gc();
            AxisAlignedBB.getAABBPool().clearPool();
            this.theWorld.getWorldVec3Pool().clearAndFreeCache();
        }
        catch (Throwable ignored)
        {}

        try
        {
            System.gc();
            this.loadWorld(null);
        }
        catch (Throwable ignored)
        {}

        System.gc();
    }

    /**
     * checks if keys are down
     */
    private void screenshotListener()
    {
        if (Keyboard.isKeyDown(60))
        {
            if (!this.isTakingScreenshot)
            {
                this.isTakingScreenshot = true;
                this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight));
            }
        }
        else
        {
            this.isTakingScreenshot = false;
        }
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int key)
    {
        List var2 = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (var2 != null && !var2.isEmpty())
        {
            ProfilerResult var3 = (ProfilerResult)var2.remove(0);

            if (key == 0)
            {
                if (var3.field_76331_c.length() > 0)
                {
                    int var4 = this.debugProfilerName.lastIndexOf(".");

                    if (var4 >= 0)
                    {
                        this.debugProfilerName = this.debugProfilerName.substring(0, var4);
                    }
                }
            }
            else
            {
                --key;

                if (key < var2.size() && !((ProfilerResult)var2.get(key)).field_76331_c.equals("unspecified"))
                {
                    if (this.debugProfilerName.length() > 0)
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((ProfilerResult)var2.get(key)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long ignored)
    {
        if (this.mcProfiler.profilingEnabled)
        {
            List var3 = this.mcProfiler.getProfilingData(this.debugProfilerName);
            ProfilerResult var4 = (ProfilerResult)var3.remove(0);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Tessellator var5 = Tessellator.instance;
            short var6 = 160;
            int var7 = this.displayWidth - var6 - 10;
            int var8 = this.displayHeight - var6 * 2;
            GL11.glEnable(GL11.GL_BLEND);
            var5.startDrawingQuads();
            var5.setColorRGBA_I(0, 200);
            var5.addVertex((double)((float)var7 - (float)var6 * 1.1F), (double)((float)var8 - (float)var6 * 0.6F - 16.0F), 0.0D);
            var5.addVertex((double)((float)var7 - (float)var6 * 1.1F), (double)(var8 + var6 * 2), 0.0D);
            var5.addVertex((double)((float)var7 + (float)var6 * 1.1F), (double)(var8 + var6 * 2), 0.0D);
            var5.addVertex((double)((float)var7 + (float)var6 * 1.1F), (double)((float)var8 - (float)var6 * 0.6F - 16.0F), 0.0D);
            var5.draw();
            GL11.glDisable(GL11.GL_BLEND);
            double var9 = 0.0D;
            int var13;

            for (Object aVar3 : var3) {
                ProfilerResult var12 = (ProfilerResult) aVar3;
                var13 = MathHelper.floor_double(var12.field_76332_a / 4.0D) + 1;
                var5.startDrawing(6);
                var5.setColorOpaque_I(var12.func_76329_a());
                var5.addVertex((double) var7, (double) var8, 0.0D);
                int var14;
                float var15;
                float var17;
                float var16;

                for (var14 = var13; var14 >= 0; --var14) {
                    var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D / 100.0D);
                    var16 = MathHelper.sin(var15) * (float) var6;
                    var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
                    var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
                }

                var5.draw();
                var5.startDrawing(5);
                var5.setColorOpaque_I((var12.func_76329_a() & 16711422) >> 1);

                for (var14 = var13; var14 >= 0; --var14) {
                    var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D / 100.0D);
                    var16 = MathHelper.sin(var15) * (float) var6;
                    var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
                    var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
                    var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17 + 10.0F), 0.0D);
                }

                var5.draw();
                var9 += var12.field_76332_a;
            }

            DecimalFormat var19 = new DecimalFormat("##0.00");
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            String var18 = "";

            if (!var4.field_76331_c.equals("unspecified"))
            {
                var18 = var18 + "[0] ";
            }

            if (var4.field_76331_c.length() == 0)
            {
                var18 = var18 + "ROOT ";
            }
            else
            {
                var18 = var18 + var4.field_76331_c + " ";
            }

            var13 = 16777215;
            this.fontRenderer.drawStringWithShadow(var18, var7 - var6, var8 - var6 / 2 - 16, var13);
            this.fontRenderer.drawStringWithShadow(var18 = var19.format(var4.field_76330_b) + "%", var7 + var6 - this.fontRenderer.getStringWidth(var18), var8 - var6 / 2 - 16, var13);

            for (int var21 = 0; var21 < var3.size(); ++var21)
            {
                ProfilerResult var20 = (ProfilerResult)var3.get(var21);
                String var22 = "";

                if (var20.field_76331_c.equals("unspecified"))
                {
                    var22 = var22 + "[?] ";
                }
                else
                {
                    var22 = var22 + "[" + (var21 + 1) + "] ";
                }

                var22 = var22 + var20.field_76331_c;
                this.fontRenderer.drawStringWithShadow(var22, var7 - var6, var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
                this.fontRenderer.drawStringWithShadow(var22 = var19.format(var20.field_76332_a) + "%", var7 + var6 - 50 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
                this.fontRenderer.drawStringWithShadow(var22 = var19.format(var20.field_76330_b) + "%", var7 + var6 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var21 * 8 + 20, var20.func_76329_a());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
        ModList.stop();
        BlazeLoader.saveSettings();
        ConfigList.saveAllConfigs();
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen(null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
     */
    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
            {
                this.sndManager.pauseAllSounds();
            }
        }
    }

    private void sendClickBlockToController(int par1, boolean par2)
    {
        if (!par2)
        {
            this.leftClickCounter = 0;
        }

        if (par1 != 0 || this.leftClickCounter <= 0)
        {
            if (par2 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && par1 == 0)
            {
                int var3 = this.objectMouseOver.blockX;
                int var4 = this.objectMouseOver.blockY;
                int var5 = this.objectMouseOver.blockZ;
                this.playerController.onPlayerDamageBlock(var3, var4, var5, this.objectMouseOver.sideHit);

                if (this.thePlayer.isCurrentToolAdventureModeExempt(var3, var4, var5))
                {
                    this.effectRenderer.addBlockHitEffects(var3, var4, var5, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    /**
     * Called whenever the mouse is clicked. Button clicked is 0 for left clicking and 1 for right clicking. Args:
     * buttonClicked
     */
    private void clickMouse(int button)
    {
        if (button != 0 || this.leftClickCounter <= 0)
        {
            if (button == 0)
            {
                this.thePlayer.swingItem();
            }

            if (button == 1)
            {
                this.rightClickDelayTimer = 4;
            }

            boolean var2 = true;
            ItemStack var3 = this.thePlayer.inventory.getCurrentItem();

            if (this.objectMouseOver == null)
            {
                if (button == 0 && this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                if (button == 0)
                {
                    this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                }

                if (button == 1 && this.playerController.func_78768_b(this.thePlayer, this.objectMouseOver.entityHit))
                {
                    var2 = false;
                }
            }
            else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
            {
                int var4 = this.objectMouseOver.blockX;
                int var5 = this.objectMouseOver.blockY;
                int var6 = this.objectMouseOver.blockZ;
                int var7 = this.objectMouseOver.sideHit;

                if (button == 0)
                {
                    this.playerController.clickBlock(var4, var5, var6, this.objectMouseOver.sideHit);
                }
                else
                {
                    int var8 = var3 != null ? var3.stackSize : 0;

                    if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, var3, var4, var5, var6, var7, this.objectMouseOver.hitVec))
                    {
                        var2 = false;
                        this.thePlayer.swingItem();
                    }

                    if (var3 == null)
                    {
                        return;
                    }

                    if (var3.stackSize == 0)
                    {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                    }
                    else if (var3.stackSize != var8 || this.playerController.isInCreativeMode())
                    {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                    }
                }
            }

            if (var2 && button == 1)
            {
                ItemStack var9 = this.thePlayer.inventory.getCurrentItem();

                if (var9 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var9))
                {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;

            if (this.fullscreen)
            {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            Display.update();
        }
        catch (Exception var2)
        {
            var2.printStackTrace();
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int width, int height)
    {
        this.displayWidth = width <= 0 ? 1 : width;
        this.displayHeight = height <= 0 ? 1 : height;

        if (this.currentScreen != null)
        {
            ScaledResolution var3 = new ScaledResolution(this.gameSettings, width, height);
            int var4 = var3.getScaledWidth();
            int var5 = var3.getScaledHeight();
            this.currentScreen.setWorldAndResolution(this, var4, var5);
        }
    }

    /**
     * Runs the current tick.
     */
    public void runTick()
    {
        BlazeLoader.isInTick = true;
        BlazeLoader.ticks++;
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        this.mcProfiler.startSection("stats");
        this.statFileWriter.func_77449_e();
        this.mcProfiler.endStartSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.endStartSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null)
        {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null)
        {
            if (this.thePlayer.getHealth() <= 0.0F)
            {
                this.displayGuiScreen(null);
            }
            else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
        {
            this.displayGuiScreen(null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        CrashReport var2;
        CrashReportCategory var3;

        if (this.currentScreen != null)
        {
            try
            {
                this.currentScreen.handleInput();
            }
            catch (Throwable var6)
            {
                var2 = CrashReport.makeCrashReport(var6, "Updating screen events");
                var3 = var2.makeCategory("Affected screen");
                var3.addCrashSectionCallable("Screen name", new CallableUpdatingScreenName(this));
                throw new ReportedException(var2);
            }

            if (this.currentScreen != null)
            {
                try
                {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable var5)
                {
                    var2 = CrashReport.makeCrashReport(var5, "Ticking screen");
                    var3 = var2.makeCategory("Affected screen");
                    var3.addCrashSectionCallable("Screen name", new CallableParticleScreenName(this));
                    throw new ReportedException(var2);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            this.mcProfiler.endStartSection("mouse");
            int var1;

            while (Mouse.next())
            {
                var1 = Mouse.getEventButton();

                if (isRunningOnMac && var1 == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)))
                {
                    var1 = 1;
                }

                KeyBinding.setKeyBindState(var1 - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState())
                {
                    KeyBinding.onTick(var1 - 100);
                }

                long var9 = getSystemTime() - this.systemTime;

                if (var9 <= 200L)
                {
                    int var4 = Mouse.getEventDWheel();

                    if (var4 != 0)
                    {
                        this.thePlayer.inventory.changeCurrentItem(var4);

                        if (this.gameSettings.noclip)
                        {
                            if (var4 > 0)
                            {
                                var4 = 1;
                            }

                            if (var4 < 0)
                            {
                                var4 = -1;
                            }

                            this.gameSettings.noclipRate += (float)var4 * 0.25F;
                        }
                    }

                    if (this.currentScreen == null)
                    {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState())
                        {
                            this.setIngameFocus();
                        }
                    }
                    else if (this.currentScreen != null)
                    {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");
            boolean var8;

            while (Keyboard.next())
            {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState())
                {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (this.field_83002_am > 0L)
                {
                    if (getSystemTime() - this.field_83002_am >= 6000L)
                    {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                    {
                        this.field_83002_am = -1L;
                    }
                }
                else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
                {
                    this.field_83002_am = getSystemTime();
                }

                if (Keyboard.getEventKeyState())
                {
                    if (Keyboard.getEventKey() == 87)
                    {
                        this.toggleFullscreen();
                    }
                    else
                    {
                        if (this.currentScreen != null)
                        {
                            this.currentScreen.handleKeyboardInput();
                        }
                        else
                        {
                            if (Keyboard.getEventKey() == 1)
                            {
                                this.displayInGameMenu();
                            }

                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61))
                            {
                                this.refreshResources();
                            }

                            if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61))
                            {
                                this.refreshResources();
                            }

                            if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61))
                            {
                                var8 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                                this.gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, var8 ? -1 : 1);
                            }

                            if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61))
                            {
                                this.renderGlobal.loadRenderers();
                            }

                            if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61))
                            {
                                this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                                this.gameSettings.saveOptions();
                            }

                            if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61))
                            {
                                RenderManager.field_85095_o = !RenderManager.field_85095_o;
                            }

                            if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61))
                            {
                                this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                                this.gameSettings.saveOptions();
                            }

                            if (Keyboard.getEventKey() == 59)
                            {
                                this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                            }

                            if (Keyboard.getEventKey() == 61)
                            {
                                this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                                this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            }

                            if (Keyboard.getEventKey() == 63)
                            {
                                ++this.gameSettings.thirdPersonView;

                                if (this.gameSettings.thirdPersonView > 2)
                                {
                                    this.gameSettings.thirdPersonView = 0;
                                }
                            }

                            if (Keyboard.getEventKey() == 66)
                            {
                                this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                            }
                        }

                        for (var1 = 0; var1 < 9; ++var1)
                        {
                            if (Keyboard.getEventKey() == 2 + var1)
                            {
                                this.thePlayer.inventory.currentItem = var1;
                            }
                        }

                        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart)
                        {
                            if (Keyboard.getEventKey() == 11)
                            {
                                this.updateDebugProfilerName(0);
                            }

                            for (var1 = 0; var1 < 9; ++var1)
                            {
                                if (Keyboard.getEventKey() == 2 + var1)
                                {
                                    this.updateDebugProfilerName(var1 + 1);
                                }
                            }
                        }
                    }
                }
            }

            var8 = this.gameSettings.chatVisibility != 2;

            while (this.gameSettings.keyBindInventory.isPressed())
            {
                if (this.playerController.func_110738_j())
                {
                    this.thePlayer.func_110322_i();
                }
                else
                {
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed())
            {
                this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }

            while (this.gameSettings.keyBindChat.isPressed() && var8)
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && var8)
            {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem())
            {
                if (!this.gameSettings.keyBindUseItem.pressed)
                {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                label381:

                while (true)
                {
                    if (!this.gameSettings.keyBindAttack.isPressed())
                    {
                        while (this.gameSettings.keyBindUseItem.isPressed()){

                        }

                        while (true)
                        {
                            if (this.gameSettings.keyBindPickBlock.isPressed())
                            {
                                continue;
                            }

                            break label381;
                        }
                    }
                }
            }
            else
            {
                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    this.clickMouse(0);
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    this.clickMouse(1);
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    this.clickMiddleMouseButton();
                }
            }

            if (this.gameSettings.keyBindUseItem.pressed && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
            {
                this.clickMouse(1);
            }

            this.sendClickBlockToController(0, this.currentScreen == null && this.gameSettings.keyBindAttack.pressed && this.inGameHasFocus);
        }

        if (this.theWorld != null)
        {
            if (this.thePlayer != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.theWorld.lastLightningBolt > 0)
                {
                    --this.theWorld.lastLightningBolt;
                }

                this.theWorld.updateEntities();
            }

            if (!this.isGamePaused)
            {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting > 0, true);

                try
                {
                    this.theWorld.tick();
                }
                catch (Throwable var7)
                {
                    var2 = CrashReport.makeCrashReport(var7, "Exception in world tick");

                    if (this.theWorld == null)
                    {
                        var3 = var2.makeCategory("Affected level");
                        var3.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.theWorld.addWorldInfoToCrashReport(var2);
                    }

                    throw new ReportedException(var2);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null)
            {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReadPackets();
        }

        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();

        BlazeLoader.isInTick = false;
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String folderName, String worldName, WorldSettings settings)
    {
        this.loadWorld(null);
        System.gc();
        ISaveHandler var4 = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo var5 = var4.loadWorldInfo();

        if (var5 == null && settings != null)
        {
            var5 = new WorldInfo(settings, folderName);
            var4.saveWorldInfo(var5);
        }

        if (settings == null)
        {
            settings = new WorldSettings(var5);
        }

        this.statFileWriter.readStat(StatList.startGameStat, 1);
        this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, settings);
        this.theIntegratedServer.startServerThread();
        this.integratedServerIsRunning = true;
        this.loadingScreen.displayProgressMessage(I18n.getString("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop())
        {
            String var6 = this.theIntegratedServer.getUserMessage();

            if (var6 != null)
            {
                this.loadingScreen.resetProgresAndWorkingMessage(I18n.getString(var6));
            }
            else
            {
                this.loadingScreen.resetProgresAndWorkingMessage("");
            }

            try
            {
                Thread.sleep(200L);
            }
            catch (InterruptedException ignored)
            {}
        }

        this.displayGuiScreen(null);

        try
        {
            NetClientHandler var10 = new NetClientHandler(this, this.theIntegratedServer);
            this.myNetworkManager = var10.getNetManager();
        }
        catch (IOException var8)
        {
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(new CrashReport("Connecting to integrated server", var8)));
        }
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient world)
    {
        this.loadWorld(world, "");
    }

    /**
     * message is displayed on the loading screen to the user unloads the current world first
     */
    public void loadWorld(WorldClient world, String message)
    {
        this.statFileWriter.syncStats();

        if (world == null)
        {
            NetClientHandler var3 = this.getNetHandler();

            if (var3 != null)
            {
                var3.cleanup();
            }

            if (this.myNetworkManager != null)
            {
                this.myNetworkManager.closeConnections();
            }

            if (this.theIntegratedServer != null)
            {
                this.theIntegratedServer.initiateShutdown();
            }

            this.theIntegratedServer = null;
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null)
        {
            this.loadingScreen.resetProgressAndMessage(message);
            this.loadingScreen.resetProgresAndWorkingMessage("");
        }

        if (world == null && this.theWorld != null)
        {
            this.setServerData(null);
            this.integratedServerIsRunning = false;
        }

        this.sndManager.playStreaming(null, 0.0F, 0.0F, 0.0F);
        this.sndManager.stopAllSounds();
        this.theWorld = world;

        if (world != null)
        {
            if (this.renderGlobal != null)
            {
                this.renderGlobal.setWorldAndLoadRenderers(world);
            }

            if (this.effectRenderer != null)
            {
                this.effectRenderer.clearEffects(world);
            }

            if (this.thePlayer == null)
            {
                this.thePlayer = this.playerController.func_78754_a(world);
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            world.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        }
        else
        {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;

        ApiBase.localPlayer = this.thePlayer;
        ModList.loadWorld(world, message);
    }

    /**
     * A String of renderGlobal.getDebugInfoRenders
     */
    public String debugInfoRenders()
    {
        return this.renderGlobal.getDebugInfoRenders();
    }

    /**
     * Gets the information in the F3 menu about how many entities are infront/around you
     */
    public String getEntityDebug()
    {
        return this.renderGlobal.getDebugInfoEntities();
    }

    /**
     * Gets the name of the world's current chunk provider
     */
    public String getWorldProviderName()
    {
        return this.theWorld.getProviderName();
    }

    /**
     * A String of how many entities are in the world
     */
    public String debugInfoEntities()
    {
        return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
    }

    public void setDimensionAndSpawnPlayer(int dimension)
    {
        this.theWorld.setSpawnLocation();
        this.theWorld.removeAllEntities();
        int var2 = 0;
        String var3 = null;

        if (this.thePlayer != null)
        {
            var2 = this.thePlayer.entityId;
            this.theWorld.removeEntity(this.thePlayer);
            var3 = this.thePlayer.func_142021_k();
        }

        this.renderViewEntity = null;
        this.thePlayer = this.playerController.func_78754_a(this.theWorld);
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.func_142020_c(var3);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.entityId = var2;
        this.playerController.setPlayerCapabilities(this.thePlayer);

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen(null);
        }

        ApiBase.localPlayer = this.thePlayer;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo()
    {
        return this.isDemo;
    }

    /**
     * Returns the NetClientHandler.
     */
    public NetClientHandler getNetHandler()
    {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled()
    {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    /**
     * Returns true if the message is a client command and should not be sent to the server. However there are no such
     * commands at this point in time.
     */
    public boolean handleClientCommand(String ignored)
    {
        return false;
    }

    /**
     * Called when the middle mouse button gets clicked
     */
    private void clickMiddleMouseButton()
    {
        if (this.objectMouseOver != null)
        {
            boolean var1 = this.thePlayer.capabilities.isCreativeMode;
            int var3 = 0;
            boolean var4 = false;
            int var2;
            int var5;

            if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
            {
                var5 = this.objectMouseOver.blockX;
                int var6 = this.objectMouseOver.blockY;
                int var7 = this.objectMouseOver.blockZ;
                Block var8 = Block.blocksList[this.theWorld.getBlockId(var5, var6, var7)];

                if (var8 == null)
                {
                    return;
                }

                var2 = var8.idPicked(this.theWorld, var5, var6, var7);

                if (var2 == 0)
                {
                    return;
                }

                var4 = Item.itemsList[var2].getHasSubtypes();
                int var9 = var2 < 256 && !Block.blocksList[var8.blockID].isFlowerPot() ? var2 : var8.blockID;
                var3 = Block.blocksList[var9].getDamageValue(this.theWorld, var5, var6, var7);
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != EnumMovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !var1)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    var2 = Item.painting.itemID;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    var2 = Item.leash.itemID;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame var10 = (EntityItemFrame)this.objectMouseOver.entityHit;

                    if (var10.getDisplayedItem() == null)
                    {
                        var2 = Item.itemFrame.itemID;
                    }
                    else
                    {
                        var2 = var10.getDisplayedItem().itemID;
                        var3 = var10.getDisplayedItem().getItemDamage();
                        var4 = true;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart var11 = (EntityMinecart)this.objectMouseOver.entityHit;

                    if (var11.getMinecartType() == 2)
                    {
                        var2 = Item.minecartPowered.itemID;
                    }
                    else if (var11.getMinecartType() == 1)
                    {
                        var2 = Item.minecartCrate.itemID;
                    }
                    else if (var11.getMinecartType() == 3)
                    {
                        var2 = Item.minecartTnt.itemID;
                    }
                    else if (var11.getMinecartType() == 5)
                    {
                        var2 = Item.minecartHopper.itemID;
                    }
                    else
                    {
                        var2 = Item.minecartEmpty.itemID;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    var2 = Item.boat.itemID;
                }
                else
                {
                    var2 = Item.monsterPlacer.itemID;
                    var3 = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    var4 = true;

                    if (var3 <= 0 || !EntityList.entityEggs.containsKey(Integer.valueOf(var3)))
                    {
                        return;
                    }
                }
            }

            this.thePlayer.inventory.setCurrentItem(var2, var3, var4, var1);

            if (var1)
            {
                var5 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + this.thePlayer.inventory.currentItem;
                this.playerController.sendSlotPacket(this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), var5);
            }
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport crashReport)
    {
        crashReport.getCategory().addCrashSectionCallable("Launched Version", new CallableLaunchedVersion(this));
        crashReport.getCategory().addCrashSectionCallable("LWJGL", new CallableLWJGLVersion(this));
        crashReport.getCategory().addCrashSectionCallable("OpenGL", new CallableGLInfo(this));
        crashReport.getCategory().addCrashSectionCallable("Is Modded", new CallableModded(this));
        crashReport.getCategory().addCrashSectionCallable("Type", new CallableType2(this));
        crashReport.getCategory().addCrashSectionCallable("Resource Pack", new CallableTexturePack(this));
        crashReport.getCategory().addCrashSectionCallable("Current Language", new CallableClientProfiler(this));
        crashReport.getCategory().addCrashSectionCallable("Profiler Position", new CallableClientMemoryStats(this));
        crashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new MinecraftINNER13(this));

        if (this.theWorld != null)
        {
            this.theWorld.addWorldInfoToCrashReport(crashReport);
        }

        return crashReport;
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getMinecraft()
    {
        return theMinecraft;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper snooper)
    {
        snooper.addData("fps", debugFPS);
        snooper.addData("texpack_name", this.mcResourcePackRepository.getResourcePackName());
        snooper.addData("vsync_enabled", this.gameSettings.enableVsync);
        snooper.addData("display_frequency", Display.getDisplayMode().getFrequency());
        snooper.addData("display_type", this.fullscreen ? "fullscreen" : "windowed");
        snooper.addData("run_time", (MinecraftServer.getSystemTimeMillis() - snooper.func_130105_g()) / 60L * 1000L);

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null)
        {
            snooper.addData("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper snooper)
    {
        snooper.addData("opengl_version", GL11.glGetString(GL11.GL_VERSION));
        snooper.addData("opengl_vendor", GL11.glGetString(GL11.GL_VENDOR));
        snooper.addData("client_brand", ClientBrandRetriever.getClientModName());
        snooper.addData("launched_version", this.launchedVersion);
        ContextCapabilities var2 = GLContext.getCapabilities();
        snooper.addData("gl_caps[ARB_multitexture]", var2.GL_ARB_multitexture);
        snooper.addData("gl_caps[ARB_multisample]", var2.GL_ARB_multisample);
        snooper.addData("gl_caps[ARB_texture_cube_map]", var2.GL_ARB_texture_cube_map);
        snooper.addData("gl_caps[ARB_vertex_blend]", var2.GL_ARB_vertex_blend);
        snooper.addData("gl_caps[ARB_matrix_palette]", var2.GL_ARB_matrix_palette);
        snooper.addData("gl_caps[ARB_vertex_program]", var2.GL_ARB_vertex_program);
        snooper.addData("gl_caps[ARB_vertex_shader]", var2.GL_ARB_vertex_shader);
        snooper.addData("gl_caps[ARB_fragment_program]", var2.GL_ARB_fragment_program);
        snooper.addData("gl_caps[ARB_fragment_shader]", var2.GL_ARB_fragment_shader);
        snooper.addData("gl_caps[ARB_shader_objects]", var2.GL_ARB_shader_objects);
        snooper.addData("gl_caps[ARB_vertex_buffer_object]", var2.GL_ARB_vertex_buffer_object);
        snooper.addData("gl_caps[ARB_framebuffer_object]", var2.GL_ARB_framebuffer_object);
        snooper.addData("gl_caps[ARB_pixel_buffer_object]", var2.GL_ARB_pixel_buffer_object);
        snooper.addData("gl_caps[ARB_uniform_buffer_object]", var2.GL_ARB_uniform_buffer_object);
        snooper.addData("gl_caps[ARB_texture_non_power_of_two]", var2.GL_ARB_texture_non_power_of_two);
        snooper.addData("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS));
        snooper.addData("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));
        snooper.addData("gl_max_texture_size", getGLMaximumTextureSize());
    }

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize()
    {
        for (int var0 = 16384; var0 > 0; var0 >>= 1)
        {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, var0, var0, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
            int var1 = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (var1 != 0)
            {
                return var0;
            }
        }

        return -1;
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return this.gameSettings.snooperEnabled;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData serverData)
    {
        this.currentServerData = serverData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer()
    {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer()
    {
        if (theMinecraft != null)
        {
            IntegratedServer var0 = theMinecraft.getIntegratedServer();

            if (var0 != null)
            {
                var0.stopServer();
            }
        }
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public PlayerUsageSnooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen()
    {
        return this.fullscreen;
    }

    public ILogAgent getLogAgent()
    {
        return this.mcLogAgent;
    }

    public Session getSession()
    {
        return this.session;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.renderEngine;
    }

    public ResourceManager getResourceManager()
    {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository()
    {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager()
    {
        return this.mcLanguageManager;
    }

    static String getLaunchedVersion(Minecraft minecraft)
    {
        return minecraft.launchedVersion;
    }

    static LanguageManager func_142024_b(Minecraft minecraft)
    {
        return minecraft.mcLanguageManager;
    }

    public void setTPS(float tps){
        this.timer.ticksPerSecond = tps;
    }

    public float getTPS(){
        return this.timer.ticksPerSecond;
    }
}
