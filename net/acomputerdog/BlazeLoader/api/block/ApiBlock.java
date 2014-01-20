package net.acomputerdog.BlazeLoader.api.block;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.api.math.ApiMath;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
        return ApiBase.theMinecraft.getIntegratedServer().worldServers[dimension];
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
     * Gets a block by it's name or ID
     * @param identifier A string representing the name or ID of the block.
     * @return The block defined by parameter identifier
     */
    public static Block getBlockByNameOrId(String identifier) {
        return ApiMath.isInteger(identifier) ? getBlockById(Integer.parseInt(identifier)) : getBlockByName(identifier);
    }

    /**
     * Gets a block by it's name
     * @param name The name of the block
     * @return Gets the block defined by param name.
     */
    public static Block getBlockByName(String name) {
        return Block.getBlockFromName(name);
    }

    /**
     * Gets a block by it's BlockId.
     * @param id The ID of the block.
     * @return Return the block defined by param id.
     */
    public static Block getBlockById(int id) {
        return Block.getBlockById(id);
    }

    /**
     * Gets a block by it's item version.
     * @param item The item to get the block from.
     * @return Return the block associated with param item.
     */
    public static Block getBlockByItem(Item item) {
        return Block.getBlockFromItem(item);
    }

    /**
     * Registers a block in the block registry.
     * @param block The block to add
     * @param name The name to register the block as
     * @param id The ID of the block.
     */
    public static void registerBlock (Block block, String name, int id) {
        Block.blockRegistry.addObject(id, name, block);
    }

    /**
     * Gets the icon of a block.
     * @param block The block to get the icon from
     * @return Return the icon belonging to param block
     */
    public static IIcon getBlockIcon(Block block) {
        return block.blockIcon;
    }
}
