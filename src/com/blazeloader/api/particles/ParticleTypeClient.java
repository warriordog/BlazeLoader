package com.blazeloader.api.particles;

import java.util.ArrayList;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;

public class ParticleTypeClient implements IParticle {
	
	private final String particleName;
	private final int argCount;
	
	private IParticleFactory particleFactory;
	
	private int particleId;
	
	private boolean ignoreDist;
	private int maxParticleDistance = 255;
	
	public ParticleTypeClient(String name, boolean ignoreDistance, int argumentCount) {
		particleName = name;
		argCount = argumentCount;
		setIgnoreDistance(ignoreDistance);
	}
	
	protected final ParticleTypeClient setFactory(IParticleFactory factory) {
		if (particleFactory == null) {
			particleFactory = factory;
		}
		return this;
	}
	
	/**
	 * Gets the factory used to spawn this particle.
	 * 
	 * @return IParticleFactory
	 */
	public IParticleFactory getFactory() {
		return particleFactory;
	}
	
	/**
	 * Set whether this particle will always spawn regardless of how far it is from the camera.
	 * 
	 * @param val	distance
	 */
	public void setIgnoreDistance(boolean val) {
		ignoreDist = val;
	}
	
	/**
	 * Whether this particle will always spawn regardless of how far it is from the camera.
	 * 
	 * @returns val	distance
	 */
	public boolean getIgnoreDistance() {
		return ignoreDist;
	}
	
	/**
	 * Set maximum distance away from the camera this particle can be spawned.
	 * 
	 * Ignored if getIgnoreDistance() is true.
	 * 
	 * @param dist	distance in blocks
	 */
	public void setMaxDistance(int dist) {
		maxParticleDistance = dist;
	}
	
	/**
	 * Maximum distance away from the camera this particle can be spawned.
	 * 
	 * Ignored if getIgnoreDistance() is true.
	 * 
	 * @returns distance in blocks
	 * @default 255
	 */
	public int getMaxDistance() {
		return maxParticleDistance;
	}
	
	/**
	 * The id for this particle.
	 * 
	 * This particle will be allocated an Id at runtime.
	 * 
	 * @return int id
	 */
	public final int getId() {
		return particleId;
	}
	
	/**
	 * This particle's name.
	 * 
	 * @return String name
	 */
	public final String getName() {
		return particleName;
	}
	
	public final ParticleTypeClient setId(int id) {
		particleId = id;
		return this;
	}
	
	/**
	 * Returns true if this particle takes any extra arguments.
	 * 
	 * @return true of argsCound > 0
	 */
	public boolean hasArguments() {
		return argCount > 0;
	}
	
	/**
	 * Returns the total extra arguents this particle accepts.
	 * 
	 * @return int args
	 */
	public int getArgumentCount() {
		return argCount;
	}
}
