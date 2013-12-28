package net.acomputerdog.BlazeLoader.api.world;

import net.minecraft.world.World;

import java.util.Random;

/**
 * Represents a chunk generator.
 */
public interface IChunkGenerator {

	/**
	 * @param world The world we are generating in.
	 * @param random The chunk specific random.
	 * @param chunkX The X coordinate of the chunk being generated.
	 * @param chunkZ The Z coordinate of the chunk being generated.
	 */
	public void generateChunk(World world, Random random, int chunkX, int chunkZ);
}