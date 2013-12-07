package net.minecraft.client;

import com.google.common.collect.Lists;
import io.netty.util.concurrent.GenericFutureListener;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.util.config.ConfigList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.*;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Minecraft's main class.  Replaces MinecraftProxy.
 */
public class Minecraft implements IPlayerUsage
{
    private static final Logger field_147123_G = LogManager.getLogger();

    protected boolean hasFiredLocalDeath = false;

    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.MACOS;

    /** A 10MiB preallocation to ensure the heap is reasonably sized. */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
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
    public Entity field_147125_j;
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
    private final boolean field_147129_ai;
    private final boolean isDemo;
    private INetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;

    /** The profiler instance */
    public final Profiler mcProfiler = new Profiler();
    private long field_83002_am = -1L;
    private ReloadableResourceManager mcResourceManager;
    private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
    private List<ResourcePack> defaultResourcePacks = Lists.newArrayList();
    private DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private Framebuffer field_147124_at;
    private TextureMap field_147128_au;
    private SoundHandler field_147127_av;
    private MusicTicker field_147126_aw;

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
    private static final String __OBFID = "CL_00000631";

    public Minecraft(Session session, int width, int height, boolean fullscreen, boolean demo, File dataDIR, File assetsDIR, File resourcePacksDIR, Proxy netProxy, String version)
    {
        theMinecraft = this;
        this.mcDataDir = dataDIR;
        this.fileAssets = assetsDIR;
        this.fileResourcepacks = resourcePacksDIR;
        this.launchedVersion = version;
        this.mcDefaultResourcePack = new DefaultResourcePack(this.fileAssets);
        this.addDefaultResourcePack();
        this.proxy = netProxy == null ? Proxy.NO_PROXY : netProxy;
        this.startTimerHackThread();
        this.session = session;
        field_147123_G.info("Setting user: " + session.getUsername());
        field_147123_G.info("(Session ID is " + session.getSessionID() + ")");
        this.isDemo = demo;
        this.displayWidth = width;
        this.displayHeight = height;
        this.tempDisplayWidth = width;
        this.tempDisplayHeight = height;
        this.fullscreen = fullscreen;
        this.field_147129_ai = func_147122_X();
        ImageIO.setUseCache(false);
        Bootstrap.func_151354_b();
        ApiBase.theMinecraft = this;
        ApiBase.theProfiler = mcProfiler;
    }

    private static boolean func_147122_X()
    {
        String[] var0 = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        String[] var1 = var0;
        int var2 = var0.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            String var4 = var1[var3];
            String var5 = System.getProperty(var4);

            if (var5 != null && var5.contains("64"))
            {
                return true;
            }
        }

