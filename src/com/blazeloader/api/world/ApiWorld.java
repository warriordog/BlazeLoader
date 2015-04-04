package com.blazeloader.api.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.blazeloader.api.block.UpdateType;
import com.blazeloader.bl.main.BLMain;
import com.blazeloader.util.version.Versions;

/**
 * Api functions related to worlds
 *
 * NOT CURRENTLY IMPLEMENTED
 *
 */
public class ApiWorld {

    private static List<IChunkGenerator> generators = new ArrayList<IChunkGenerator>();

    /**
     * Registers a chunk generator
     *
     * @param generator The generator to register
     */
    public static void registerChunkGenerator(IChunkGenerator generator) {
        generators.add(generator);
    }

    /**
     * Generates a chunk at a specified location.
     *
     * @param world  The world to generate in
     * @param chunkX The x-location of the chunk to generate
     * @param chunkZ The z-location of the chunk to generate
     */
    public static void generateChunk(World world, int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long seedX = random.nextLong() >> 2 + 1l;
        long seedZ = random.nextLong() >> 2 + 1l;
        long chunkSeed = (seedX * chunkX + seedZ * chunkZ) ^ world.getSeed();

        for (IChunkGenerator generator : generators) {
            random.setSeed(chunkSeed);
            generator.generateChunk(world, random, chunkX, chunkZ);
        }

    }

    /**
     * Gets all entities in the world within a certain radius from a given point
     *
     * @param w   World
     * @param x   XCoordinate
     * @param y   YCoordinate
     * @param z   ZCoordinate
     * @param rad Maximum Radius
     * @return List of matching Entities
     */
    public static List<Entity> getEntitiesNear(World w, int x, int y, int z, int rad) {
        return getEntitiesOfTypeNear(w, null, x, y, z, rad);
    }

    /**
     * Gets entities of type in the world within a certain radius from a given point
     * Acts like getEntitiesNear() when Entity Type is null
     *
     * @param w   World
     * @param c   Entity Type
     * @param x   XCoordinate
     * @param y   YCoordinate
     * @param z   ZCoordinate
     * @param rad Maximum Radius
     * @return List of matching Entities
     */
    public static List<Entity> getEntitiesOfTypeNear(World w, Class<? extends Entity> c, int x, int y, int z, int rad) {
        List<Entity> result = new ArrayList<Entity>();
        for (Entity i : ((List<Entity>) w.loadedEntityList)) {
            if (c == null || i.getClass() == c) {
                if (i.posX >= x - rad && i.posX <= x + rad) {
                    if (i.posY >= y - rad && i.posY <= y + rad) {
                        if (i.posZ >= z - rad && i.posZ <= z + rad) result.add(i);
                    }
                }
            }
        }

        return result;
    }
    
    /**
     * Gets he closest player withing a certain distance relative to a given location.
     * 
     * @param w				World to look in
     * @param pos			BlockPos for the origin to look from
     * @param distance		Distance up to which we will search
     * @return	Closest player of null
     */
    public static EntityPlayer getClosestPlayer(World w, BlockPos pos, double distance) {
    	return w.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), distance);
    }
    

    /**
     * Sets the block at a specified location. And triggers a block update
     *
     * @param world The world to change the block in.
     * @param x     The X-coordinate to change.
     * @param y     The Y-coordinate to change.
     * @param z     The Z-coordinate to change.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block) {
        setBlockAt(world, new BlockPos(x, y, z), block);
    }
	
    /**
     * Sets the block at a specified location.  And triggers a block update
     *
     * @param world The world to change the block in.
     * @param pos   The position of the block.
     * @param block the block to set.
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block) {
    	setBlockAt(world, pos, block, UpdateType.UPDATE_AND_NOTIFY);
    }
    

    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param x          The X-coordinate to change.
     * @param y          The Y-coordinate to change.
     * @param z          The Z-coordinate to change.
     * @param block      the block to set
     * @param notifyFlag The notification flags.
     */
    public static void setBlockAt(World world, int x, int y, int z, IBlockState block, UpdateType notifyFlag) {
        setBlockAt(world, new BlockPos(x, y, z), block, notifyFlag);
    }
    
    /**
     * Sets the block at a specified location.
     *
     * @param world      The world to change the block in.
     * @param pos        The position of the block.
     * @param block      the block to set
     * @param notifyFlag The notification flags.
     */
    public static void setBlockAt(World world, BlockPos pos, IBlockState block, UpdateType notifyFlag) {
        world.setBlockState(pos, block, notifyFlag.getValue());
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     	the world containing the block
     * @param x         	XCoordinate
     * @param y         	YCoordinate
     * @param z         	ZCoordinate
     * @param dropItems		true if the block must drop as an item, or it's contained items
     */
    public static void destroyBlock(World world, int x, int y, int z, boolean dropItems) {
        destroyBlock(world, new BlockPos(x, y, z), dropItems);
    }

    /**
     * Destroys a block in the world creating sound and particle effects as if it were broken by a player.
     *
     * @param world     	the world containing the block
     * @param pos       	the position of the block.
     * @param dropItems		true if the block must drop as an item, or it's contained items
     */
    public static void destroyBlock(World world, BlockPos pos, boolean dropItems) {
        if (!world.isRemote) {
            world.destroyBlock(pos, dropItems);
        } else {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block.getMaterial() == Material.air) return;
            if (dropItems) block.dropBlockAsItem(world, pos, state, 0);
            setBlockAt(world, pos, Blocks.air.getDefaultState());
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
    
}