package com.blazeloader.api.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;
import com.google.common.collect.Collections2;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Provides an API and registry for client based custom particles.
 *
 */
public class ApiParticles {
	/**
	 * Registers a custom particle.
	 * 
	 * @param factory			IParticleFactory to generate an EntityFX when needed
	 * @param name				The name of your particle
	 * @param ignoreDistance	Whether your particle will always spawn regardless of how far it is from the camera
	 * @param argumentCount		Number of extra arguments your particle takes when create
	 * 
	 * @return ParticleType representing your particle. Use this to spawn your particle.
	 */
	public static IParticle registerParticle(IParticleFactory factory, String name, boolean ignoreDistance, int argumentCount) {
		return ParticlesRegister.instance.setFactory(ParticlesRegister.instance.registerParticle(name, ignoreDistance, argumentCount), factory);
	}
	
	/**
	 * Gets the names of all registered particles.
	 * 
	 * @return String[] array of names.
	 */
	public static String[] getParticleNames() {
		return ParticlesRegister.getParticleNames();
	}
	
	/**
	 * Retrieves a ParticleType by it's id.
	 * 
	 * @param id	Particle ID
	 * 
	 * @return Associated particle type
	 */
	public static IParticle getParticleFromId(int id) {
		return ParticlesRegister.getParticleFromId(id);
	}
	
	/**
	 * Retrieves a ParticleType for one of vanilla Minecraft's particles.
	 * 
	 * @param id	Particle ID
	 * 
	 * @return Associated particle type
	 */
	public static IParticle getParticle(EnumParticleTypes vanillaType) {
		return ParticlesRegister.getParticle(vanillaType);
	}
	
    /**
     * Spawns block hit particles around an entity.
     * 
     * @param e				Entity to apply the affect to.
     * @param blockState	Blockstate for the block to base the particles off of.
     */
    public static void addBlockHitEffectsToEntity(Entity e, IBlockState blockState) {
    	ParticlesRegister.instance.addBlockHitEffectsToEntity(e, blockState);
    }
    
    /**
     * Spawns block hit particles along one wide of the given Entity's hitbox.
     * 
     * @param e				Entity to apply the affect to.
     * @param blockState	Blockstate for the block to base the particles off of.
     * @param side			The side of the entity's hit box to apply this effect to.
     */
    public static void addBlockHitEffectsToEntity(Entity e, IBlockState blockState, int side) {
    	ParticlesRegister.instance.addBlockHitEffectsToEntity(e, blockState, side);
    }
    
    /**
     * Spawns block destruction particles inside an entity's hit area.
     * Useful for creating some fancy effects.
     * 
     * @param e				Entity to apply this effect to.
     * @param blockState	BlockState for the block to base the particles off of.
     */
    public static void addBlockDestroyEffectsToEntity(Entity e, IBlockState blockState) {
    	ParticlesRegister.instance.addBlockDestroyEffectsToEntity(e, blockState);
    }
    
    /**
     * Spawns particles in a spherical bubble around a point.
     * 
     * Particle density is capped at 64 per block.
     * 
     * @param particleType		ParticleData for the particle to spawn
     * @param world				World to spawn the particle in
     * @param x					X center coordinate
     * @param y					Y center coordinate
     * @param z					Z center coordinate
     * @param radius			Radius of sphere
     */
    public static void particleBubble(ParticleData particleType, World world, double x, double y, double z, double radius) {
    	particleBubble(particleType, world, x, y, z, radius, 64);
    }
    
    /**
     * Spawns particles in a spherical bubble around a point.
     * 
     * @param particleType		ParticleData for the particle to spawn
     * @param world				World to spawn the particle in
     * @param x					X center coordinate
     * @param y					Y center coordinate
     * @param z					Z center coordinate
     * @param radius			Radius of sphere
     * @param total				Density of particles per block
     */
	public static void particleBubble(ParticleData particleType, World world, double x, double y, double z, double radius, int total) {
		spawnParticleShape(particleType, world, x, y, z, new Sphere(true, radius), total);
    }
	
	/**
	 * Spawns a particle in a shaped dictated by the given function.
     * 
     * @param particleType		ParticleData for the particle to spawn
     * @param world				World to spawn the particle in
     * @param x					X center coordinate
     * @param y					Y center coordinate
     * @param z					Z center coordinate
	 * @param shape				Shape to draw
	 * @param total				Density of particles per block
	 */
	public static void spawnParticleShape(ParticleData particle, World world, double x, double y, double z, IShape shape, int total) {
		ParticlesRegister.instance.spawnParticleShape(particle, world, x, y, z, shape, total);
	}
	
	/**
	 * Spawns a particle emitter in the world attached to the given entity.
	 * 
	 * @param e			Entity to attach to
	 * @param particle	Particle to spawn
	 */
	public static void spawnParticleEmitter(Entity e, ParticleData particle) {
		ParticlesRegister.instance.spawnParticleEmitter(e, particle);
	}
	
	/**
	 * Spawns a particle
	 * 
     * @param particle		ParticleData for the particle to spawn
     * @param world			World to spawn the particle in
	 */
    public static void spawnParticle(ParticleData particle, World world) {
    	ParticlesRegister.instance.spawnParticle(particle, world);
    }
    
    /**
     * Adds a particle to the client renderer
     * 
     * @param fx	EntityFX to add.
     */
    public static void addEffectToRenderer(Entity fx) {
    	ParticlesRegister.instance.addEffectToRenderer(fx);
    }
}
