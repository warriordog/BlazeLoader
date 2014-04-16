package net.minecraft.client;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.acomputerdog.BlazeLoader.api.client.ApiKeyBinding;
import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.event.EventHandler;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.util.BLModContainer;
import net.acomputerdog.BlazeLoader.util.config.ConfigList;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;

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

@SideOnly(Side.CLIENT)
public class Minecraft implements IPlayerUsage {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.MACOS;
    /**
     * A 10MiB preallocation to ensure the heap is reasonably sized.
     */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private ServerData currentServerData;
    /**
     * The RenderEngine instance used by Minecraft
     */
    public TextureManager renderEngine;
    /**
     * Set to 'this' in Minecraft constructor; used by some settings get methods
     */
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean hasCrashed;
    /**
     * Instance of CrashReport.
     */
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    public Timer timer = new Timer(20.0F);
    /**
     * Instance of PlayerUsageSnooper.
     */
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getSystemTimeMillis());
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
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    private final Session session;
    private boolean isGamePaused;
    /**
     * The font renderer used for displaying and measuring text.
     */
    public FontRenderer fontRenderer;
    public FontRenderer standardGalacticFontRenderer;
    /**
     * The GuiScreen that's being displayed at the moment.
     */
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    /**
     * Mouse left click counter
     */
    private int leftClickCounter;
    /**
     * Display width
     */
    private int tempDisplayWidth;
    /**
     * Display height
     */
    private int tempDisplayHeight;
    /**
     * Instance of IntegratedServer.
     */
    private IntegratedServer theIntegratedServer;
    /**
     * Gui achievement
     */
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    /**
     * Skip render world
     */
    public boolean skipRenderWorld;
    /**
     * The ray trace hit that the mouse is over.
     */
    public MovingObjectPosition objectMouseOver;
    /**
     * The game settings that currently hold effect.
     */
    public GameSettings gameSettings;
    /**
     * Mouse helper instance.
     */
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
    /**
     * Join player counter
     */
    private int joinPlayerCounter;
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    /**
     * The profiler instance
     */
    public final Profiler mcProfiler = new Profiler();
    private long field_83002_am = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private List defaultResourcePacks = Lists.newArrayList();
    private DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
     */
    volatile boolean running = true;
    /**
     * String that shows the debug information
     */
    public String debug = "";
    /**
     * Approximate time (in ms) of last update to debug string
     */
    long debugUpdateTime = getSystemTime();
    /**
     * holds the current fps
     */
    int fpsCounter;
    long prevFrameTime = -1L;
    /**
     * Profiler currently displayed in the debug screen pie chart
     */
    private String debugProfilerName = "root";
    private static final String __OBFID = "CL_00000631";

    public Minecraft(Session par1Session, int par2, int par3, boolean par4, boolean par5, File par6File, File par7File, File par8File, Proxy par9Proxy, String par10Str) {
        theMinecraft = this;
        this.mcDataDir = par6File;
        this.fileAssets = par7File;
        this.fileResourcepacks = par8File;
        this.launchedVersion = par10Str;
        this.mcDefaultResourcePack = new DefaultResourcePack(this.fileAssets);
        this.addDefaultResourcePack();
        this.proxy = par9Proxy == null ? Proxy.NO_PROXY : par9Proxy;
        this.startTimerHackThread();
        this.session = par1Session;
        logger.info("Setting user: " + par1Session.getUsername());
        //logger.info("(Session ID is " + par1Session.getSessionID() + ")"); //don't print the session to the console.. that's stupid...
        this.isDemo = par5;
        this.displayWidth = par2;
        this.displayHeight = par3;
        this.tempDisplayWidth = par2;
        this.tempDisplayHeight = par3;
        this.fullscreen = par4;
        this.jvm64bit = isJvm64bit();
        ImageIO.setUseCache(false);
        Bootstrap.func_151354_b();
        ApiGeneral.theProfiler = mcProfiler;
        ApiGeneral.theMinecraft = this;
    }

    private static boolean isJvm64bit() {
        String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        int i = astring.length;

        for (String s : astring) {
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64")) {
                return true;
            }
        }

        return false;
    }

    public static void func_147105_a(String p_147105_0_) {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Class oclass = toolkit.getClass();

            if (oclass.getName().equals("sun.awt.X11.XToolkit")) {
                Field field = oclass.getDeclaredField("awtAppClassName");
                field.setAccessible(true);
                field.set(toolkit, p_147105_0_);
            }
        } catch (Exception ignored) {
        }
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            private static final String __OBFID = "CL_00000632";

            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport par1CrashReport) {
        this.hasCrashed = true;
        this.crashReporter = par1CrashReport;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport par1CrashReport) {
        File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        System.out.println(par1CrashReport.getCompleteReport());

        if (par1CrashReport.getFile() != null) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + par1CrashReport.getFile());
            System.exit(-1);
        } else if (par1CrashReport.saveToFile(file2)) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public void setServer(String par1Str, int par2) {
        this.serverName = par1Str;
        this.serverPort = par2;
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void startGame() throws LWJGLException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        gameSettings.keyBindings = ApiKeyBinding.getKeyBindings();
        gameSettings.loadOptions();

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        if (this.fullscreen) {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();

            if (this.displayWidth <= 0) {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
                this.displayHeight = 1;
            }
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setResizable(true);
        Display.setTitle("Minecraft 1.7.2");
        logger.info("LWJGL Version: " + Sys.getVersion());
        Util.EnumOS enumos = Util.getOSType();

        if (enumos != Util.EnumOS.MACOS) {
            try {
                Display.setIcon(new ByteBuffer[]{this.readImage(new File(this.fileAssets, "/icons/icon_16x16.png")), this.readImage(new File(this.fileAssets, "/icons/icon_32x32.png"))});
            } catch (IOException ioexception) {
                logger.error("Couldn\'t set icon", ioexception);
            }

            if (enumos != Util.EnumOS.WINDOWS) {
                func_147105_a("Minecraft");
            }
        }

        try {
            ForgeHooksClient.createDisplay();
        } catch (LWJGLException lwjglexception) {
            logger.error("Couldn\'t set pixel format", lwjglexception);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }

            if (this.fullscreen) {
                this.updateDisplayMode();
            }

            Display.create();
        }

        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
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
        FMLClientHandler.instance().beginMinecraftLoading(this, this.defaultResourcePacks, this.mcResourceManager);
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcMusicTicker = new MusicTicker(this);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.loadScreen();
        this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null) {
            this.fontRenderer.setUnicodeFlag(this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont);
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
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
            private static final String __OBFID = "CL_00000639";

            /**
             * Formats the strings based on 'IStatStringFormat' interface.
             */
            public String formatString(String par1Str) {
                try {
                    return String.format(par1Str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
                } catch (Exception exception) {
                    return "Error: " + exception.getLocalizedMessage();
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
        this.textureMapBlocks = new TextureMap(0, "textures/blocks");
        this.textureMapBlocks.setAnisotropicFiltering(this.gameSettings.anisotropicFiltering);
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        FMLClientHandler.instance().finishMinecraftLoading();
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngameForge(this);

        if (this.serverName != null) {
            FMLClientHandler.instance().connectToServerAtStartup(this.serverName, this.serverPort);
        } else {
            this.displayGuiScreen(new GuiMainMenu());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }

        FMLClientHandler.instance().onInitializationComplete();
        Display.setVSyncEnabled(this.gameSettings.enableVsync);
        ModList.start();
    }

    public void refreshResources() {
        ArrayList arraylist = Lists.newArrayList(this.defaultResourcePacks);

        for (Object o : this.mcResourcePackRepository.getRepositoryEntries()) {
            ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry) o;
            arraylist.add(entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.func_148530_e() != null) {
            arraylist.add(this.mcResourcePackRepository.func_148530_e());
        }

        this.mcLanguageManager.parseLanguageMetadata(arraylist);
        this.mcResourceManager.reloadResources(arraylist);

        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    private void addDefaultResourcePack() {
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    }

    private ByteBuffer readImage(File par1File) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(par1File);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int i = aint.length;

        for (int k : aint) {
            bytebuffer.putInt(k << 8 | k >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException {
        HashSet hashset = new HashSet();
        Collections.addAll(hashset, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();

        if (!hashset.contains(displaymode) && Util.getOSType() == Util.EnumOS.MACOS) {

            for (Object macDisplayMode : macDisplayModes) {
                DisplayMode displaymode1 = (DisplayMode) macDisplayMode;
                boolean flag = true;
                Iterator iterator1 = hashset.iterator();
                DisplayMode displaymode2;

                while (iterator1.hasNext()) {
                    displaymode2 = (DisplayMode) iterator1.next();

                    if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight()) {
                        flag = false;
                        break;
                    }
                }

                if (!flag) {
                    iterator1 = hashset.iterator();

                    while (iterator1.hasNext()) {
                        displaymode2 = (DisplayMode) iterator1.next();

                        if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() / 2 && displaymode2.getHeight() == displaymode1.getHeight() / 2) {
                            displaymode = displaymode2;
                            break;
                        }
                    }
                }
            }
        }

        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    /**
     * Displays a new screen.
     */
    private void loadScreen() throws LWJGLException {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.renderEngine.bindTexture(locationMojangPng);
        ScaledResolution scaledresolution = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
        int i = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) scaledresolution.getScaledWidth(), (double) scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.renderEngine.bindTexture(locationMojangPng);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(16777215);
        tessellator.addVertexWithUV(0.0D, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV((double) this.displayWidth, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV((double) this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.setColorOpaque_I(16777215);
        short short1 = 256;
        short short2 = 256;
        this.scaledTessellator((scaledresolution.getScaledWidth() - short1) / 2, (scaledresolution.getScaledHeight() - short2) / 2, 0, 0, short1, short2);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glFlush();
        this.func_147120_f();
    }

    /**
     * Loads Tessellator with a scaled resolution
     */
    public void scaledTessellator(int par1, int par2, int par3, int par4, int par5, int par6) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1), (double) (par2 + par6), 0.0D, (double) ((float) (par3) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), 0.0D, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2), 0.0D, (double) ((float) (par3 + par5) * f), (double) ((float) (par4) * f1));
        tessellator.addVertexWithUV((double) (par1), (double) (par2), 0.0D, (double) ((float) (par3) * f), (double) ((float) (par4) * f1));
        tessellator.draw();
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen p_147108_1_) {
        if (p_147108_1_ == null && this.theWorld == null) {
            p_147108_1_ = new GuiMainMenu();
        } else if (p_147108_1_ == null && this.thePlayer.getHealth() <= 0.0F) {
            p_147108_1_ = new GuiGameOver();
        }

        GuiScreen old = this.currentScreen;
        GuiOpenEvent event = new GuiOpenEvent(p_147108_1_);

        if (!EventHandler.eventOnGui(old, p_147108_1_, !MinecraftForge.EVENT_BUS.post(event))) return;

        p_147108_1_ = event.gui;

        if (old != null && p_147108_1_ != old) {
            old.onGuiClosed();
        }

        if (p_147108_1_ instanceof GuiModList) {
            try {
                Field modField = GuiModList.class.getDeclaredField("mods");
                modField.setAccessible(true);
                List<ModContainer> mods = (List) modField.get(p_147108_1_);
                mods.add(BLModContainer.instance);
            } catch (Exception e) {
                BlazeLoader.getLogger().logWarning("Could not inject into FML mod list!");
            }
        }

        if (p_147108_1_ instanceof GuiMainMenu) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = p_147108_1_;

        if (p_147108_1_ != null) {
            this.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            p_147108_1_.setWorldAndResolution(this, i, j);
            this.skipRenderWorld = false;
        } else {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String par1Str) {
        int i = GL11.glGetError();

        if (i != 0) {
            String s1 = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + par1Str);
            logger.error(i + ": " + s1);
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet() {
        try {
            logger.info("Stopping!");

            try {
                this.loadWorld(null);
            } catch (Throwable ignored) {
            }

            try {
                GLAllocation.deleteTexturesAndDisplayLists();
            } catch (Throwable ignored) {
            }

            this.mcSoundHandler.unloadSounds();
        } finally {
            Display.destroy();

            if (!this.hasCrashed) {
                System.exit(0);
            }
        }

        System.gc();
    }

    public void run() {
        BlazeLoader.init(new File(System.getProperty("user.dir")));
        this.running = true;
        CrashReport crashreport;

        try {
            this.startGame();
        } catch (Throwable throwable) {
            crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }

        while (true) {
            try {
                while (this.running) {
                    if (!this.hasCrashed || this.crashReporter == null) {
                        try {
                            this.runGameLoop();
                        } catch (OutOfMemoryError outofmemoryerror) {
                            this.freeMemory();
                            this.displayGuiScreen(new GuiMemoryErrorScreen());
                            System.gc();
                        }

                        continue;
                    }

                    this.displayCrashReport(this.crashReporter);
                    return;
                }
            } catch (MinecraftError ignored) {
            } catch (ReportedException reportedexception) {
                this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
                this.freeMemory();
                logger.fatal("Reported exception thrown!", reportedexception);
                this.displayCrashReport(reportedexception.getCrashReport());
            } catch (Throwable throwable1) {
                crashreport = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
                this.freeMemory();
                logger.fatal("Unreported exception thrown!", throwable1);
                this.displayCrashReport(crashreport);
            } finally {
                this.shutdownMinecraftApplet();
            }

            return;
        }
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop() {
        AxisAlignedBB.getAABBPool().cleanPool();

        if (this.theWorld != null) {
            this.theWorld.getWorldVec3Pool().clear();
        }

        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        } else {
            this.timer.updateTimer();
        }

        if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled) {
            this.refreshTexturePacksScheduled = false;
            this.refreshResources();
        }

        long j = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int i = 0; i < this.timer.elapsedTicks; ++i) {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long k = System.nanoTime() - j;
        this.checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld) {
            FMLCommonHandler.instance().onRenderTickStart(this.timer.renderPartialTicks);
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
            this.mcProfiler.endSection();
            FMLCommonHandler.instance().onRenderTickEnd(this.timer.renderPartialTicks);
        }

        GL11.glFlush();
        this.mcProfiler.endSection();

        if (!Display.isActive() && this.fullscreen) {
            this.toggleFullscreen();
        }

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(k);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.func_146254_a();
        this.framebufferMc.unbindFramebuffer();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GL11.glPopMatrix();
        this.mcProfiler.startSection("root");
        this.func_147120_f();
        Thread.yield();
        this.screenshotListener();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();

        while (getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning()) {
                this.usageSnooper.startSnooper();
            }
        }

        this.mcProfiler.endSection();

        if (this.isFramerateLimitBelowMax()) {
            Display.sync(this.getLimitFramerate());
        }
    }

    public void func_147120_f() {
        Display.update();

        if (!this.fullscreen && Display.wasResized()) {
            int i = this.displayWidth;
            int j = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != i || this.displayHeight != j) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        } catch (Throwable ignored) {
        }

        try {
            System.gc();
            AxisAlignedBB.getAABBPool().clearPool();
            this.theWorld.getWorldVec3Pool().clearAndFreeCache();
        } catch (Throwable ignored) {
        }

        try {
            System.gc();
            this.loadWorld(null);
        } catch (Throwable ignored) {
        }

        System.gc();
    }

    /**
     * checks if keys are down
     */
    private void screenshotListener() {
        if (this.gameSettings.keyBindScreenshot.isPressed()) {
            if (!this.isTakingScreenshot) {
                this.isTakingScreenshot = true;
                this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
            }
        } else {
            this.isTakingScreenshot = false;
        }
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int par1) {
        List list = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (list != null && !list.isEmpty()) {
            Profiler.Result result = (Profiler.Result) list.remove(0);

            if (par1 == 0) {
                if (result.field_76331_c.length() > 0) {
                    int j = this.debugProfilerName.lastIndexOf(".");

                    if (j >= 0) {
                        this.debugProfilerName = this.debugProfilerName.substring(0, j);
                    }
                }
            } else {
                --par1;

                if (par1 < list.size() && !((Profiler.Result) list.get(par1)).field_76331_c.equals("unspecified")) {
                    if (this.debugProfilerName.length() > 0) {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Profiler.Result) list.get(par1)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long par1) {
        if (this.mcProfiler.profilingEnabled) {
            List list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result result = (Profiler.Result) list.remove(0);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Tessellator tessellator = Tessellator.instance;
            short short1 = 160;
            int j = this.displayWidth - short1 - 10;
            int k = this.displayHeight - short1 * 2;
            GL11.glEnable(GL11.GL_BLEND);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 200);
            tessellator.addVertex((double) ((float) j - (float) short1 * 1.1F), (double) ((float) k - (float) short1 * 0.6F - 16.0F), 0.0D);
            tessellator.addVertex((double) ((float) j - (float) short1 * 1.1F), (double) (k + short1 * 2), 0.0D);
            tessellator.addVertex((double) ((float) j + (float) short1 * 1.1F), (double) (k + short1 * 2), 0.0D);
            tessellator.addVertex((double) ((float) j + (float) short1 * 1.1F), (double) ((float) k - (float) short1 * 0.6F - 16.0F), 0.0D);
            tessellator.draw();
            GL11.glDisable(GL11.GL_BLEND);
            double d0 = 0.0D;
            int i1;

            for (Object aList : list) {
                Profiler.Result result1 = (Profiler.Result) aList;
                i1 = MathHelper.floor_double(result1.field_76332_a / 4.0D) + 1;
                tessellator.startDrawing(6);
                tessellator.setColorOpaque_I(result1.func_76329_a());
                tessellator.addVertex((double) j, (double) k, 0.0D);
                int j1;
                float f;
                float f2;
                float f1;

                for (j1 = i1; j1 >= 0; --j1) {
                    f = (float) ((d0 + result1.field_76332_a * (double) j1 / (double) i1) * Math.PI * 2.0D / 100.0D);
                    f1 = MathHelper.sin(f) * (float) short1;
                    f2 = MathHelper.cos(f) * (float) short1 * 0.5F;
                    tessellator.addVertex((double) ((float) j + f1), (double) ((float) k - f2), 0.0D);
                }

                tessellator.draw();
                tessellator.startDrawing(5);
                tessellator.setColorOpaque_I((result1.func_76329_a() & 16711422) >> 1);

                for (j1 = i1; j1 >= 0; --j1) {
                    f = (float) ((d0 + result1.field_76332_a * (double) j1 / (double) i1) * Math.PI * 2.0D / 100.0D);
                    f1 = MathHelper.sin(f) * (float) short1;
                    f2 = MathHelper.cos(f) * (float) short1 * 0.5F;
                    tessellator.addVertex((double) ((float) j + f1), (double) ((float) k - f2), 0.0D);
                    tessellator.addVertex((double) ((float) j + f1), (double) ((float) k - f2 + 10.0F), 0.0D);
                }

                tessellator.draw();
                d0 += result1.field_76332_a;
            }

            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            String s = "";

            if (!result.field_76331_c.equals("unspecified")) {
                s = s + "[0] ";
            }

            if (result.field_76331_c.length() == 0) {
                s = s + "ROOT ";
            } else {
                s = s + result.field_76331_c + " ";
            }

            i1 = 16777215;
            this.fontRenderer.drawStringWithShadow(s, j - short1, k - short1 / 2 - 16, i1);
            this.fontRenderer.drawStringWithShadow(s = decimalformat.format(result.field_76330_b) + "%", j + short1 - this.fontRenderer.getStringWidth(s), k - short1 / 2 - 16, i1);

            for (int k1 = 0; k1 < list.size(); ++k1) {
                Profiler.Result result2 = (Profiler.Result) list.get(k1);
                String s1 = "";

                if (result2.field_76331_c.equals("unspecified")) {
                    s1 = s1 + "[?] ";
                } else {
                    s1 = s1 + "[" + (k1 + 1) + "] ";
                }

                s1 = s1 + result2.field_76331_c;
                this.fontRenderer.drawStringWithShadow(s1, j - short1, k + short1 / 2 + k1 * 8 + 20, result2.func_76329_a());
                this.fontRenderer.drawStringWithShadow(s1 = decimalformat.format(result2.field_76332_a) + "%", j + short1 - 50 - this.fontRenderer.getStringWidth(s1), k + short1 / 2 + k1 * 8 + 20, result2.func_76329_a());
                this.fontRenderer.drawStringWithShadow(s1 = decimalformat.format(result2.field_76330_b) + "%", j + short1 - this.fontRenderer.getStringWidth(s1), k + short1 / 2 + k1 * 8 + 20, result2.func_76329_a());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown() {
        this.running = false;
        ModList.stop();
        BlazeLoader.saveSettings();
        ConfigList.saveAllConfigs();
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus() {
        if (Display.isActive()) {
            if (!this.inGameHasFocus) {
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
    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void func_147115_a(boolean p_147115_1_) {
        if (!p_147115_1_) {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0) {
            if (p_147115_1_ && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int i = this.objectMouseOver.blockX;
                int j = this.objectMouseOver.blockY;
                int k = this.objectMouseOver.blockZ;

                if (this.theWorld.getBlock(i, j, k).getMaterial() != Material.air) {
                    this.playerController.onPlayerDamageBlock(i, j, k, this.objectMouseOver.sideHit);

                    if (this.thePlayer.isCurrentToolAdventureModeExempt(i, j, k)) {
                        this.effectRenderer.addBlockHitEffects(i, j, k, this.objectMouseOver);
                        this.thePlayer.swingItem();
                    }
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void func_147116_af() {
        if (this.leftClickCounter <= 0) {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null) {
                logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else {
                switch (Minecraft.SwitchMovingObjectType.field_151437_a[this.objectMouseOver.typeOfHit.ordinal()]) {
                    case 1:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;
                    case 2:
                        int i = this.objectMouseOver.blockX;
                        int j = this.objectMouseOver.blockY;
                        int k = this.objectMouseOver.blockZ;

                        if (this.theWorld.getBlock(i, j, k).getMaterial() == Material.air) {
                            if (this.playerController.isNotCreative()) {
                                this.leftClickCounter = 10;
                            }
                        } else {
                            this.playerController.clickBlock(i, j, k, this.objectMouseOver.sideHit);
                        }
                }
            }
        }
    }

    private void func_147121_ag() {
        this.rightClickDelayTimer = 4;
        boolean flag = true;
        ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();

        if (this.objectMouseOver == null) {
            logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
        } else {
            switch (Minecraft.SwitchMovingObjectType.field_151437_a[this.objectMouseOver.typeOfHit.ordinal()]) {
                case 1:
                    if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                        flag = false;
                    }

                    break;
                case 2:
                    int i = this.objectMouseOver.blockX;
                    int j = this.objectMouseOver.blockY;
                    int k = this.objectMouseOver.blockZ;

                    if (!this.theWorld.getBlock(i, j, k).isAir(theWorld, i, j, k)) {
                        int l = itemstack != null ? itemstack.stackSize : 0;

                        boolean result = !ForgeEventFactory.onPlayerInteract(thePlayer, Action.RIGHT_CLICK_BLOCK, i, j, k, this.objectMouseOver.sideHit).isCanceled();
                        if (result && this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, i, j, k, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                            flag = false;
                            this.thePlayer.swingItem();
                        }

                        if (itemstack == null) {
                            return;
                        }

                        if (itemstack.stackSize == 0) {
                            this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                        } else if (itemstack.stackSize != l || this.playerController.isInCreativeMode()) {
                            this.entityRenderer.itemRenderer.resetEquippedProgress();
                        }
                    }
            }
        }

        if (flag) {
            ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();

            boolean result = !ForgeEventFactory.onPlayerInteract(thePlayer, Action.RIGHT_CLICK_AIR, 0, 0, 0, -1).isCanceled();
            if (result && itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
                this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;

            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.func_147120_f();
        } catch (Exception exception) {
            logger.error("Couldn\'t toggle fullscreen", exception);
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int par1, int par2) {
        this.displayWidth = par1 <= 0 ? 1 : par1;
        this.displayHeight = par2 <= 0 ? 1 : par2;

        if (this.currentScreen != null) {
            ScaledResolution scaledresolution = new ScaledResolution(this.gameSettings, par1, par2);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            this.currentScreen.setWorldAndResolution(this, k, l);
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Runs the current tick.
     */
    public void runTick() {
        BlazeLoader.isInTick = true;
        BlazeLoader.numTicks++;
        if (this.rightClickDelayTimer > 0) {
            --this.rightClickDelayTimer;
        }

        FMLCommonHandler.instance().onPreClientTick();

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused) {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.endStartSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null) {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused) {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null) {
            if (this.thePlayer.getHealth() <= 0.0F) {
                this.displayGuiScreen(null);
            } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                this.displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
            this.displayGuiScreen(null);
        }

        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
        }

        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
            } catch (Throwable throwable1) {
                crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", new Callable() {
                    private static final String __OBFID = "CL_00000640";

                    public String call() {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    crashreportcategory = crashreport.makeCategory("Affected screen");
                    crashreportcategory.addCrashSectionCallable("Screen name", new Callable() {
                        private static final String __OBFID = "CL_00000642";

                        public String call() {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            this.mcProfiler.endStartSection("mouse");
            int i;

            while (Mouse.next()) {
                if (ForgeHooksClient.postMouseEvent()) continue;

                i = Mouse.getEventButton();

                if (isRunningOnMac && i == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
                    i = 1;
                }

                KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState()) {
                    KeyBinding.onTick(i - 100);
                }

                long k = getSystemTime() - this.systemTime;

                if (k <= 200L) {
                    int j = Mouse.getEventDWheel();

                    if (j != 0) {
                        this.thePlayer.inventory.changeCurrentItem(j);

                        if (this.gameSettings.noclip) {
                            if (j > 0) {
                                j = 1;
                            }

                            if (j < 0) {
                                j = -1;
                            }

                            this.gameSettings.noclipRate += (float) j * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                            this.setIngameFocus();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
                FMLCommonHandler.instance().fireMouseInput();
            }

            if (this.leftClickCounter > 0) {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");
            boolean flag;

            while (Keyboard.next()) {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (this.field_83002_am > 0L) {
                    if (getSystemTime() - this.field_83002_am >= 6000L) {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                        this.field_83002_am = -1L;
                    }
                } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                    this.field_83002_am = getSystemTime();
                }

                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 62 && this.entityRenderer != null) {
                        this.entityRenderer.deactivateShader();
                    }

                    if (Keyboard.getEventKey() == 87) {
                        this.toggleFullscreen();
                    } else {
                        if (this.currentScreen != null) {
                            this.currentScreen.handleKeyboardInput();
                        } else {
                            if (Keyboard.getEventKey() == 1) {
                                this.displayInGameMenu();
                            }

                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                                this.refreshResources();
                            }

                            if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                                this.refreshResources();
                            }

                            if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                                flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                                this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, flag ? -1 : 1);
                            }

                            if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                                this.renderGlobal.loadRenderers();
                            }

                            if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                                this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                                this.gameSettings.saveOptions();
                            }

                            if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                                RenderManager.debugBoundingBox = !RenderManager.debugBoundingBox;
                            }

                            if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                                this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                                this.gameSettings.saveOptions();
                            }

                            if (Keyboard.getEventKey() == 59) {
                                this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                            }

                            if (Keyboard.getEventKey() == 61) {
                                this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                                this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            }

                            if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                                ++this.gameSettings.thirdPersonView;

                                if (this.gameSettings.thirdPersonView > 2) {
                                    this.gameSettings.thirdPersonView = 0;
                                }
                            }

                            if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                                this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                            }
                        }

                        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                            if (Keyboard.getEventKey() == 11) {
                                this.updateDebugProfilerName(0);
                            }

                            for (i = 0; i < 9; ++i) {
                                if (Keyboard.getEventKey() == 2 + i) {
                                    this.updateDebugProfilerName(i + 1);
                                }
                            }
                        }
                    }
                    FMLCommonHandler.instance().fireKeyInput();
                }
            }
            
            EventHandler.eventKeyHeld();

            for (i = 0; i < 9; ++i) {
                if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
                    this.thePlayer.inventory.currentItem = i;
                }
            }

            flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.func_110738_j()) {
                    this.thePlayer.func_110322_i();
                } else {
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed()) {
                this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }

            while (this.gameSettings.keyBindChat.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem()) {
                if (!this.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                label391:

                while (true) {
                    if (!this.gameSettings.keyBindAttack.isPressed()) {
                        while (this.gameSettings.keyBindUseItem.isPressed()) {
                        }

                        while (true) {
                            if (this.gameSettings.keyBindPickBlock.isPressed()) {
                                continue;
                            }

                            break label391;
                        }
                    }
                }
            } else {
                while (this.gameSettings.keyBindAttack.isPressed()) {
                    this.func_147116_af();
                }

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    this.func_147121_ag();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    this.func_147112_ai();
                }
            }

            if (this.gameSettings.keyBindUseItem.getIsKeyPressed() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
                this.func_147121_ag();
            }

            this.func_147115_a(this.currentScreen == null && this.gameSettings.keyBindAttack.getIsKeyPressed() && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused) {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused) {
                if (this.theWorld.lastLightningBolt > 0) {
                    --this.theWorld.lastLightningBolt;
                }

                this.theWorld.updateEntities();
            }
        }

        if (!this.isGamePaused) {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.theWorld != null) {
            if (!this.isGamePaused) {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);

                try {
                    this.theWorld.tick();
                } catch (Throwable throwable2) {
                    crashreport = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.theWorld == null) {
                        crashreportcategory = crashreport.makeCategory("Affected level");
                        crashreportcategory.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.theWorld.addWorldInfoToCrashReport(crashreport);
                    }

                    throw new ReportedException(crashreport);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null) {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        FMLCommonHandler.instance().onPostClientTick();

        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();
        BlazeLoader.isInTick = false;
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String par1Str, String par2Str, WorldSettings par3WorldSettings) {
        FMLClientHandler.instance().startIntegratedServer(par1Str, par2Str, par3WorldSettings);
        this.loadWorld(null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(par1Str, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && par3WorldSettings != null) {
            worldinfo = new WorldInfo(par3WorldSettings, par1Str);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (par3WorldSettings == null) {
            par3WorldSettings = new WorldSettings(worldinfo);
        }

        try {
            this.theIntegratedServer = new IntegratedServer(this, par1Str, par2Str, par3WorldSettings);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", par1Str);
            crashreportcategory.addCrashSection("Level Name", par2Str);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displayProgressMessage(I18n.format("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String s2 = this.theIntegratedServer.getUserMessage();

            if (s2 != null) {
                this.loadingScreen.resetProgresAndWorkingMessage(I18n.format(s2));
            } else {
                this.loadingScreen.resetProgresAndWorkingMessage("");
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }

        this.displayGuiScreen(null);
        SocketAddress socketaddress = this.theIntegratedServer.func_147137_ag().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null));
        networkmanager.scheduleOutboundPacket(new C00Handshake(4, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.scheduleOutboundPacket(new C00PacketLoginStart(this.getSession().func_148256_e()));
        this.myNetworkManager = networkmanager;
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient par1WorldClient) {
        this.loadWorld(par1WorldClient, "");
    }

    /**
     * par2Str is displayed on the loading screen to the user unloads the current world first
     */
    public void loadWorld(WorldClient par1WorldClient, String par2Str) {
        if (theWorld != null) {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(theWorld));
        }

        if (par1WorldClient == null) {
            NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();

            if (nethandlerplayclient != null) {
                nethandlerplayclient.cleanup();
            }

            if (this.theIntegratedServer != null) {
                this.theIntegratedServer.initiateShutdown();
                if (loadingScreen != null) {
                    this.loadingScreen.resetProgresAndWorkingMessage("Shutting down internal server...");
                }
                while (!theIntegratedServer.isServerStopped()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            this.theIntegratedServer = null;
            this.guiAchievement.func_146257_b();
            this.entityRenderer.getMapItemRenderer().func_148249_a();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(par2Str);
            this.loadingScreen.resetProgresAndWorkingMessage("");
        }

        if (par1WorldClient == null && this.theWorld != null) {
            if (this.mcResourcePackRepository.func_148530_e() != null) {
                this.scheduleResourcesRefresh();
            }

            this.mcResourcePackRepository.func_148529_f();
            this.setServerData(null);
            this.integratedServerIsRunning = false;
            FMLClientHandler.instance().handleClientWorldClosing(this.theWorld);
        }

        this.mcSoundHandler.stopSounds();
        this.theWorld = par1WorldClient;

        if (par1WorldClient != null) {
            if (this.renderGlobal != null) {
                this.renderGlobal.setWorldAndLoadRenderers(par1WorldClient);
            }

            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(par1WorldClient);
            }

            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.func_147493_a(par1WorldClient, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            par1WorldClient.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    /**
     * A String of renderGlobal.getDebugInfoRenders
     */
    public String debugInfoRenders() {
        return this.renderGlobal.getDebugInfoRenders();
    }

    /**
     * Gets the information in the F3 menu about how many entities are infront/around you
     */
    public String getEntityDebug() {
        return this.renderGlobal.getDebugInfoEntities();
    }

    /**
     * Gets the name of the world's current chunk provider
     */
    public String getWorldProviderName() {
        return this.theWorld.getProviderName();
    }

    /**
     * A String of how many entities are in the world
     */
    public String debugInfoEntities() {
        return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
    }

    public void setDimensionAndSpawnPlayer(int par1) {
        this.theWorld.setSpawnLocation();
        this.theWorld.removeAllEntities();
        int j = 0;
        String s = null;

        if (this.thePlayer != null) {
            j = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            s = this.thePlayer.func_142021_k();
        }

        this.renderViewEntity = null;
        this.thePlayer = this.playerController.func_147493_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.dimension = par1;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.func_142020_c(s);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(j);
        this.playerController.setPlayerCapabilities(this.thePlayer);

        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen(null);
        }
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled() {
        /**
         * Set to 'this' in Minecraft constructor; used by some settings get methods
         */
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    private void func_147112_ai() {
        if (this.objectMouseOver != null) {
            boolean flag = this.thePlayer.capabilities.isCreativeMode;
            int j;

            if (!ForgeHooks.onPickBlock(this.objectMouseOver, this.thePlayer, this.theWorld)) return;
            // We delete this code wholly instead of commenting it out, to make sure we detect changes in it between MC versions
            if (flag) {
                j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + this.thePlayer.inventory.currentItem;
                this.playerController.sendSlotPacket(this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), j);
            }
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport par1CrashReport) {
        par1CrashReport.getCategory().addCrashSectionCallable("Launched Version", new Callable() {
            private static final String __OBFID = "CL_00000643";

            public String call() {
                return Minecraft.this.launchedVersion;
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("LWJGL", new Callable() {
            private static final String __OBFID = "CL_00000644";

            public String call() {
                return Sys.getVersion();
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("OpenGL", new Callable() {
            private static final String __OBFID = "CL_00000645";

            public String call() {
                return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Is Modded", new Callable() {
            private static final String __OBFID = "CL_00000646";

            public String call() {
                String s = ClientBrandRetriever.getClientModName();
                return !s.equals("vanilla") ? "Definitely; Client brand changed to \'" + s + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Type", new Callable() {
            private static final String __OBFID = "CL_00000647";

            public String call() {
                return "Client (map_client.txt)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Resource Packs", new Callable() {
            private static final String __OBFID = "CL_00000633";

            public String call() {
                return Minecraft.this.gameSettings.resourcePacks.toString();
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Current Language", new Callable() {
            private static final String __OBFID = "CL_00000634";

            public String call() {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Profiler Position", new Callable() {
            private static final String __OBFID = "CL_00000635";

            public String call() {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable() {
            private static final String __OBFID = "CL_00000636";

            public String call() {
                int i = Minecraft.this.theWorld.getWorldVec3Pool().getPoolSize();
                int j = 56 * i;
                int k = j / 1024 / 1024;
                int l = Minecraft.this.theWorld.getWorldVec3Pool().getNextFreeSpace();
                int i1 = 56 * l;
                int j1 = i1 / 1024 / 1024;
                return i + " (" + j + " bytes; " + k + " MB) allocated, " + l + " (" + i1 + " bytes; " + j1 + " MB) used";
            }
        });
        par1CrashReport.getCategory().addCrashSectionCallable("Anisotropic Filtering", new Callable() {
            private static final String __OBFID = "CL_00000637";

            public String call() {
                return Minecraft.this.gameSettings.anisotropicFiltering == 1 ? "Off (1)" : "On (" + Minecraft.this.gameSettings.anisotropicFiltering + ")";
            }
        });

        if (this.theWorld != null) {
            this.theWorld.addWorldInfoToCrashReport(par1CrashReport);
        }

        return par1CrashReport;
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getMinecraft() {
        /**
         * Set to 'this' in Minecraft constructor; used by some settings get methods
         */
        return theMinecraft;
    }

    public void scheduleResourcesRefresh() {
        this.refreshTexturePacksScheduled = true;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
        par1PlayerUsageSnooper.addData("fps", debugFPS);
        par1PlayerUsageSnooper.addData("vsync_enabled", this.gameSettings.enableVsync);
        par1PlayerUsageSnooper.addData("display_frequency", Display.getDisplayMode().getFrequency());
        par1PlayerUsageSnooper.addData("display_type", this.fullscreen ? "fullscreen" : "windowed");
        par1PlayerUsageSnooper.addData("run_time", (MinecraftServer.getSystemTimeMillis() - par1PlayerUsageSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        par1PlayerUsageSnooper.addData("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int i = 0;

        for (Object o : this.mcResourcePackRepository.getRepositoryEntries()) {
            ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry) o;
            par1PlayerUsageSnooper.addData("resource_pack[" + i++ + "]", entry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            par1PlayerUsageSnooper.addData("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper par1PlayerUsageSnooper) {
        par1PlayerUsageSnooper.addData("opengl_version", GL11.glGetString(GL11.GL_VERSION));
        par1PlayerUsageSnooper.addData("opengl_vendor", GL11.glGetString(GL11.GL_VENDOR));
        par1PlayerUsageSnooper.addData("client_brand", ClientBrandRetriever.getClientModName());
        par1PlayerUsageSnooper.addData("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        par1PlayerUsageSnooper.addData("gl_caps[ARB_multitexture]", contextcapabilities.GL_ARB_multitexture);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_multisample]", contextcapabilities.GL_ARB_multisample);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_texture_cube_map]", contextcapabilities.GL_ARB_texture_cube_map);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_blend]", contextcapabilities.GL_ARB_vertex_blend);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_matrix_palette]", contextcapabilities.GL_ARB_matrix_palette);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_program]", contextcapabilities.GL_ARB_vertex_program);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_shader]", contextcapabilities.GL_ARB_vertex_shader);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_fragment_program]", contextcapabilities.GL_ARB_fragment_program);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_fragment_shader]", contextcapabilities.GL_ARB_fragment_shader);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_shader_objects]", contextcapabilities.GL_ARB_shader_objects);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_vertex_buffer_object]", contextcapabilities.GL_ARB_vertex_buffer_object);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_framebuffer_object]", contextcapabilities.GL_ARB_framebuffer_object);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_pixel_buffer_object]", contextcapabilities.GL_ARB_pixel_buffer_object);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_uniform_buffer_object]", contextcapabilities.GL_ARB_uniform_buffer_object);
        par1PlayerUsageSnooper.addData("gl_caps[ARB_texture_non_power_of_two]", contextcapabilities.GL_ARB_texture_non_power_of_two);
        par1PlayerUsageSnooper.addData("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS));
        par1PlayerUsageSnooper.addData("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));
        par1PlayerUsageSnooper.addData("gl_max_texture_size", getGLMaximumTextureSize());
    }

    //Forge: Adds a optimization to the getGLMaximumTextureSize, only calculate it once.
    private static int max_texture_size = -1;

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize() {
        if (max_texture_size != -1) {
            return max_texture_size;
        }

        for (int i = 16384; i > 0; i >>= 1) {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            int j = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (j != 0) {
                max_texture_size = i;
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled() {
        return this.gameSettings.snooperEnabled;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData par1ServerData) {
        this.currentServerData = par1ServerData;
    }

    public ServerData func_147104_D() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() {
        if (theMinecraft != null) {
            IntegratedServer integratedserver = theMinecraft.getIntegratedServer();

            if (integratedserver != null) {
                integratedserver.stopServer();
            }
        }
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Session getSession() {
        return this.session;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType func_147109_W() {
        return this.currentScreen instanceof GuiWinGame ? MusicTicker.MusicType.CREDITS : (this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU);
    }

    public List<IResourcePack> getDefaultResourcePacks() {
        return this.defaultResourcePacks;
    }

    @SideOnly(Side.CLIENT)

    static final class SwitchMovingObjectType {
        static final int[] field_151437_a = new int[MovingObjectPosition.MovingObjectType.values().length];
        private static final String __OBFID = "CL_00000638";

        static {
            try {
                field_151437_a[MovingObjectPosition.MovingObjectType.ENTITY.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                field_151437_a[MovingObjectPosition.MovingObjectType.BLOCK.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }
        }
    }
}
