package com.blazeloader.api.block;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.util.version.Versions;
import com.mumfrey.liteloader.util.ModUtilities;

import net.acomputerdog.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    /**
     * Sets the block at a specified location.  And triggers a block update
     *
     * @param world The world to change the block in.
     * @param pos   The position of the block.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block) {
        world.setBlockState(pos, block, 3);
    }

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param pos        The position of the block.
     * @param block      the block to set
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block, int notifyFlag) {
        world.setBlockState(pos, block, notifyFlag);
    }

    /**
     * Sets the block at a specified location.  And triggers a block update
     *
     * @param world The world to change the block in.
     * @param x     The X-coordinate to change.
     * @param y     The Y-coordinate to change.
     * @param z     The Z-coordinate to change.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block) {
        setBlockAt(world, new BlockPos(x, y, z), block, 3);
    }

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param x          The X-coordinate to change.
     * @param y          The Y-coordinate to change.
     * @param z          The Z-coordinate to change.
     * @param block      the block to set
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block, int notifyFlag) {
        setBlockAt(world, new BlockPos(x, y, z), block, notifyFlag);
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     World
     * @param x         XCoordinate
     * @param y         YCoordinate
     * @param z         ZCoordinate
     * @param dropItems Block  will drop as an item if true
     */
    public static void destroyBlock(World world, int x, int y, int z, boolean dropItems) {
        destroyBlock(world, new BlockPos(x, y, z), dropItems);
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     World
     * @param pos       The position of the block.
     * @param dropItems Block  will drop as an item if true
     */
    public static void destroyBlock(World world, BlockPos pos, boolean dropItems) {
        if (!world.isRemote) {
            world.destroyBlock(pos, dropItems);
        }
    }

    /**
     * Play sound and particle effect for a block being broken without removing
     * the block
     *
     * @param w World
     * @param x XCoordinate
     * @param y YCoordinate
     * @param z ZCoordinate
     */
    public static void playBlockDestructionEffect(World w, int x, int y, int z) {
        playBlockDestructionEffect(w, new BlockPos(x, y, z));
    }

    /**
     * Play sound and particle effect for a block being broken without removing
     * the block
     *
     * @param w   World
     * @param pos The position of the block.
     */
    public static void playBlockDestructionEffect(World w, BlockPos pos) {
        if (!w.isRemote) {
            IBlockState b = w.getBlockState(pos);
            w.playAuxSFX(2001, pos, Block.getStateId(b));
        }
    }

    /**
     * Gets the IntegratedServer.worldServers[] index of the specified world. 
     *
     * @param dimensionLevel The dimension to get the index of.
     * @return Return the index of the dimension.
     */
    public static int getDimensionIndex(int dimensionLevel) {
        if (dimensionLevel == -1) return 1;
        if (dimensionLevel == 1) return 2;
        return dimensionLevel;
    }

    /**
     * Gets the world for the specified dimension.  Should be a dimension index returned by getDimensionIndex.
     *
     * @param dimension The dimension to get.
     * @return The WorldServer for the specified index.
     */
    public static WorldServer getServerForDimension(int dimension) {
    	WorldServer[] worldServers = null;
    	if (Versions.isClient()) {
	    	try {
	    		if (net.minecraft.client.Minecraft.getMinecraft().isSingleplayer()) {
	    			worldServers = net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().worldServers;
	    		}
	    	} catch (Exception e) {
	    		BLMain.LOGGER_FULL.logError("Exception in fetching worldservers for side CLIENT. Please submit a bug report to Blazeloader devs.", e);
	    	}
    	} else {
	    	try {
	    		worldServers = net.minecraft.server.MinecraftServer.getServer().worldServers;
	    	} catch (Throwable e) {
	    		BLMain.LOGGER_FULL.logError("Exception in fetching worldservers for side SERVER. Please submit a bug report to Blazeloader devs.", e);
	    	}
    	}
    	if (worldServers != null) {
    		dimension = getDimensionIndex(dimension);
    		if (dimension < 0 && dimension >= worldServers.length) {
    			dimension = 0;
    			BLMain.LOGGER_FULL.logWarning("Unsupported dimension index. Make sure you pass in the index of the dimension you want, not the dimension code.");
    		}
    		return worldServers[dimension];
    	}
        return null;
    }
    
    /**
     * Gets the state of the block found at the specified location.
     *
     * @param world The world to get the block from.
     * @param x     The X-coordinate to get.
     * @param y     The Y-coordinate to get.
     * @param z     The Z-coordinate to get.
     * @return Return the blockstate Mapping at the specified location.
     */
    public static IBlockState getBlockAt(World world, int x, int y, int z) {
        return getBlockAt(world, new BlockPos(x, y, z));
    }

    /**
     * Gets the state of the block found at the specified location.
     *
     * @param world The world to get the block from.
     * @param pos   The position of the block.
     * @return Return the blockstate Mapping at the specified location.
     */
    public static IBlockState getBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos);
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
     * @param mod	The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, String mod, String name, Block block) {
    	registerBlock(id, new ResourceLocation(mod, name), block);
    }
    
    /**
     * Registers a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, ResourceLocation name, Block block) {
    	ModUtilities.addBlock(id, name, block, true);
    	//Block.blockRegistry.register(id, name, block);
    }

    /**
     * Registers or replaces a TileEntity
     *
     * @param clazz Tile entity class
     * @param name  Entity name. Used as its id.
     */
    public static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        TileEntity.classToNameMap.put(clazz, name);
        TileEntity.nameToClassMap.put(name, clazz);
    }

    /**
     * Gets the name of a block.
     *
     * @param block The block to get the name for
     * @return Return a string of the name belonging to param block
     */
    public static String getBlockName(Block block) {
        return (String) Block.blockRegistry.getNameForObject(block);
    }

}
