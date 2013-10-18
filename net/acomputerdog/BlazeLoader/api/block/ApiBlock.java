package net.acomputerdog.BlazeLoader.api.block;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Block;
import net.minecraft.src.WorldServer;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    @Beta(stable = true)
    /**
     * Gets an available block ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Block ID
     */
    public static int getFreeBlockId(){

        if(Block.blocksList[BlazeLoader.freeBlockId] == null){
            int id =  BlazeLoader.freeBlockId;
            BlazeLoader.freeBlockId++;
            return id;
        }
        else{
            int id =  BlazeLoader.updateFreeBlockId();
            BlazeLoader.freeBlockId++;
            return id;
        }
    }

    @Beta(stable = true)
    /**
     * Gets an available block ID, checking for used IDs that have been freed.
     * Throws a RuntimeException if none are available.
     * @return Returns a free Block ID.
     */
    public static int recheckBlockIds(){
        int id =  BlazeLoader.resetFreeBlockId();
        BlazeLoader.freeBlockId++;
        return id;
    }

    /**
     * Sets the block at a specified location.
     * @param world The world to change the block in.  Should be a dimension index returned by getDimensionIndex.
     * @param x The X-coordinate to change.
     * @param y The Y-coordinate to change.
     * @param z The Z-coordinate to change.
     * @param id The block ID to set.
     * @param metadata The block Metadata to set.
     * @param notifyFlag The notification flags.  Should be the value(s) of ENotificationType
     */
    public static void setBlock(int world, int x, int y, int z, int id, int metadata, int notifyFlag){
        getServerForDimension(world).setBlock(x, y, z, id, metadata, notifyFlag);
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
     * Gets the Block ID of a location.
     * @param world The world to get the ID from.
     * @param x The X-coordinate to get.
     * @param y The Y-coordinate to get.
     * @param z The Z-coordinate to get.
     * @return Return the block ID at the specified location.
     */
    public static int getBlockId(int world, int x, int y, int z){
        return getServerForDimension(world).getBlockId(x, y, z);
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
