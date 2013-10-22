package net.acomputerdog.BlazeLoader.api.block;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Block;
import net.minecraft.src.WorldServer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    /**
     * Gets an available block ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Block ID
     */
    public static int getFreeBlockId(){

        if(Block.blocksList[BlazeLoader.freeBlockId] == null){
            int id =  BlazeLoader.freeBlockId;
            BlazeLoader.freeBlockId++;
            return id;
        }else{
            int id =  BlazeLoader.updateFreeBlockId();
            BlazeLoader.freeBlockId++;
            return id;
        }
    }

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

    @Beta(stable = true)
    /**
     * Overrides and existing block as well as any other fields referencing it.
     * Eg:  If overriding the block with ID 1, Block.blockStone will also be replaces.
     * @param block The block class to create the block from.
     * @param blockID The ID of the new block.
     * @param blockArgs Arguments to pass to the constructor of the new block.
     */
    public static void overrideBlock(Class<? extends Block> block, int blockID, Object[] blockArgs){
        Block oldBlock = Block.blocksList[blockID];
        List<Field> newBlocks = new ArrayList<Field>();
        if(oldBlock != null){
            for(Field f : Block.class.getDeclaredFields()){
                try{
                    f.setAccessible(true);
                    if(f.get(null) == oldBlock){
                        newBlocks.add(f);
                    }
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Could not get block field!", e);
                }
            }
            Block.blocksList[blockID] = null;
        }
        Block blockInstance = null;
        for(Constructor c : block.getDeclaredConstructors()){
            if(c.getParameterTypes().length == blockArgs.length){
                try{
                    c.setAccessible(true);
                    blockInstance = (Block)c.newInstance(blockArgs);
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Could not create new block!", e);
                }
            }
        }
        for(Field f : newBlocks){
            try{
                f.setAccessible(true);
                int modifiers = f.getModifiers();
                if(Modifier.isFinal(modifiers)){
                    Field theModifiers = Field.class.getDeclaredField("modifiers");
                    theModifiers.setAccessible(true);
                    theModifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                }
                f.set(null, blockInstance);
            }catch(ReflectiveOperationException e){
                throw new RuntimeException("Could not replace block field!", e);
            }
        }
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
