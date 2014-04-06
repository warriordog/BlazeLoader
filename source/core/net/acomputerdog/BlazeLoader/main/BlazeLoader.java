package net.acomputerdog.BlazeLoader.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.main.command.CommandBL;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.mod.ModList;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.ELogLevel;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.command.CommandHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class of BlazeLoader.  Contains various internal fields and methods.
 */
public final class BlazeLoader {
    public static int currFreeEntityId = 1;
    public static boolean isInTick = false;
    public static long numTicks = 0;
    public static CommandHandler commandHandler = new CommandHandler();

    private static Settings settings = new Settings();
    private static CLogger logger = new CLogger("BlazeLoader", true, true, Settings.minimumLogLevel);
    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();
    private static File settingsFile;
    private static boolean settingsLoaded = false;
    private static boolean hasInit = false;
    private static HashMap<Class, Render> entityMap = null;

    public static Mod currActiveMod = null;

    public static void init(File mainDir) {
        ApiGeneral.theProfiler.startSection("BL_Init");
        if (hasInit) {
            throw new IllegalStateException("Attempted to load twice!");
        } else {
            hasInit = true;
        }
        try {
            ApiGeneral.theProfiler.startSection("SettingsAndFiles");
            logger.logInfo("BlazeLoader version " + Version.getMinecraftVersion() + "/" + Version.getStringVersion() + " is starting...");

            ApiGeneral.mainDir = mainDir;
            File apiDir = new File(mainDir, "/BL/");
            if (!apiDir.exists() && !apiDir.mkdir()) {
                logger.logError("Could not create main API directory!");
            }

            settingsFile = new File(apiDir, "BLConfig.json");
            if (!settingsFile.exists()) {
                logger.logWarning("Config file does not exist!  It will be created.");
                saveSettings();
            }
            loadSettings();
            saveSettings();
            ELogLevel level = ELogLevel.getByName(Settings.minimumLogLevelName);
            if (level != null) {
                Settings.minimumLogLevel = level;
            }

            if (Settings.useVersionMods) {
                Settings.modDir = "/versions/" + Version.getMinecraftVersion() + "/mods/";
                Settings.configDir = "/versions/" + Version.getMinecraftVersion() + "/config/";
            }
            ApiGeneral.modDir = new File(mainDir, Settings.modDir);
            if (!ApiGeneral.modDir.exists() || !ApiGeneral.modDir.isDirectory()) {
                logger.logWarning("Mods folder not found!  Creating new folder...");
                logger.logDetail(ApiGeneral.modDir.mkdirs() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
            }
            ApiGeneral.configDir = new File(mainDir, Settings.configDir);
            if (!ApiGeneral.configDir.exists() || !ApiGeneral.configDir.isDirectory()) {
                logger.logWarning("Config folder not found!  Creating new folder...");
                logger.logDetail(ApiGeneral.configDir.mkdirs() ? "Creating folder succeeded!" : "Creating folder failed! Check file permissions!");
            }

            new CommandBL();

            ApiGeneral.theProfiler.endStartSection("Mod Loading");
            try {
                logger.logInfo("Loading mods...");
                if (Settings.enableMods) {
                    loadMods();
                    ModList.load();
                    initBlocks();
                    initItems();
                } else {
                    logger.logDetail("Mods are disabled in config, skipping mod loading.");
                }
                logger.logInfo("Mods loaded with no issues.");
            } catch (Exception e) {
                logger.logError("Caught exception loading mods!");
                e.printStackTrace();
            }
            ApiGeneral.theProfiler.endSection();
        } catch (Exception e) {
            logger.logFatal("Exception occurred while starting BlazeLoader!");
            e.printStackTrace();
            shutdown(1);
        }
        ApiGeneral.theProfiler.endSection();
    }

    private static void loadMods() {
        if (Settings.loadModsFromClasspath) {
            logger.logDetail("Loading mods from: classpath");
            try {
                Enumeration<URL> roots = BlazeLoader.class.getClassLoader().getResources("");
                while (roots.hasMoreElements()) {
                    File path = new File(roots.nextElement().toURI());
                    ModLoader.loadMods(path, path);
                }
            } catch (Exception e) {
                logger.logError("Exception loading mods in jar!");
                e.printStackTrace();
            }
        }
        logger.logDetail("Loading mods from: " + ApiGeneral.modDir.getAbsolutePath());
        ModLoader.loadMods(ApiGeneral.modDir, ApiGeneral.modDir);
        logger.logInfo("Mod loading complete.");
    }

    public static CLogger getLogger() {
        return logger;
    }

    public static void loadSettings() {
        settingsLoaded = true;
        try {
            settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
            if (settings == null) {
                saveSettings();
            }
        } catch (FileNotFoundException e) {
            saveSettings();
        } catch (JsonParseException e) {
            logger.logWarning("Format error in settings file; reloading.");
            saveSettings();
        } catch (Exception e) {
            logger.logError("Error occurred reading settings!");
            e.printStackTrace();
        }
    }

    public static void saveSettings() {
        if (!settingsLoaded) {
            settingsLoaded = true;
            loadSettings();
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile)));
            gson.toJson(settings, writer);
            writer.close();
        } catch (IOException e) {
            logger.logError("Could not save settings!");
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void shutdown(int code) {
        try {
            Minecraft.getMinecraft().shutdown();
            Thread.currentThread().join(100);
        } catch (Exception ignored) {
        }
        System.exit(code);
    }


    public static HashMap<Class, Render> getEntityRenderMap() {
        if (entityMap == null) {
            for (Field f : RenderManager.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(f.getType())) {
                    try {
                        f.setAccessible(true);
                        entityMap = (HashMap<Class, Render>) f.get(RenderManager.instance);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not get entity map!", e);
                    }
                }
            }
            if (entityMap == null) {
                throw new RuntimeException("Could not find entity map!");
            }
        }
        return entityMap;
    }

    private static void initBlocks() {
        Blocks.air = (Block) Block.blockRegistry.getObject("air");
        Blocks.stone = (Block) Block.blockRegistry.getObject("stone");
        Blocks.grass = (BlockGrass) Block.blockRegistry.getObject("grass");
        Blocks.dirt = (Block) Block.blockRegistry.getObject("dirt");
        Blocks.cobblestone = (Block) Block.blockRegistry.getObject("cobblestone");
        Blocks.planks = (Block) Block.blockRegistry.getObject("planks");
        Blocks.sapling = (Block) Block.blockRegistry.getObject("sapling");
        Blocks.bedrock = (Block) Block.blockRegistry.getObject("bedrock");
        Blocks.flowing_water = (BlockLiquid) Block.blockRegistry.getObject("flowing_water");
        Blocks.water = (Block) Block.blockRegistry.getObject("water");
        Blocks.flowing_lava = (BlockLiquid) Block.blockRegistry.getObject("flowing_lava");
        Blocks.lava = (Block) Block.blockRegistry.getObject("lava");
        Blocks.sand = (Block) Block.blockRegistry.getObject("sand");
        Blocks.gravel = (Block) Block.blockRegistry.getObject("gravel");
        Blocks.gold_ore = (Block) Block.blockRegistry.getObject("gold_ore");
        Blocks.iron_ore = (Block) Block.blockRegistry.getObject("iron_ore");
        Blocks.coal_ore = (Block) Block.blockRegistry.getObject("coal_ore");
        Blocks.log = (Block) Block.blockRegistry.getObject("log");
        Blocks.log2 = (Block) Block.blockRegistry.getObject("log2");
        Blocks.leaves = (BlockLeaves) Block.blockRegistry.getObject("leaves");
        Blocks.leaves2 = (BlockLeaves) Block.blockRegistry.getObject("leaves2");
        Blocks.sponge = (Block) Block.blockRegistry.getObject("sponge");
        Blocks.glass = (Block) Block.blockRegistry.getObject("glass");
        Blocks.lapis_ore = (Block) Block.blockRegistry.getObject("lapis_ore");
        Blocks.lapis_block = (Block) Block.blockRegistry.getObject("lapis_block");
        Blocks.dispenser = (Block) Block.blockRegistry.getObject("dispenser");
        Blocks.sandstone = (Block) Block.blockRegistry.getObject("sandstone");
        Blocks.noteblock = (Block) Block.blockRegistry.getObject("noteblock");
        Blocks.bed = (Block) Block.blockRegistry.getObject("bed");
        Blocks.golden_rail = (Block) Block.blockRegistry.getObject("golden_rail");
        Blocks.detector_rail = (Block) Block.blockRegistry.getObject("detector_rail");
        Blocks.sticky_piston = (BlockPistonBase) Block.blockRegistry.getObject("sticky_piston");
        Blocks.web = (Block) Block.blockRegistry.getObject("web");
        Blocks.tallgrass = (BlockTallGrass) Block.blockRegistry.getObject("tallgrass");
        Blocks.deadbush = (BlockDeadBush) Block.blockRegistry.getObject("deadbush");
        Blocks.piston = (BlockPistonBase) Block.blockRegistry.getObject("piston");
        Blocks.piston_head = (BlockPistonExtension) Block.blockRegistry.getObject("piston_head");
        Blocks.wool = (Block) Block.blockRegistry.getObject("wool");
        Blocks.piston_extension = (BlockPistonMoving) Block.blockRegistry.getObject("piston_extension");
        Blocks.yellow_flower = (BlockFlower) Block.blockRegistry.getObject("yellow_flower");
        Blocks.red_flower = (BlockFlower) Block.blockRegistry.getObject("red_flower");
        Blocks.brown_mushroom = (BlockBush) Block.blockRegistry.getObject("brown_mushroom");
        Blocks.red_mushroom = (BlockBush) Block.blockRegistry.getObject("red_mushroom");
        Blocks.gold_block = (Block) Block.blockRegistry.getObject("gold_block");
        Blocks.iron_block = (Block) Block.blockRegistry.getObject("iron_block");
        Blocks.double_stone_slab = (BlockSlab) Block.blockRegistry.getObject("double_stone_slab");
        Blocks.stone_slab = (BlockSlab) Block.blockRegistry.getObject("stone_slab");
        Blocks.brick_block = (Block) Block.blockRegistry.getObject("brick_block");
        Blocks.tnt = (Block) Block.blockRegistry.getObject("tnt");
        Blocks.bookshelf = (Block) Block.blockRegistry.getObject("bookshelf");
        Blocks.mossy_cobblestone = (Block) Block.blockRegistry.getObject("mossy_cobblestone");
        Blocks.obsidian = (Block) Block.blockRegistry.getObject("obsidian");
        Blocks.torch = (Block) Block.blockRegistry.getObject("torch");
        Blocks.fire = (BlockFire) Block.blockRegistry.getObject("fire");
        Blocks.mob_spawner = (Block) Block.blockRegistry.getObject("mob_spawner");
        Blocks.oak_stairs = (Block) Block.blockRegistry.getObject("oak_stairs");
        Blocks.chest = (BlockChest) Block.blockRegistry.getObject("chest");
        Blocks.redstone_wire = (BlockRedstoneWire) Block.blockRegistry.getObject("redstone_wire");
        Blocks.diamond_ore = (Block) Block.blockRegistry.getObject("diamond_ore");
        Blocks.diamond_block = (Block) Block.blockRegistry.getObject("diamond_block");
        Blocks.crafting_table = (Block) Block.blockRegistry.getObject("crafting_table");
        Blocks.wheat = (Block) Block.blockRegistry.getObject("wheat");
        Blocks.farmland = (Block) Block.blockRegistry.getObject("farmland");
        Blocks.furnace = (Block) Block.blockRegistry.getObject("furnace");
        Blocks.lit_furnace = (Block) Block.blockRegistry.getObject("lit_furnace");
        Blocks.standing_sign = (Block) Block.blockRegistry.getObject("standing_sign");
        Blocks.wooden_door = (Block) Block.blockRegistry.getObject("wooden_door");
        Blocks.ladder = (Block) Block.blockRegistry.getObject("ladder");
        Blocks.rail = (Block) Block.blockRegistry.getObject("rail");
        Blocks.stone_stairs = (Block) Block.blockRegistry.getObject("stone_stairs");
        Blocks.wall_sign = (Block) Block.blockRegistry.getObject("wall_sign");
        Blocks.lever = (Block) Block.blockRegistry.getObject("lever");
        Blocks.stone_pressure_plate = (Block) Block.blockRegistry.getObject("stone_pressure_plate");
        Blocks.iron_door = (Block) Block.blockRegistry.getObject("iron_door");
        Blocks.wooden_pressure_plate = (Block) Block.blockRegistry.getObject("wooden_pressure_plate");
        Blocks.redstone_ore = (Block) Block.blockRegistry.getObject("redstone_ore");
        Blocks.lit_redstone_ore = (Block) Block.blockRegistry.getObject("lit_redstone_ore");
        Blocks.unlit_redstone_torch = (Block) Block.blockRegistry.getObject("unlit_redstone_torch");
        Blocks.redstone_torch = (Block) Block.blockRegistry.getObject("redstone_torch");
        Blocks.stone_button = (Block) Block.blockRegistry.getObject("stone_button");
        Blocks.snow_layer = (Block) Block.blockRegistry.getObject("snow_layer");
        Blocks.ice = (Block) Block.blockRegistry.getObject("ice");
        Blocks.snow = (Block) Block.blockRegistry.getObject("snow");
        Blocks.cactus = (Block) Block.blockRegistry.getObject("cactus");
        Blocks.clay = (Block) Block.blockRegistry.getObject("clay");
        Blocks.reeds = (Block) Block.blockRegistry.getObject("reeds");
        Blocks.jukebox = (Block) Block.blockRegistry.getObject("jukebox");
        Blocks.fence = (Block) Block.blockRegistry.getObject("fence");
        Blocks.pumpkin = (Block) Block.blockRegistry.getObject("pumpkin");
        Blocks.netherrack = (Block) Block.blockRegistry.getObject("netherrack");
        Blocks.soul_sand = (Block) Block.blockRegistry.getObject("soul_sand");
        Blocks.glowstone = (Block) Block.blockRegistry.getObject("glowstone");
        Blocks.portal = (BlockPortal) Block.blockRegistry.getObject("portal");
        Blocks.lit_pumpkin = (Block) Block.blockRegistry.getObject("lit_pumpkin");
        Blocks.cake = (Block) Block.blockRegistry.getObject("cake");
        Blocks.unpowered_repeater = (BlockRedstoneRepeater) Block.blockRegistry.getObject("unpowered_repeater");
        Blocks.powered_repeater = (BlockRedstoneRepeater) Block.blockRegistry.getObject("powered_repeater");
        Blocks.trapdoor = (Block) Block.blockRegistry.getObject("trapdoor");
        Blocks.monster_egg = (Block) Block.blockRegistry.getObject("monster_egg");
        Blocks.stonebrick = (Block) Block.blockRegistry.getObject("stonebrick");
        Blocks.brown_mushroom_block = (Block) Block.blockRegistry.getObject("brown_mushroom_block");
        Blocks.red_mushroom_block = (Block) Block.blockRegistry.getObject("red_mushroom_block");
        Blocks.iron_bars = (Block) Block.blockRegistry.getObject("iron_bars");
        Blocks.glass_pane = (Block) Block.blockRegistry.getObject("glass_pane");
        Blocks.melon_block = (Block) Block.blockRegistry.getObject("melon_block");
        Blocks.pumpkin_stem = (Block) Block.blockRegistry.getObject("pumpkin_stem");
        Blocks.melon_stem = (Block) Block.blockRegistry.getObject("melon_stem");
        Blocks.vine = (Block) Block.blockRegistry.getObject("vine");
        Blocks.fence_gate = (Block) Block.blockRegistry.getObject("fence_gate");
        Blocks.brick_stairs = (Block) Block.blockRegistry.getObject("brick_stairs");
        Blocks.stone_brick_stairs = (Block) Block.blockRegistry.getObject("stone_brick_stairs");
        Blocks.mycelium = (BlockMycelium) Block.blockRegistry.getObject("mycelium");
        Blocks.waterlily = (Block) Block.blockRegistry.getObject("waterlily");
        Blocks.nether_brick = (Block) Block.blockRegistry.getObject("nether_brick");
        Blocks.nether_brick_fence = (Block) Block.blockRegistry.getObject("nether_brick_fence");
        Blocks.nether_brick_stairs = (Block) Block.blockRegistry.getObject("nether_brick_stairs");
        Blocks.nether_wart = (Block) Block.blockRegistry.getObject("nether_wart");
        Blocks.enchanting_table = (Block) Block.blockRegistry.getObject("enchanting_table");
        Blocks.brewing_stand = (Block) Block.blockRegistry.getObject("brewing_stand");
        Blocks.cauldron = (BlockCauldron) Block.blockRegistry.getObject("cauldron");
        Blocks.end_portal = (Block) Block.blockRegistry.getObject("end_portal");
        Blocks.end_portal_frame = (Block) Block.blockRegistry.getObject("end_portal_frame");
        Blocks.end_stone = (Block) Block.blockRegistry.getObject("end_stone");
        Blocks.dragon_egg = (Block) Block.blockRegistry.getObject("dragon_egg");
        Blocks.redstone_lamp = (Block) Block.blockRegistry.getObject("redstone_lamp");
        Blocks.lit_redstone_lamp = (Block) Block.blockRegistry.getObject("lit_redstone_lamp");
        Blocks.double_wooden_slab = (BlockSlab) Block.blockRegistry.getObject("double_wooden_slab");
        Blocks.wooden_slab = (BlockSlab) Block.blockRegistry.getObject("wooden_slab");
        Blocks.cocoa = (Block) Block.blockRegistry.getObject("cocoa");
        Blocks.sandstone_stairs = (Block) Block.blockRegistry.getObject("sandstone_stairs");
        Blocks.emerald_ore = (Block) Block.blockRegistry.getObject("emerald_ore");
        Blocks.ender_chest = (Block) Block.blockRegistry.getObject("ender_chest");
        Blocks.tripwire_hook = (BlockTripWireHook) Block.blockRegistry.getObject("tripwire_hook");
        Blocks.tripwire = (Block) Block.blockRegistry.getObject("tripwire");
        Blocks.emerald_block = (Block) Block.blockRegistry.getObject("emerald_block");
        Blocks.spruce_stairs = (Block) Block.blockRegistry.getObject("spruce_stairs");
        Blocks.birch_stairs = (Block) Block.blockRegistry.getObject("birch_stairs");
        Blocks.jungle_stairs = (Block) Block.blockRegistry.getObject("jungle_stairs");
        Blocks.command_block = (Block) Block.blockRegistry.getObject("command_block");
        Blocks.beacon = (BlockBeacon) Block.blockRegistry.getObject("beacon");
        Blocks.cobblestone_wall = (Block) Block.blockRegistry.getObject("cobblestone_wall");
        Blocks.flower_pot = (Block) Block.blockRegistry.getObject("flower_pot");
        Blocks.carrots = (Block) Block.blockRegistry.getObject("carrots");
        Blocks.potatoes = (Block) Block.blockRegistry.getObject("potatoes");
        Blocks.wooden_button = (Block) Block.blockRegistry.getObject("wooden_button");
        Blocks.skull = (Block) Block.blockRegistry.getObject("skull");
        Blocks.anvil = (Block) Block.blockRegistry.getObject("anvil");
        Blocks.trapped_chest = (Block) Block.blockRegistry.getObject("trapped_chest");
        Blocks.light_weighted_pressure_plate = (Block) Block.blockRegistry.getObject("light_weighted_pressure_plate");
        Blocks.heavy_weighted_pressure_plate = (Block) Block.blockRegistry.getObject("heavy_weighted_pressure_plate");
        Blocks.unpowered_comparator = (BlockRedstoneComparator) Block.blockRegistry.getObject("unpowered_comparator");
        Blocks.powered_comparator = (BlockRedstoneComparator) Block.blockRegistry.getObject("powered_comparator");
        Blocks.daylight_detector = (BlockDaylightDetector) Block.blockRegistry.getObject("daylight_detector");
        Blocks.redstone_block = (Block) Block.blockRegistry.getObject("redstone_block");
        Blocks.quartz_ore = (Block) Block.blockRegistry.getObject("quartz_ore");
        Blocks.hopper = (BlockHopper) Block.blockRegistry.getObject("hopper");
        Blocks.quartz_block = (Block) Block.blockRegistry.getObject("quartz_block");
        Blocks.quartz_stairs = (Block) Block.blockRegistry.getObject("quartz_stairs");
        Blocks.activator_rail = (Block) Block.blockRegistry.getObject("activator_rail");
        Blocks.dropper = (Block) Block.blockRegistry.getObject("dropper");
        Blocks.stained_hardened_clay = (Block) Block.blockRegistry.getObject("stained_hardened_clay");
        Blocks.hay_block = (Block) Block.blockRegistry.getObject("hay_block");
        Blocks.carpet = (Block) Block.blockRegistry.getObject("carpet");
        Blocks.hardened_clay = (Block) Block.blockRegistry.getObject("hardened_clay");
        Blocks.coal_block = (Block) Block.blockRegistry.getObject("coal_block");
        Blocks.packed_ice = (Block) Block.blockRegistry.getObject("packed_ice");
        Blocks.acacia_stairs = (Block) Block.blockRegistry.getObject("acacia_stairs");
        Blocks.dark_oak_stairs = (Block) Block.blockRegistry.getObject("dark_oak_stairs");
        Blocks.double_plant = (BlockDoublePlant) Block.blockRegistry.getObject("double_plant");
        Blocks.stained_glass = (BlockStainedGlass) Block.blockRegistry.getObject("stained_glass");
        Blocks.stained_glass_pane = (BlockStainedGlassPane) Block.blockRegistry.getObject("stained_glass_pane");
    }

    private static void initItems() {
        Items.iron_shovel = (Item) Item.itemRegistry.getObject("iron_shovel");
        Items.iron_pickaxe = (Item) Item.itemRegistry.getObject("iron_pickaxe");
        Items.iron_axe = (Item) Item.itemRegistry.getObject("iron_axe");
        Items.flint_and_steel = (Item) Item.itemRegistry.getObject("flint_and_steel");
        Items.apple = (Item) Item.itemRegistry.getObject("apple");
        Items.bow = (ItemBow) Item.itemRegistry.getObject("bow");
        Items.arrow = (Item) Item.itemRegistry.getObject("arrow");
        Items.coal = (Item) Item.itemRegistry.getObject("coal");
        Items.diamond = (Item) Item.itemRegistry.getObject("diamond");
        Items.iron_ingot = (Item) Item.itemRegistry.getObject("iron_ingot");
        Items.gold_ingot = (Item) Item.itemRegistry.getObject("gold_ingot");
        Items.iron_sword = (Item) Item.itemRegistry.getObject("iron_sword");
        Items.wooden_sword = (Item) Item.itemRegistry.getObject("wooden_sword");
        Items.wooden_shovel = (Item) Item.itemRegistry.getObject("wooden_shovel");
        Items.wooden_pickaxe = (Item) Item.itemRegistry.getObject("wooden_pickaxe");
        Items.wooden_axe = (Item) Item.itemRegistry.getObject("wooden_axe");
        Items.stone_sword = (Item) Item.itemRegistry.getObject("stone_sword");
        Items.stone_shovel = (Item) Item.itemRegistry.getObject("stone_shovel");
        Items.stone_pickaxe = (Item) Item.itemRegistry.getObject("stone_pickaxe");
        Items.stone_axe = (Item) Item.itemRegistry.getObject("stone_axe");
        Items.diamond_sword = (Item) Item.itemRegistry.getObject("diamond_sword");
        Items.diamond_shovel = (Item) Item.itemRegistry.getObject("diamond_shovel");
        Items.diamond_pickaxe = (Item) Item.itemRegistry.getObject("diamond_pickaxe");
        Items.diamond_axe = (Item) Item.itemRegistry.getObject("diamond_axe");
        Items.stick = (Item) Item.itemRegistry.getObject("stick");
        Items.bowl = (Item) Item.itemRegistry.getObject("bowl");
        Items.mushroom_stew = (Item) Item.itemRegistry.getObject("mushroom_stew");
        Items.golden_sword = (Item) Item.itemRegistry.getObject("golden_sword");
        Items.golden_shovel = (Item) Item.itemRegistry.getObject("golden_shovel");
        Items.golden_pickaxe = (Item) Item.itemRegistry.getObject("golden_pickaxe");
        Items.golden_axe = (Item) Item.itemRegistry.getObject("golden_axe");
        Items.string = (Item) Item.itemRegistry.getObject("string");
        Items.feather = (Item) Item.itemRegistry.getObject("feather");
        Items.gunpowder = (Item) Item.itemRegistry.getObject("gunpowder");
        Items.wooden_hoe = (Item) Item.itemRegistry.getObject("wooden_hoe");
        Items.stone_hoe = (Item) Item.itemRegistry.getObject("stone_hoe");
        Items.iron_hoe = (Item) Item.itemRegistry.getObject("iron_hoe");
        Items.diamond_hoe = (Item) Item.itemRegistry.getObject("diamond_hoe");
        Items.golden_hoe = (Item) Item.itemRegistry.getObject("golden_hoe");
        Items.wheat_seeds = (Item) Item.itemRegistry.getObject("wheat_seeds");
        Items.wheat = (Item) Item.itemRegistry.getObject("wheat");
        Items.bread = (Item) Item.itemRegistry.getObject("bread");
        Items.leather_helmet = (ItemArmor) Item.itemRegistry.getObject("leather_helmet");
        Items.leather_chestplate = (ItemArmor) Item.itemRegistry.getObject("leather_chestplate");
        Items.leather_leggings = (ItemArmor) Item.itemRegistry.getObject("leather_leggings");
        Items.leather_boots = (ItemArmor) Item.itemRegistry.getObject("leather_boots");
        Items.chainmail_helmet = (ItemArmor) Item.itemRegistry.getObject("chainmail_helmet");
        Items.chainmail_chestplate = (ItemArmor) Item.itemRegistry.getObject("chainmail_chestplate");
        Items.chainmail_leggings = (ItemArmor) Item.itemRegistry.getObject("chainmail_leggings");
        Items.chainmail_boots = (ItemArmor) Item.itemRegistry.getObject("chainmail_boots");
        Items.iron_helmet = (ItemArmor) Item.itemRegistry.getObject("iron_helmet");
        Items.iron_chestplate = (ItemArmor) Item.itemRegistry.getObject("iron_chestplate");
        Items.iron_leggings = (ItemArmor) Item.itemRegistry.getObject("iron_leggings");
        Items.iron_boots = (ItemArmor) Item.itemRegistry.getObject("iron_boots");
        Items.diamond_helmet = (ItemArmor) Item.itemRegistry.getObject("diamond_helmet");
        Items.diamond_chestplate = (ItemArmor) Item.itemRegistry.getObject("diamond_chestplate");
        Items.diamond_leggings = (ItemArmor) Item.itemRegistry.getObject("diamond_leggings");
        Items.diamond_boots = (ItemArmor) Item.itemRegistry.getObject("diamond_boots");
        Items.golden_helmet = (ItemArmor) Item.itemRegistry.getObject("golden_helmet");
        Items.golden_chestplate = (ItemArmor) Item.itemRegistry.getObject("golden_chestplate");
        Items.golden_leggings = (ItemArmor) Item.itemRegistry.getObject("golden_leggings");
        Items.golden_boots = (ItemArmor) Item.itemRegistry.getObject("golden_boots");
        Items.flint = (Item) Item.itemRegistry.getObject("flint");
        Items.porkchop = (Item) Item.itemRegistry.getObject("porkchop");
        Items.cooked_porkchop = (Item) Item.itemRegistry.getObject("cooked_porkchop");
        Items.painting = (Item) Item.itemRegistry.getObject("painting");
        Items.golden_apple = (Item) Item.itemRegistry.getObject("golden_apple");
        Items.sign = (Item) Item.itemRegistry.getObject("sign");
        Items.wooden_door = (Item) Item.itemRegistry.getObject("wooden_door");
        Items.bucket = (Item) Item.itemRegistry.getObject("bucket");
        Items.water_bucket = (Item) Item.itemRegistry.getObject("water_bucket");
        Items.lava_bucket = (Item) Item.itemRegistry.getObject("lava_bucket");
        Items.minecart = (Item) Item.itemRegistry.getObject("minecart");
        Items.saddle = (Item) Item.itemRegistry.getObject("saddle");
        Items.iron_door = (Item) Item.itemRegistry.getObject("iron_door");
        Items.redstone = (Item) Item.itemRegistry.getObject("redstone");
        Items.snowball = (Item) Item.itemRegistry.getObject("snowball");
        Items.boat = (Item) Item.itemRegistry.getObject("boat");
        Items.leather = (Item) Item.itemRegistry.getObject("leather");
        Items.milk_bucket = (Item) Item.itemRegistry.getObject("milk_bucket");
        Items.brick = (Item) Item.itemRegistry.getObject("brick");
        Items.clay_ball = (Item) Item.itemRegistry.getObject("clay_ball");
        Items.reeds = (Item) Item.itemRegistry.getObject("reeds");
        Items.paper = (Item) Item.itemRegistry.getObject("paper");
        Items.book = (Item) Item.itemRegistry.getObject("book");
        Items.slime_ball = (Item) Item.itemRegistry.getObject("slime_ball");
        Items.chest_minecart = (Item) Item.itemRegistry.getObject("chest_minecart");
        Items.furnace_minecart = (Item) Item.itemRegistry.getObject("furnace_minecart");
        Items.egg = (Item) Item.itemRegistry.getObject("egg");
        Items.compass = (Item) Item.itemRegistry.getObject("compass");
        Items.fishing_rod = (ItemFishingRod) Item.itemRegistry.getObject("fishing_rod");
        Items.clock = (Item) Item.itemRegistry.getObject("clock");
        Items.glowstone_dust = (Item) Item.itemRegistry.getObject("glowstone_dust");
        Items.fish = (Item) Item.itemRegistry.getObject("fish");
        Items.cooked_fished = (Item) Item.itemRegistry.getObject("cooked_fished");
        Items.dye = (Item) Item.itemRegistry.getObject("dye");
        Items.bone = (Item) Item.itemRegistry.getObject("bone");
        Items.sugar = (Item) Item.itemRegistry.getObject("sugar");
        Items.cake = (Item) Item.itemRegistry.getObject("cake");
        Items.bed = (Item) Item.itemRegistry.getObject("bed");
        Items.repeater = (Item) Item.itemRegistry.getObject("repeater");
        Items.cookie = (Item) Item.itemRegistry.getObject("cookie");
        Items.filled_map = (ItemMap) Item.itemRegistry.getObject("filled_map");
        Items.shears = (ItemShears) Item.itemRegistry.getObject("shears");
        Items.melon = (Item) Item.itemRegistry.getObject("melon");
        Items.pumpkin_seeds = (Item) Item.itemRegistry.getObject("pumpkin_seeds");
        Items.melon_seeds = (Item) Item.itemRegistry.getObject("melon_seeds");
        Items.beef = (Item) Item.itemRegistry.getObject("beef");
        Items.cooked_beef = (Item) Item.itemRegistry.getObject("cooked_beef");
        Items.chicken = (Item) Item.itemRegistry.getObject("chicken");
        Items.cooked_chicken = (Item) Item.itemRegistry.getObject("cooked_chicken");
        Items.rotten_flesh = (Item) Item.itemRegistry.getObject("rotten_flesh");
        Items.ender_pearl = (Item) Item.itemRegistry.getObject("ender_pearl");
        Items.blaze_rod = (Item) Item.itemRegistry.getObject("blaze_rod");
        Items.ghast_tear = (Item) Item.itemRegistry.getObject("ghast_tear");
        Items.gold_nugget = (Item) Item.itemRegistry.getObject("gold_nugget");
        Items.nether_wart = (Item) Item.itemRegistry.getObject("nether_wart");
        Items.potionitem = (ItemPotion) Item.itemRegistry.getObject("potion");
        Items.glass_bottle = (Item) Item.itemRegistry.getObject("glass_bottle");
        Items.spider_eye = (Item) Item.itemRegistry.getObject("spider_eye");
        Items.fermented_spider_eye = (Item) Item.itemRegistry.getObject("fermented_spider_eye");
        Items.blaze_powder = (Item) Item.itemRegistry.getObject("blaze_powder");
        Items.magma_cream = (Item) Item.itemRegistry.getObject("magma_cream");
        Items.brewing_stand = (Item) Item.itemRegistry.getObject("brewing_stand");
        Items.cauldron = (Item) Item.itemRegistry.getObject("cauldron");
        Items.ender_eye = (Item) Item.itemRegistry.getObject("ender_eye");
        Items.speckled_melon = (Item) Item.itemRegistry.getObject("speckled_melon");
        Items.spawn_egg = (Item) Item.itemRegistry.getObject("spawn_egg");
        Items.experience_bottle = (Item) Item.itemRegistry.getObject("experience_bottle");
        Items.fire_charge = (Item) Item.itemRegistry.getObject("fire_charge");
        Items.writable_book = (Item) Item.itemRegistry.getObject("writable_book");
        Items.written_book = (Item) Item.itemRegistry.getObject("written_book");
        Items.emerald = (Item) Item.itemRegistry.getObject("emerald");
        Items.item_frame = (Item) Item.itemRegistry.getObject("item_frame");
        Items.flower_pot = (Item) Item.itemRegistry.getObject("flower_pot");
        Items.carrot = (Item) Item.itemRegistry.getObject("carrot");
        Items.potato = (Item) Item.itemRegistry.getObject("potato");
        Items.baked_potato = (Item) Item.itemRegistry.getObject("baked_potato");
        Items.poisonous_potato = (Item) Item.itemRegistry.getObject("poisonous_potato");
        Items.map = (ItemEmptyMap) Item.itemRegistry.getObject("map");
        Items.golden_carrot = (Item) Item.itemRegistry.getObject("golden_carrot");
        Items.skull = (Item) Item.itemRegistry.getObject("skull");
        Items.carrot_on_a_stick = (Item) Item.itemRegistry.getObject("carrot_on_a_stick");
        Items.nether_star = (Item) Item.itemRegistry.getObject("nether_star");
        Items.pumpkin_pie = (Item) Item.itemRegistry.getObject("pumpkin_pie");
        Items.fireworks = (Item) Item.itemRegistry.getObject("fireworks");
        Items.firework_charge = (Item) Item.itemRegistry.getObject("firework_charge");
        Items.enchanted_book = (ItemEnchantedBook) Item.itemRegistry.getObject("enchanted_book");
        Items.comparator = (Item) Item.itemRegistry.getObject("comparator");
        Items.netherbrick = (Item) Item.itemRegistry.getObject("netherbrick");
        Items.quartz = (Item) Item.itemRegistry.getObject("quartz");
        Items.tnt_minecart = (Item) Item.itemRegistry.getObject("tnt_minecart");
        Items.hopper_minecart = (Item) Item.itemRegistry.getObject("hopper_minecart");
        Items.iron_horse_armor = (Item) Item.itemRegistry.getObject("iron_horse_armor");
        Items.golden_horse_armor = (Item) Item.itemRegistry.getObject("golden_horse_armor");
        Items.diamond_horse_armor = (Item) Item.itemRegistry.getObject("diamond_horse_armor");
        Items.lead = (Item) Item.itemRegistry.getObject("lead");
        Items.name_tag = (Item) Item.itemRegistry.getObject("name_tag");
        Items.command_block_minecart = (Item) Item.itemRegistry.getObject("command_block_minecart");
        Items.record_13 = (Item) Item.itemRegistry.getObject("record_13");
        Items.record_cat = (Item) Item.itemRegistry.getObject("record_cat");
        Items.record_blocks = (Item) Item.itemRegistry.getObject("record_blocks");
        Items.record_chirp = (Item) Item.itemRegistry.getObject("record_chirp");
        Items.record_far = (Item) Item.itemRegistry.getObject("record_far");
        Items.record_mall = (Item) Item.itemRegistry.getObject("record_mall");
        Items.record_mellohi = (Item) Item.itemRegistry.getObject("record_mellohi");
        Items.record_stal = (Item) Item.itemRegistry.getObject("record_stal");
        Items.record_strad = (Item) Item.itemRegistry.getObject("record_strad");
        Items.record_ward = (Item) Item.itemRegistry.getObject("record_ward");
        Items.record_11 = (Item) Item.itemRegistry.getObject("record_11");
        Items.record_wait = (Item) Item.itemRegistry.getObject("record_wait");
    }
}
