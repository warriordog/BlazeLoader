package net.acomputerdog.BlazeLoader.api.block;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    /**
     * Sets the block at a specified location.
     * @param world The world to change the block in.  Should be a dimension index returned by getDimensionIndex.
     * @param x The X-coordinate to change.
     * @param y The Y-coordinate to change.
     * @param z The Z-coordinate to change.
     * @param block the block to set
     * @param metadata The block Metadata to set.
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlock(int world, int x, int y, int z, Block block, int metadata, int notifyFlag){
        setBlock(getServerForDimension(world), x, y, z, block, metadata, notifyFlag);
    }

    /**
     * Sets the block at a specified location.
     * @param world The world to change the block in..
     * @param x The X-coordinate to change.
     * @param y The Y-coordinate to change.
     * @param z The Z-coordinate to change.
     * @param block the block to set
     * @param metadata The block Metadata to set.
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlock(World world, int x, int y, int z, Block block, int metadata, int notifyFlag){
        world.func_147465_d(x, y, z, block, metadata, notifyFlag);
    }

    /**
     * Gets the IntegratedServer.worldServers[] index of the specified world.  As of MC1.6.2 the only possible values are -1, 0, and 1.
     * @param dimensionLevel The dimension to get the index of.
     * @return Return the index of the dimension.
     */
    public static int getDimensionIndex(int dimensionLevel){
        if(dimensionLevel == -1){
            return 1;
        }else if(dimensionLevel == 1){
            return 2;
        }else{
            return dimensionLevel;
        }
    }

    /**
     * Gets the world for the specified dimension.  Should be a dimension index returned by getDimensionIndex.
     * @param dimension The dimension to get.
     * @return The WorldServer for the specified index.
     */
    public static WorldServer getServerForDimension(int dimension){
        return ApiBase.theMinecraft.getIntegratedServer().worldServers[dimension];
    }

    /**
     * Gets the Block at a location.
     * @param world The world to get the block from.
     * @param x The X-coordinate to get.
     * @param y The Y-coordinate to get.
     * @param z The Z-coordinate to get.
     * @return Return the block at the specified location.
     */
    public static Block getBlock(int world, int x, int y, int z){
        return getServerForDimension(world).func_147439_a(x, y, z);
    }

    /**
     * Gets the Block at a location.
     * @param world The world to get the block from.
     * @param x The X-coordinate to get.
     * @param y The Y-coordinate to get.
     * @param z The Z-coordinate to get.
     * @return Return the block at the specified location.
     */
    public static Block getBlock(World world, int x, int y, int z){
        return world.func_147439_a(x, y, z);
    }

    /**
     * Gets the Block Metadata of a location.
     * @param world The world to get the Metadata from.
     * @param x The X-coordinate to get.
     * @param y The Y-coordinate to get.
     * @param z The Z-coordinate to get.
     * @return Return the block Metadata at the specified location.
     */
    public static int getBlockMetadata(int world, int x, int y, int z){
        return getServerForDimension(world).getBlockMetadata(x, y, z);
    }
}
