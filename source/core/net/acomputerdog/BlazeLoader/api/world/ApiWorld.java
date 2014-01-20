package net.acomputerdog.BlazeLoader.api.world;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Beta(stable = false) //Needs Testing
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
}