        return false;
    }

    public static void func_147105_a(String p_147105_0_)
    {
        try
        {
            Toolkit var1 = Toolkit.getDefaultToolkit();
            Class var2 = var1.getClass();

            if (var2.getName().equals("sun.awt.X11.XToolkit"))
            {
                Field var3 = var2.getDeclaredField("awtAppClassName");
                var3.setAccessible(true);
                var3.set(var1, p_147105_0_);
            }
        }
        catch (Exception var4)
        {
        }
    }

    public Framebuffer func_147110_a()
    {
        return this.field_147124_at;
    }

    private void startTimerHackThread()
    {
        Thread var1 = new Thread("Timer hack thread")
        {
            private static final String __OBFID = "CL_00000632";
            public void run()
            {
                while (Minecraft.this.running)
                {
                    try
                    {
                        Thread.sleep(2147483647L);
                    }
                    catch (InterruptedException var2)
                    {
                    }
                }
            }
        };
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
        else if (crashReport.func_147149_a(var3))
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
        Display.setTitle("Minecraft 1.7.2");
        field_147123_G.info("LWJGL Version: " + Sys.getVersion());
        Util.EnumOS var1 = Util.getOSType();

        if (var1 != Util.EnumOS.MACOS)
        {
            try
            {
                Display.setIcon(new ByteBuffer[] {this.readImage(new File(this.fileAssets, "/icons/icon_16x16.png")), this.readImage(new File(this.fileAssets, "/icons/icon_32x32.png"))});
            }
            catch (IOException var6)
            {
                field_147123_G.error("Couldn\'t set icon", var6);
            }

            if (var1 != Util.EnumOS.WINDOWS)
            {
                func_147105_a("Minecraft");
            }
        }

        try
        {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException var5)
        {
            field_147123_G.error("Couldn\'t set pixel format", var5);

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
        this.field_147124_at = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.field_147124_at.func_147604_a(0.0F, 0.0F, 0.0F, 0.0F);
        this.guiAchievement = new GuiAchievement(this);
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.field_147127_av = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.field_147126_aw = new MusicTicker(this);
        this.mcResourceManager.registerReloadListener(this.field_147127_av);
        this.loadScreen();
        this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRenderer.setUnicodeFlag(this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.field_151455_aw);
            this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRenderer);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat()
        {
            private static final String __OBFID = "CL_00000639";
            public String formatString(String par1Str)
            {
                try
                {
                    return String.format(par1Str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.field_151445_Q.func_151463_i()));
                }
                catch (Exception var3)
                {
                    return "Error: " + var3.getLocalizedMessage();
                }
            }
        });
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
        this.field_147128_au = new TextureMap(0, "textures/blocks");
        this.field_147128_au.func_147632_b(this.gameSettings.field_151443_J);
        this.field_147128_au.func_147633_a(this.gameSettings.field_151442_I);
        this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.field_147128_au);
        this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        if (this.serverName != null)
        {
            this.func_147108_a(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
        }
        else
        {
            this.func_147108_a(new GuiMainMenu());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        Display.setVSyncEnabled(this.gameSettings.enableVsync);
        ModList.start();
    }

    public void refreshResources()
    {
        ArrayList<ResourcePack> var1 = Lists.newArrayList(this.defaultResourcePacks);

        for (Object o : this.mcResourcePackRepository.getRepositoryEntries()) {
            ResourcePackRepository.Entry var3 = (ResourcePackRepository.Entry) o;
            var1.add(var3.getResourcePack());
        }
        if (this.mcResourcePackRepository.func_148530_e() != null)
        {
            var1.add(this.mcResourcePackRepository.func_148530_e());
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

    private ByteBuffer readImage(File par1File) throws IOException
    {
        BufferedImage var2 = ImageIO.read(par1File);
        int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), null, 0, var2.getWidth());
        ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);
        int[] var5 = var3;
        int var6 = var3.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            int var8 = var5[var7];
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

        if (!var1.contains(var2) && Util.getOSType() == Util.EnumOS.MACOS)
        {
            Iterator var3 = macDisplayModes.iterator();

            while (var3.hasNext())
            {
                DisplayMode var4 = (DisplayMode)var3.next();
                boolean var5 = true;
                Iterator var6 = var1.iterator();
                DisplayMode var7;

                while (var6.hasNext())
                {
                    var7 = (DisplayMode)var6.next();

                    if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() && var7.getHeight() == var4.getHeight())
                    {
                        var5 = false;
                        break;
                    }
                }

                if (!var5)
                {
                    var6 = var1.iterator();

                    while (var6.hasNext())
                    {
                        var7 = (DisplayMode)var6.next();

                        if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() / 2 && var7.getHeight() == var4.getHeight() / 2)
                        {
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
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.renderEngine.bindTexture(locationMojangPng);
        ScaledResolution var1 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
        int var2 = var1.getScaleFactor();
        Framebuffer var3 = new Framebuffer(var1.getScaledWidth() * var2, var1.getScaledHeight() * var2, true);
        var3.func_147610_a(false);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) var1.getScaledWidth(), (double) var1.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.renderEngine.bindTexture(locationMojangPng);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        var4.setColorOpaque_I(16777215);
        var4.addVertexWithUV(0.0D, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
        var4.addVertexWithUV((double) this.displayWidth, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
        var4.addVertexWithUV((double) this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        var4.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        var4.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var4.setColorOpaque_I(16777215);
        short var5 = 256;
        short var6 = 256;
        this.scaledTessellator((var1.getScaledWidth() - var5) / 2, (var1.getScaledHeight() - var6) / 2, 0, 0, var5, var6);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        var3.func_147609_e();
        var3.func_147615_c(var1.getScaledWidth() * var2, var1.getScaledHeight() * var2);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glFlush();
        this.func_147120_f();
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
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), 0.0D, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), 0.0D, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), 0.0D, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
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
    public void func_147108_a(GuiScreen screen){
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
                this.currentScreen.func_146281_b();
            }

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
                this.ingameGUI.func_146158_b().func_146231_a();
            }

            this.currentScreen = screen;

            if (screen != null)
            {
                this.setIngameNotInFocus();
                ScaledResolution var2 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
                int var3 = var2.getScaledWidth();
                int var4 = var2.getScaledHeight();
                (screen).func_146280_a(this, var3, var4);
                this.skipRenderWorld = false;
            }
            else
            {
                this.field_147127_av.func_147687_e();
                this.setIngameFocus();
            }
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    public void checkGLError(String errorPoint)
    {
        int var2 = GL11.glGetError();

        if (var2 != 0)
        {
            String var3 = GLU.gluErrorString(var2);
            field_147123_G.error("########## GL ERROR ##########");
            field_147123_G.error("@ " + errorPoint);
            field_147123_G.error(var2 + ": " + var3);
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
            field_147123_G.info("Stopping!");

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

            this.field_147127_av.func_147685_d();
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

        while (true)
        {
            try
            {
                if (!this.running)
                {
                    break;
                }

                if (!this.hasCrashed || this.crashReporter == null)
                {
                    try
                    {
                        this.runGameLoop();
                    }
                    catch (OutOfMemoryError var10)
                    {
                        this.freeMemory();
                        this.func_147108_a(new GuiMemoryErrorScreen());
                        System.gc();
                    }

                    continue;
                }

                this.displayCrashReport(this.crashReporter);
            }
            catch (MinecraftError var12)
            {
                break;
            }
            catch (ReportedException var13)
            {
                this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
                this.freeMemory();
                field_147123_G.fatal("Reported exception thrown!", var13);
                this.displayCrashReport(var13.getCrashReport());
                break;
            }
            catch (Throwable var14)
            {
                var2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
                this.freeMemory();
                field_147123_G.fatal("Unreported exception thrown!", var14);
                this.displayCrashReport(var2);
                break;
            }

            return;
        }
        this.shutdownMinecraftApplet();
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

        if (Display.isCreated() && Display.isCloseRequested())
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


        if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled)
        {
            this.refreshTexturePacksScheduled = false;
            this.refreshResources();
        }

        long var5 = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int var3 = 0; var3 < this.timer.elapsedTicks; ++var3)
        {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long var6 = System.nanoTime() - var5;
        this.checkGLError("Pre render");
        RenderBlocks.field_147843_b = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        this.field_147127_av.func_147691_a(this.thePlayer, this.timer.renderPartialTicks);

        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.field_147124_at.func_147610_a(true);
        this.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

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
            this.displayDebugInfo(var6);
        }
        else
        {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }


        this.guiAchievement.func_146254_a();
        this.field_147124_at.func_147609_e();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.field_147124_at.func_147615_c(this.displayWidth, this.displayHeight);
        GL11.glPopMatrix();
        this.mcProfiler.startSection("root");
        this.func_147120_f();
        Thread.yield();

        this.screenshotListener();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();

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

        if (this.func_147107_h())
        {
            Display.sync(this.getLimitFramerate());
        }
    }

    public void func_147120_f()
    {
        Display.update();

        if (!this.fullscreen && Display.wasResized())
        {
            int var1 = this.displayWidth;
            int var2 = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != var1 || this.displayHeight != var2)
            {
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
        }
    }

    public int getLimitFramerate()
    {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean func_147107_h()
    {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.func_148267_f();
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
        if (this.gameSettings.field_151447_Z.func_151468_f())
        {
            if (!this.isTakingScreenshot)
            {
                this.isTakingScreenshot = true;
                this.ingameGUI.func_146158_b().func_146227_a(ScreenShotHelper.func_148260_a(this.mcDataDir, this.displayWidth, this.displayHeight, this.field_147124_at));
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
            Profiler.Result var3 = (Profiler.Result)var2.remove(0);

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

                if (key < var2.size() && !((Profiler.Result)var2.get(key)).field_76331_c.equals("unspecified"))
                {
                    if (this.debugProfilerName.length() > 0)
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)var2.get(key)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long ignored)
    {
        if (this.mcProfiler.profilingEnabled)
        {
            List var3 = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result var4 = (Profiler.Result)var3.remove(0);
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
                Profiler.Result var12 = (Profiler.Result) aVar3;
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
                Profiler.Result var20 = (Profiler.Result)var3.get(var21);
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
                this.func_147108_a(null);
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

            this.func_147108_a(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
            {
                this.field_147127_av.func_147689_b();
            }
        }
    }

    private void func_147115_a(boolean par1)
    {
        if (!par1)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0)
        {
            if (par1 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int var2 = this.objectMouseOver.blockX;
                int var3 = this.objectMouseOver.blockY;
                int var4 = this.objectMouseOver.blockZ;

                if (this.theWorld.func_147439_a(var2, var3, var4).func_149688_o() != Material.field_151579_a)
                {
                    this.playerController.onPlayerDamageBlock(var2, var3, var4, this.objectMouseOver.sideHit);

                    if (this.thePlayer.isCurrentToolAdventureModeExempt(var2, var3, var4))
                    {
                        this.effectRenderer.addBlockHitEffects(var2, var3, var4, this.objectMouseOver.sideHit);
                        this.thePlayer.swingItem();
                    }
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void func_147116_af()
    {
        if (this.leftClickCounter <= 0)
        {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null)
            {
                field_147123_G.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else
            {
                switch (Minecraft.SwitchMovingObjectType.field_151437_a[this.objectMouseOver.typeOfHit.ordinal()])
                {
                    case 1:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;

                    case 2:
                        int var1 = this.objectMouseOver.blockX;
                        int var2 = this.objectMouseOver.blockY;
                        int var3 = this.objectMouseOver.blockZ;

                        if (this.theWorld.func_147439_a(var1, var2, var3).func_149688_o() == Material.field_151579_a)
                        {
                            if (this.playerController.isNotCreative())
                            {
                                this.leftClickCounter = 10;
                            }
                        }
                        else
                        {
                            this.playerController.clickBlock(var1, var2, var3, this.objectMouseOver.sideHit);
                        }
                }
            }
        }
    }

    /**
     * Called whenever the mouse is clicked. Button clicked is 0 for left clicking and 1 for right clicking. Args:
     * buttonClicked
     */
    private void func_147121_ag()
    {
        this.rightClickDelayTimer = 4;
        boolean var1 = true;
        ItemStack var2 = this.thePlayer.inventory.getCurrentItem();

        if (this.objectMouseOver == null)
        {
            field_147123_G.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
        }
        else
        {
            switch (Minecraft.SwitchMovingObjectType.field_151437_a[this.objectMouseOver.typeOfHit.ordinal()])
            {
                case 1:
                    if (this.playerController.func_78768_b(this.thePlayer, this.objectMouseOver.entityHit))
                    {
                        var1 = false;
                    }

                    break;

                case 2:
                    int var3 = this.objectMouseOver.blockX;
                    int var4 = this.objectMouseOver.blockY;
                    int var5 = this.objectMouseOver.blockZ;

                    if (this.theWorld.func_147439_a(var3, var4, var5).func_149688_o() != Material.field_151579_a)
                    {
                        int var6 = var2 != null ? var2.stackSize : 0;

                        if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, var2, var3, var4, var5, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec))
                        {
                            var1 = false;
                            this.thePlayer.swingItem();
                        }

                        if (var2 == null)
                        {
                            return;
                        }

                        if (var2.stackSize == 0)
                        {
                            this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                        }
                        else if (var2.stackSize != var6 || this.playerController.isInCreativeMode())
                        {
                            this.entityRenderer.itemRenderer.resetEquippedProgress();
                        }
                    }
            }
        }

        if (var1)
        {
            ItemStack var7 = this.thePlayer.inventory.getCurrentItem();

            if (var7 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var7))
            {
                this.entityRenderer.itemRenderer.resetEquippedProgress2();
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
            else
            {
                this.func_147119_ah();
            }
            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.func_147120_f();
        }
        catch (Exception var2)
        {
            field_147123_G.error("Couldn\'t toggle fullscreen", var2);
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
            this.currentScreen.func_146280_a(this, var4, var5);
        }
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.func_147119_ah();
    }

    private void func_147119_ah()
    {
        this.field_147124_at.func_147613_a(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null)
        {
            this.entityRenderer.func_147704_a(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Runs the current tick.
     */
    public void runTick()
    {
        BlazeLoader.isInTick = true;
        BlazeLoader.numTicks++;
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

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
                this.func_147108_a(null);
            }
            else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null)
            {
                this.func_147108_a(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
        {
            this.func_147108_a(null);
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
                this.currentScreen.func_146269_k();
            }
            catch (Throwable var6)
            {
                var2 = CrashReport.makeCrashReport(var6, "Updating screen events");
                var3 = var2.makeCategory("Affected screen");
                var3.addCrashSectionCallable("Screen name", new Callable()
                {
                    private static final String __OBFID = "CL_00000640";
                    public String call()
                    {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
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
                    var3.addCrashSectionCallable("Screen name", new Callable()
                    {
                        private static final String __OBFID = "CL_00000642";
                        public String call()
                        {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(var2);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.field_146291_p)
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
                        this.currentScreen.func_146274_d();
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

                    if (Keyboard.getEventKey() == 62 && this.entityRenderer != null)
                    {
                        this.entityRenderer.func_147703_b();
                    }
                    if (Keyboard.getEventKey() == 87)
                    {
                        this.toggleFullscreen();
                    }
                    else
                    {
                        if (this.currentScreen != null)
                        {
                            this.currentScreen.func_146282_l();
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
                                this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, var8 ? -1 : 1);
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
                                this.gameSettings.showDebugProfilerChart = GuiScreen.func_146272_n();
                            }

                            if (this.gameSettings.field_151457_aa.func_151468_f())
                            {
                                ++this.gameSettings.thirdPersonView;

                                if (this.gameSettings.thirdPersonView > 2)
                                {
                                    this.gameSettings.thirdPersonView = 0;
                                }
                            }

                            if (this.gameSettings.field_151458_ab.func_151468_f())
                            {
                                this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
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

            for (var1 = 0; var1 < 9; ++var1)
            {
                if (this.gameSettings.field_151456_ac[var1].func_151468_f())
                {
                    this.thePlayer.inventory.currentItem = var1;
                }
            }

            var8 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.field_151445_Q.func_151468_f())
            {
                if (this.playerController.func_110738_j())
                {
                    this.thePlayer.func_110322_i();
                }
                else
                {
                    this.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.func_147108_a(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.func_151468_f())
            {
                this.thePlayer.dropOneItem(GuiScreen.func_146271_m());
            }

            while (this.gameSettings.keyBindChat.func_151468_f() && var8)
            {
                this.func_147108_a(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.func_151468_f() && var8)
            {
                this.func_147108_a(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem())
            {
                if (!this.gameSettings.keyBindUseItem.func_151470_d())
                {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                label391:

                while (true)
                {
                    if (!this.gameSettings.keyBindAttack.func_151468_f())
                    {
                        while (this.gameSettings.keyBindUseItem.func_151468_f())
                        {
                        }

                        while (true)
                        {
                            if (this.gameSettings.keyBindPickBlock.func_151468_f())
                            {
                                continue;
                            }

                            break label391;
                        }
                    }
                }
            }
            else
            {
                while (this.gameSettings.keyBindAttack.func_151468_f())
                {
                    this.func_147116_af();
                }

                while (this.gameSettings.keyBindUseItem.func_151468_f())
                {
                    this.func_147121_ag();
                }

                while (this.gameSettings.keyBindPickBlock.func_151468_f())
                {
                    this.func_147112_ai();
                }
            }

            if (this.gameSettings.keyBindUseItem.func_151470_d() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
            {
                this.func_147121_ag();
            }

            this.func_147115_a(this.currentScreen == null && this.gameSettings.keyBindAttack.func_151470_d() && this.inGameHasFocus);
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
                this.field_147126_aw.update();
                this.field_147127_av.update();
            }

            if (this.theWorld != null)
            {
                if (!this.isGamePaused)
                {
                    this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);
                }
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

        try
        {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, settings);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable var10)
        {
            CrashReport var7 = CrashReport.makeCrashReport(var10, "Starting integrated server");
            CrashReportCategory var8 = var7.makeCategory("Starting integrated server");
            var8.addCrashSection("Level ID", folderName);
            var8.addCrashSection("Level Name", worldName);
            throw new ReportedException(var7);
        }

        this.loadingScreen.displayProgressMessage(I18n.getStringParams("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop())
        {
            String var6 = this.theIntegratedServer.getUserMessage();

            if (var6 != null)
            {
                this.loadingScreen.resetProgresAndWorkingMessage(I18n.getStringParams(var6));
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

        this.func_147108_a(null);
        SocketAddress var11 = this.theIntegratedServer.func_147137_ag().func_151270_a();
        INetworkManager var12 = INetworkManager.func_150722_a(var11);
        var12.func_150719_a(new NetHandlerLoginClient(var12, this, null));
        var12.func_150725_a(new C00Handshake(4, var11.toString(), 0, EnumConnectionState.LOGIN));
        var12.func_150725_a(new C00PacketLoginStart(this.getSession().func_148256_e()));
        this.myNetworkManager = var12;
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

        if (world == null)
        {
            NetHandlerPlayClient var3 = this.func_147114_u();

            if (var3 != null)
            {
                var3.func_147296_c();
            }

            if (this.theIntegratedServer != null)
            {
                this.theIntegratedServer.initiateShutdown();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.func_146257_b();
            this.entityRenderer.func_147701_i().func_148249_a();
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
            if (this.mcResourcePackRepository.func_148530_e() != null)
            {
                this.func_147106_B();
            }

            this.mcResourcePackRepository.func_148529_f();
            this.setServerData(null);
            this.integratedServerIsRunning = false;
        }

        this.field_147127_av.func_147690_c();
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
                this.thePlayer = this.playerController.func_147493_a(world, new StatFileWriter());
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
            var2 = this.thePlayer.func_145782_y();
            this.theWorld.removeEntity(this.thePlayer);
            var3 = this.thePlayer.func_142021_k();
        }

        this.renderViewEntity = null;
        this.thePlayer = this.playerController.func_147493_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.func_146107_m());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.func_142020_c(var3);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.func_145769_d(var2);
        this.playerController.setPlayerCapabilities(this.thePlayer);

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.func_147108_a(null);
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
    public NetHandlerPlayClient func_147114_u()
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
     * Called when the middle mouse button gets clicked
     */
    private void func_147112_ai()
    {
        if (this.objectMouseOver != null)
        {
            boolean var1 = this.thePlayer.capabilities.isCreativeMode;
            int var3 = 0;
            boolean var4 = false;
            Item var2;
            int var5;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                var5 = this.objectMouseOver.blockX;
                int var6 = this.objectMouseOver.blockY;
                int var7 = this.objectMouseOver.blockZ;
                Block var8 = this.theWorld.func_147439_a(var5, var6, var7);

                if (var8.func_149688_o() == Material.field_151579_a)
                {
                    return;
                }

                var2 = var8.func_149694_d(this.theWorld, var5, var6, var7);

                if (var2 == null)
                {
                    return;
                }

                var4 = var2.getHasSubtypes();
                Block var9 = var2 instanceof ItemBlock && !var8.func_149648_K() ? Block.func_149634_a(var2) : var8;
                var3 = var9.func_149643_k(this.theWorld, var5, var6, var7);
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !var1)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    var2 = Items.field_151159_an;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    var2 = Items.field_151058_ca;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame var10 = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack var12 = var10.getDisplayedItem();

                    if (var12 == null)
                    {
                        var2 = Items.field_151160_bD;
                    }
                    else
                    {
                        var2 = var12.getItem();
                        var3 = var12.getItemDamage();
                        var4 = true;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart var11 = (EntityMinecart)this.objectMouseOver.entityHit;

                    if (var11.getMinecartType() == 2)
                    {
                        var2 = Items.field_151109_aJ;
                    }
                    else if (var11.getMinecartType() == 1)
                    {
                        var2 = Items.field_151108_aI;
                    }
                    else if (var11.getMinecartType() == 3)
                    {
                        var2 = Items.field_151142_bV;
                    }
                    else if (var11.getMinecartType() == 5)
                    {
                        var2 = Items.field_151140_bW;
                    }
                    else if (var11.getMinecartType() == 6)
                    {
                        var2 = Items.field_151095_cc;
                    }
                    else
                    {
                        var2 = Items.field_151143_au;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    var2 = Items.field_151124_az;
                }
                else
                {
                    var2 = Items.field_151063_bx;
                    var3 = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    var4 = true;

                    if (var3 <= 0 || !EntityList.entityEggs.containsKey(Integer.valueOf(var3)))
                    {
                        return;
                    }
                }
            }

            this.thePlayer.inventory.func_146030_a(var2, var3, var4, var1);

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
        crashReport.getCategory().addCrashSectionCallable("Launched Version", new Callable()
        {
            private static final String __OBFID = "CL_00000643";
            public String call()
            {
                return Minecraft.this.launchedVersion;
            }
        });
        crashReport.getCategory().addCrashSectionCallable("LWJGL", new Callable()
        {
            private static final String __OBFID = "CL_00000644";
            public String call()
            {
                return Sys.getVersion();
            }
        });
        crashReport.getCategory().addCrashSectionCallable("OpenGL", new Callable()
        {
            private static final String __OBFID = "CL_00000645";
            public String call()
            {
                return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable()
        {
            private static final String __OBFID = "CL_00000646";
            public String call()
            {
                String var1 = ClientBrandRetriever.getClientModName();
                return !var1.equals("vanilla") ? "Definitely; Client brand changed to \'" + var1 + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Type", new Callable()
        {
            private static final String __OBFID = "CL_00000647";
            public String call()
            {
                return "Client (map_client.txt)";
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Resource Packs", new Callable()
        {
            private static final String __OBFID = "CL_00000633";
            public String call()
            {
                return Minecraft.this.gameSettings.field_151453_l.toString();
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Current Language", new Callable()
        {
            private static final String __OBFID = "CL_00000634";
            public String call()
            {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Profiler Position", new Callable()
        {
            private static final String __OBFID = "CL_00000635";
            public String call()
            {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable()
        {
            private static final String __OBFID = "CL_00000636";
            public String call()
            {
                int var1 = Minecraft.this.theWorld.getWorldVec3Pool().getPoolSize();
                int var2 = 56 * var1;
                int var3 = var2 / 1024 / 1024;
                int var4 = Minecraft.this.theWorld.getWorldVec3Pool().func_82590_d();
                int var5 = 56 * var4;
                int var6 = var5 / 1024 / 1024;
                return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; " + var6 + " MB) used";
            }
        });
        crashReport.getCategory().addCrashSectionCallable("Anisotropic Filtering", new Callable()
        {
            private static final String __OBFID = "CL_00000637";
            public String call()
            {
                return Minecraft.this.gameSettings.field_151443_J == 1 ? "Off (1)" : "On (" + Minecraft.this.gameSettings.field_151443_J + ")";
            }
        });

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

    public void func_147106_B()
    {
        this.refreshTexturePacksScheduled = true;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper snooper)
    {
        snooper.addData("fps", debugFPS);
        snooper.addData("vsync_enabled", this.gameSettings.enableVsync);
        snooper.addData("display_frequency", Display.getDisplayMode().getFrequency());
        snooper.addData("display_type", this.fullscreen ? "fullscreen" : "windowed");
        snooper.addData("run_time", (MinecraftServer.getSystemTimeMillis() - snooper.func_130105_g()) / 60L * 1000L);
        snooper.addData("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int var2 = 0;

        for (Object o : this.mcResourcePackRepository.getRepositoryEntries()) {
            ResourcePackRepository.Entry var4 = (ResourcePackRepository.Entry) o;
            snooper.addData("resource_pack[" + var2++ + "]", var4.getResourcePackName());
        }

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

    public ServerData func_147104_D()
    {
        return this.currentServerData;
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

    public TextureMap func_147117_R()
    {
        return this.field_147128_au;
    }

    public boolean func_147111_S()
    {
        return this.field_147129_ai;
    }

    public boolean func_147113_T()
    {
        return this.isGamePaused;
    }

    public SoundHandler func_147118_V()
    {
        return this.field_147127_av;
    }

    public MusicTicker.MusicType func_147109_W()
    {
        return this.currentScreen instanceof GuiWinGame ? MusicTicker.MusicType.CREDITS : (this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? (BossStatus.bossName != null && BossStatus.statusBarLength > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU);
    }

    static final class SwitchMovingObjectType
    {
        static final int[] field_151437_a = new int[MovingObjectPosition.MovingObjectType.values().length];
        private static final String __OBFID = "CL_00000638";

        static
        {
            try
            {
                field_151437_a[MovingObjectPosition.MovingObjectType.ENTITY.ordinal()] = 1;
            }
            catch (NoSuchFieldError var2)
            {
            }

            try
            {
                field_151437_a[MovingObjectPosition.MovingObjectType.BLOCK.ordinal()] = 2;
            }
            catch (NoSuchFieldError var1)
            {
            }
        }
    }

}
