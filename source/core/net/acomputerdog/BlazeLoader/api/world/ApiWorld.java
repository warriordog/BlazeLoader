package net.acomputerdog.BlazeLoader.api.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ApiFunctions related to worlds
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
     * Returns whether the current world gen allows for snow to form at the specified coordinates
     * and that the block below allows snow to be placed on top of it.
     * eg. Snow can't form on top of ice blocks.
     * @param w	World
     * @param x	XCoordinate
     * @param y YCoordinate
     * @param z ZCoordinate
     * @return True if snow can be placed here.
     */
    public static boolean canSnowAt(World w, int x, int y, int z) {
    	return canSnowAt(w, x, y, z, false);
    }
    
    /**
     * Returns whether the current world gen allows for snow to form at the specified coordinates
     * and that the block below allows snow to be placed on top of it.
     * eg. Snow can't form on top of ice blocks.
     * @param w	World
     * @param x	XCoordinate
     * @param y YCoordinate
     * @param z ZCoordinate
     * @param ignoreSurface	If true it will return true even if the block below is ice.
     * @return True if snow can be placed here.
     */
    public static boolean canSnowAt(World w, int x, int y, int z, boolean ignoreSurface) {
    	return w.func_147478_e(x, y, z, ignoreSurface);
    }
}