package net.acomputerdog.BlazeLoader.api.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.minecraft.world.World;

@Beta(stable = false) //Needs Testing
public class ApiWorld {

	private static List<IWorldGenerator> generators = new ArrayList<IWorldGenerator>();

	public static void registerWorldGenerator(IWorldGenerator generator) {
		generators.add(generator);
	}

	public static void generate(World world, int chunkX, int chunkZ) {
		Random random = new Random(world.getSeed());
		long seedX = random.nextLong() >> 2 + 1l;
		long seedZ = random.nextLong() >> 2 + 1l;
		long chunkSeed = (seedX * chunkX + seedZ * chunkZ) ^ world.getSeed();

		for (IWorldGenerator generator : generators) {
			random.setSeed(chunkSeed);
			generator.generateWorld(world, random, chunkX, chunkZ);
		}

	}
}