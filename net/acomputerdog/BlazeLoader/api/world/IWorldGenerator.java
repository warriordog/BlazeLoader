package net.acomputerdog.BlazeLoader.api.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public interface IWorldGenerator {

	/**
	 * @param world The world we are generating in.
	 * @param random The chunk specific random.
	 * @param chunkX The X coordinate of the chunk being generated.
	 * @param chunkZ The Z coordinate of the chunk being generated.
	 */
	public void generateWorld(World world, Random random, int chunkX, int chunkZ);
}