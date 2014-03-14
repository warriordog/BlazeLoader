package net.acomputerdog.BlazeLoader.api.block;

import java.util.Map;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.acomputerdog.BlazeLoader.util.math.MathUtils;
import net.acomputerdog.BlazeLoader.util.reflect.FieldInstance;
import net.acomputerdog.BlazeLoader.util.reflect.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.  Should be a dimension index returned by getDimensionIndex.
     * @param x          The X-coordinate to change.
     * @param y          The Y-coordinate to change.
     * @param z          The Z-coordinate to change.
     * @param block      the block to set
     * @param metadata   The block Metadata to set.
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlockAt(int world, int x, int y, int z, Block block, int metadata, int notifyFlag) {
        setBlockAt(getServerForDimension(world), x, y, z, block, metadata, notifyFlag);
    }

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in..
     * @param x          The X-coordinate to change.
     * @param y          The Y-coordinate to change.
     * @param z          The Z-coordinate to change.
     * @param block      the block to set
     * @param metadata   The block Metadata to set.
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlockAt(World world, int x, int y, int z, Block block, int metadata, int notifyFlag) {
        world.setBlock(x, y, z, block, metadata, notifyFlag);
    }

    /**
     * Gets the IntegratedServer.worldServers[] index of the specified world.  As of MC1.6.2 the only possible values are -1, 0, and 1.
     *
     * @param dimensionLevel The dimension to get the index of.
     * @return Return the index of the dimension.
     */
    public static int getDimensionIndex(int dimensionLevel) {
        if (dimensionLevel == -1) {
            return 1;
        } else if (dimensionLevel == 1) {
            return 2;
        } else {
            return dimensionLevel;
        }
    }

    /**
     * Gets the world for the specified dimension.  Should be a dimension index returned by getDimensionIndex.
     *
     * @param dimension The dimension to get.
     * @return The WorldServer for the specified index.
     */
    public static WorldServer getServerForDimension(int dimension) {
        return ApiGeneral.theMinecraft.getIntegratedServer().worldServers[dimension];
    }

    /**
     * Gets the Block at a location.
     *
     * @param world The world to get the block from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the block at the specified location.
     */
    public static Block getBlockAt(int world, int x, int y, int z) {
        return getServerForDimension(world).getBlock(x, y, z);
    }

    /**
     * Gets the Block at a location.
     *
     * @param world The world to get the block from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the block at the specified location.
     */
    public static Block getBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    /**
     * Gets the Block Metadata of a location.
     *
     * @param world The world to get the Metadata from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the block Metadata at the specified location.
     */
    public static int getBlockMetadataAt(int world, int x, int y, int z) {
        return getServerForDimension(world).getBlockMetadata(x, y, z);
    }

    /**
     * Gets the Block Metadata of a location.
     *
     * @param world The world to get the Metadata from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the block Metadata at the specified location.
     */
    public static int getBlockMetadataAt(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    /**
     * Gets a block by it's name or ID
     *
     * @param identifier A string representing the name or ID of the block.
     * @return The block defined by parameter identifier
     */
    public static Block getBlockByNameOrId(String identifier) {
        return MathUtils.isInteger(identifier) ? getBlockById(Integer.parseInt(identifier)) : getBlockByName(identifier);
    }

    /**
     * Gets a block by it's name
     *
     * @param name The name of the block
     * @return Gets the block defined by param name.
     */
    public static Block getBlockByName(String name) {
        return Block.getBlockFromName(name);
    }

    /**
     * Gets a block by it's BlockId.
     *
     * @param id The ID of the block.
     * @return Return the block defined by param id.
     */
    public static Block getBlockById(int id) {
        return Block.getBlockById(id);
    }

    /**
     * Gets a block by it's item version.
     *
     * @param item The item to get the block from.
     * @return Return the block associated with param item.
     */
    public static Block getBlockByItem(Item item) {
        return Block.getBlockFromItem(item);
    }

    /**
     * Registers a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, String name, Block block) {
        Block.blockRegistry.addObject(id, name, block);
    }
    
    /**
     * Registers or replaces a TileEntity
     * @param clazz		Tile entity class
     * @param name		Entity name. Used as its id.
     */
    public static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
    	if (TileEntity.nameToClassMap.containsKey(clazz)) {
            BlazeLoader.getLogger().logDetail("Registering duplicate id: " + name);
        }
    	
    	TileEntity.classToNameMap.put(clazz, name);
    	TileEntity.nameToClassMap.put(name, clazz);
    }

    /**
     * Gets the icon of a block.
     *
     * @param block The block to get the icon from
     * @return Return the icon belonging to param block
     */
    public static IIcon getBlockIcon(Block block) {
        return block.blockIcon;
    }

    /**
     * Gets the name of a block.
     *
     * @param block The block to get the name for
     * @return Return a string of the name belonging to param block
     */
	public static String getBlockName(Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
    public static void InitBlocks() {
    	Blocks.air = (Block)Block.blockRegistry.getObject("air");
    	Blocks.stone = (Block)Block.blockRegistry.getObject("stone");
    	Blocks.grass = (BlockGrass)Block.blockRegistry.getObject("grass");
    	Blocks.dirt = (Block)Block.blockRegistry.getObject("dirt");
    	Blocks.cobblestone = (Block)Block.blockRegistry.getObject("cobblestone");
    	Blocks.planks = (Block)Block.blockRegistry.getObject("planks");
    	Blocks.sapling = (Block)Block.blockRegistry.getObject("sapling");
    	Blocks.bedrock = (Block)Block.blockRegistry.getObject("bedrock");
    	Blocks.flowing_water = (BlockLiquid)Block.blockRegistry.getObject("flowing_water");
    	Blocks.water = (Block)Block.blockRegistry.getObject("water");
    	Blocks.flowing_lava = (BlockLiquid)Block.blockRegistry.getObject("flowing_lava");
    	Blocks.lava = (Block)Block.blockRegistry.getObject("lava");
    	Blocks.sand = (Block)Block.blockRegistry.getObject("sand");
    	Blocks.gravel = (Block)Block.blockRegistry.getObject("gravel");
    	Blocks.gold_ore = (Block)Block.blockRegistry.getObject("gold_ore");
    	Blocks.iron_ore = (Block)Block.blockRegistry.getObject("iron_ore");
    	Blocks.coal_ore = (Block)Block.blockRegistry.getObject("coal_ore");
    	Blocks.log = (Block)Block.blockRegistry.getObject("log");
    	Blocks.log2 = (Block)Block.blockRegistry.getObject("log2");
    	Blocks.leaves = (BlockLeaves)Block.blockRegistry.getObject("leaves");
    	Blocks.leaves2 = (BlockLeaves)Block.blockRegistry.getObject("leaves2");
    	Blocks.sponge = (Block)Block.blockRegistry.getObject("sponge");
    	Blocks.glass = (Block)Block.blockRegistry.getObject("glass");
    	Blocks.lapis_ore = (Block)Block.blockRegistry.getObject("lapis_ore");
    	Blocks.lapis_block = (Block)Block.blockRegistry.getObject("lapis_block");
    	Blocks.dispenser = (Block)Block.blockRegistry.getObject("dispenser");
    	Blocks.sandstone = (Block)Block.blockRegistry.getObject("sandstone");
    	Blocks.noteblock = (Block)Block.blockRegistry.getObject("noteblock");
    	Blocks.bed = (Block)Block.blockRegistry.getObject("bed");
    	Blocks.golden_rail = (Block)Block.blockRegistry.getObject("golden_rail");
    	Blocks.detector_rail = (Block)Block.blockRegistry.getObject("detector_rail");
    	Blocks.sticky_piston = (BlockPistonBase)Block.blockRegistry.getObject("sticky_piston");
    	Blocks.web = (Block)Block.blockRegistry.getObject("web");
    	Blocks.tallgrass = (BlockTallGrass)Block.blockRegistry.getObject("tallgrass");
    	Blocks.deadbush = (BlockDeadBush)Block.blockRegistry.getObject("deadbush");
    	Blocks.piston = (BlockPistonBase)Block.blockRegistry.getObject("piston");
    	Blocks.piston_head = (BlockPistonExtension)Block.blockRegistry.getObject("piston_head");
    	Blocks.wool = (Block)Block.blockRegistry.getObject("wool");
    	Blocks.piston_extension = (BlockPistonMoving)Block.blockRegistry.getObject("piston_extension");
    	Blocks.yellow_flower = (BlockFlower)Block.blockRegistry.getObject("yellow_flower");
    	Blocks.red_flower = (BlockFlower)Block.blockRegistry.getObject("red_flower");
    	Blocks.brown_mushroom = (BlockBush)Block.blockRegistry.getObject("brown_mushroom");
    	Blocks.red_mushroom = (BlockBush)Block.blockRegistry.getObject("red_mushroom");
    	Blocks.gold_block = (Block)Block.blockRegistry.getObject("gold_block");
    	Blocks.iron_block = (Block)Block.blockRegistry.getObject("iron_block");
    	Blocks.double_stone_slab = (BlockSlab)Block.blockRegistry.getObject("double_stone_slab");
    	Blocks.stone_slab = (BlockSlab)Block.blockRegistry.getObject("stone_slab");
    	Blocks.brick_block = (Block)Block.blockRegistry.getObject("brick_block");
    	Blocks.tnt = (Block)Block.blockRegistry.getObject("tnt");
    	Blocks.bookshelf = (Block)Block.blockRegistry.getObject("bookshelf");
    	Blocks.mossy_cobblestone = (Block)Block.blockRegistry.getObject("mossy_cobblestone");
    	Blocks.obsidian = (Block)Block.blockRegistry.getObject("obsidian");
    	Blocks.torch = (Block)Block.blockRegistry.getObject("torch");
    	Blocks.fire = (BlockFire)Block.blockRegistry.getObject("fire");
    	Blocks.mob_spawner = (Block)Block.blockRegistry.getObject("mob_spawner");
    	Blocks.oak_stairs = (Block)Block.blockRegistry.getObject("oak_stairs");
    	Blocks.chest = (BlockChest)Block.blockRegistry.getObject("chest");
    	Blocks.redstone_wire = (BlockRedstoneWire)Block.blockRegistry.getObject("redstone_wire");
    	Blocks.diamond_ore = (Block)Block.blockRegistry.getObject("diamond_ore");
    	Blocks.diamond_block = (Block)Block.blockRegistry.getObject("diamond_block");
    	Blocks.crafting_table = (Block)Block.blockRegistry.getObject("crafting_table");
    	Blocks.wheat = (Block)Block.blockRegistry.getObject("wheat");
    	Blocks.farmland = (Block)Block.blockRegistry.getObject("farmland");
    	Blocks.furnace = (Block)Block.blockRegistry.getObject("furnace");
    	Blocks.lit_furnace = (Block)Block.blockRegistry.getObject("lit_furnace");
    	Blocks.standing_sign = (Block)Block.blockRegistry.getObject("standing_sign");
    	Blocks.wooden_door = (Block)Block.blockRegistry.getObject("wooden_door");
        Blocks.ladder = (Block)Block.blockRegistry.getObject("ladder");
        Blocks.rail = (Block)Block.blockRegistry.getObject("rail");
        Blocks.stone_stairs = (Block)Block.blockRegistry.getObject("stone_stairs");
        Blocks.wall_sign = (Block)Block.blockRegistry.getObject("wall_sign");
        Blocks.lever = (Block)Block.blockRegistry.getObject("lever");
        Blocks.stone_pressure_plate = (Block)Block.blockRegistry.getObject("stone_pressure_plate");
        Blocks.iron_door = (Block)Block.blockRegistry.getObject("iron_door");
        Blocks.wooden_pressure_plate = (Block)Block.blockRegistry.getObject("wooden_pressure_plate");
        Blocks.redstone_ore = (Block)Block.blockRegistry.getObject("redstone_ore");
        Blocks.lit_redstone_ore = (Block)Block.blockRegistry.getObject("lit_redstone_ore");
        Blocks.unlit_redstone_torch = (Block)Block.blockRegistry.getObject("unlit_redstone_torch");
        Blocks.redstone_torch = (Block)Block.blockRegistry.getObject("redstone_torch");
        Blocks.stone_button = (Block)Block.blockRegistry.getObject("stone_button");
        Blocks.snow_layer = (Block)Block.blockRegistry.getObject("snow_layer");
        Blocks.ice = (Block)Block.blockRegistry.getObject("ice");
        Blocks.snow = (Block)Block.blockRegistry.getObject("snow");
        Blocks.cactus = (Block)Block.blockRegistry.getObject("cactus");
        Blocks.clay = (Block)Block.blockRegistry.getObject("clay");
        Blocks.reeds = (Block)Block.blockRegistry.getObject("reeds");
        Blocks.jukebox = (Block)Block.blockRegistry.getObject("jukebox");
        Blocks.fence = (Block)Block.blockRegistry.getObject("fence");
        Blocks.pumpkin = (Block)Block.blockRegistry.getObject("pumpkin");
        Blocks.netherrack = (Block)Block.blockRegistry.getObject("netherrack");
        Blocks.soul_sand = (Block)Block.blockRegistry.getObject("soul_sand");
        Blocks.glowstone = (Block)Block.blockRegistry.getObject("glowstone");
        Blocks.portal = (BlockPortal)Block.blockRegistry.getObject("portal");
        Blocks.lit_pumpkin = (Block)Block.blockRegistry.getObject("lit_pumpkin");
        Blocks.cake = (Block)Block.blockRegistry.getObject("cake");
        Blocks.unpowered_repeater = (BlockRedstoneRepeater)Block.blockRegistry.getObject("unpowered_repeater");
        Blocks.powered_repeater = (BlockRedstoneRepeater)Block.blockRegistry.getObject("powered_repeater");
        Blocks.trapdoor = (Block)Block.blockRegistry.getObject("trapdoor");
        Blocks.monster_egg = (Block)Block.blockRegistry.getObject("monster_egg");
        Blocks.stonebrick = (Block)Block.blockRegistry.getObject("stonebrick");
        Blocks.brown_mushroom_block = (Block)Block.blockRegistry.getObject("brown_mushroom_block");
        Blocks.red_mushroom_block = (Block)Block.blockRegistry.getObject("red_mushroom_block");
        Blocks.iron_bars = (Block)Block.blockRegistry.getObject("iron_bars");
        Blocks.glass_pane = (Block)Block.blockRegistry.getObject("glass_pane");
        Blocks.melon_block = (Block)Block.blockRegistry.getObject("melon_block");
        Blocks.pumpkin_stem = (Block)Block.blockRegistry.getObject("pumpkin_stem");
        Blocks.melon_stem = (Block)Block.blockRegistry.getObject("melon_stem");
        Blocks.vine = (Block)Block.blockRegistry.getObject("vine");
        Blocks.fence_gate = (Block)Block.blockRegistry.getObject("fence_gate");
        Blocks.brick_stairs = (Block)Block.blockRegistry.getObject("brick_stairs");
        Blocks.stone_brick_stairs = (Block)Block.blockRegistry.getObject("stone_brick_stairs");
        Blocks.mycelium = (BlockMycelium)Block.blockRegistry.getObject("mycelium");
        Blocks.waterlily = (Block)Block.blockRegistry.getObject("waterlily");
        Blocks.nether_brick = (Block)Block.blockRegistry.getObject("nether_brick");
        Blocks.nether_brick_fence = (Block)Block.blockRegistry.getObject("nether_brick_fence");
        Blocks.nether_brick_stairs = (Block)Block.blockRegistry.getObject("nether_brick_stairs");
        Blocks.nether_wart = (Block)Block.blockRegistry.getObject("nether_wart");
        Blocks.enchanting_table = (Block)Block.blockRegistry.getObject("enchanting_table");
        Blocks.brewing_stand = (Block)Block.blockRegistry.getObject("brewing_stand");
        Blocks.cauldron = (BlockCauldron)Block.blockRegistry.getObject("cauldron");
        Blocks.end_portal = (Block)Block.blockRegistry.getObject("end_portal");
        Blocks.end_portal_frame = (Block)Block.blockRegistry.getObject("end_portal_frame");
        Blocks.end_stone = (Block)Block.blockRegistry.getObject("end_stone");
        Blocks.dragon_egg = (Block)Block.blockRegistry.getObject("dragon_egg");
        Blocks.redstone_lamp = (Block)Block.blockRegistry.getObject("redstone_lamp");
        Blocks.lit_redstone_lamp = (Block)Block.blockRegistry.getObject("lit_redstone_lamp");
        Blocks.double_wooden_slab = (BlockSlab)Block.blockRegistry.getObject("double_wooden_slab");
        Blocks.wooden_slab = (BlockSlab)Block.blockRegistry.getObject("wooden_slab");
        Blocks.cocoa = (Block)Block.blockRegistry.getObject("cocoa");
        Blocks.sandstone_stairs = (Block)Block.blockRegistry.getObject("sandstone_stairs");
        Blocks.emerald_ore = (Block)Block.blockRegistry.getObject("emerald_ore");
        Blocks.ender_chest = (Block)Block.blockRegistry.getObject("ender_chest");
        Blocks.tripwire_hook = (BlockTripWireHook)Block.blockRegistry.getObject("tripwire_hook");
        Blocks.tripwire = (Block)Block.blockRegistry.getObject("tripwire");
        Blocks.emerald_block = (Block)Block.blockRegistry.getObject("emerald_block");
        Blocks.spruce_stairs = (Block)Block.blockRegistry.getObject("spruce_stairs");
        Blocks.birch_stairs = (Block)Block.blockRegistry.getObject("birch_stairs");
        Blocks.jungle_stairs = (Block)Block.blockRegistry.getObject("jungle_stairs");
        Blocks.command_block = (Block)Block.blockRegistry.getObject("command_block");
        Blocks.beacon = (BlockBeacon)Block.blockRegistry.getObject("beacon");
        Blocks.cobblestone_wall = (Block)Block.blockRegistry.getObject("cobblestone_wall");
        Blocks.flower_pot = (Block)Block.blockRegistry.getObject("flower_pot");
        Blocks.carrots = (Block)Block.blockRegistry.getObject("carrots");
        Blocks.potatoes = (Block)Block.blockRegistry.getObject("potatoes");
        Blocks.wooden_button = (Block)Block.blockRegistry.getObject("wooden_button");
        Blocks.skull = (Block)Block.blockRegistry.getObject("skull");
        Blocks.anvil = (Block)Block.blockRegistry.getObject("anvil");
        Blocks.trapped_chest = (Block)Block.blockRegistry.getObject("trapped_chest");
        Blocks.light_weighted_pressure_plate = (Block)Block.blockRegistry.getObject("light_weighted_pressure_plate");
        Blocks.heavy_weighted_pressure_plate = (Block)Block.blockRegistry.getObject("heavy_weighted_pressure_plate");
        Blocks.unpowered_comparator = (BlockRedstoneComparator)Block.blockRegistry.getObject("unpowered_comparator");
        Blocks.powered_comparator = (BlockRedstoneComparator)Block.blockRegistry.getObject("powered_comparator");
        Blocks.daylight_detector = (BlockDaylightDetector)Block.blockRegistry.getObject("daylight_detector");
        Blocks.redstone_block = (Block)Block.blockRegistry.getObject("redstone_block");
        Blocks.quartz_ore = (Block)Block.blockRegistry.getObject("quartz_ore");
        Blocks.hopper = (BlockHopper)Block.blockRegistry.getObject("hopper");
        Blocks.quartz_block = (Block)Block.blockRegistry.getObject("quartz_block");
        Blocks.quartz_stairs = (Block)Block.blockRegistry.getObject("quartz_stairs");
        Blocks.activator_rail = (Block)Block.blockRegistry.getObject("activator_rail");
        Blocks.dropper = (Block)Block.blockRegistry.getObject("dropper");
        Blocks.stained_hardened_clay = (Block)Block.blockRegistry.getObject("stained_hardened_clay");
        Blocks.hay_block = (Block)Block.blockRegistry.getObject("hay_block");
        Blocks.carpet = (Block)Block.blockRegistry.getObject("carpet");
        Blocks.hardened_clay = (Block)Block.blockRegistry.getObject("hardened_clay");
        Blocks.coal_block = (Block)Block.blockRegistry.getObject("coal_block");
        Blocks.packed_ice = (Block)Block.blockRegistry.getObject("packed_ice");
        Blocks.acacia_stairs = (Block)Block.blockRegistry.getObject("acacia_stairs");
        Blocks.dark_oak_stairs = (Block)Block.blockRegistry.getObject("dark_oak_stairs");
        Blocks.double_plant = (BlockDoublePlant)Block.blockRegistry.getObject("double_plant");
        Blocks.stained_glass = (BlockStainedGlass)Block.blockRegistry.getObject("stained_glass");
        Blocks.stained_glass_pane = (BlockStainedGlassPane)Block.blockRegistry.getObject("stained_glass_pane");
    }
}
