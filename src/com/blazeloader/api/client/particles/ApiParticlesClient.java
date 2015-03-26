package com.blazeloader.api.client.particles;

import com.blazeloader.api.particles.IParticle;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.api.particles.ParticlesRegister;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.entity.Entity;

/**
 * Provides an API and registry for custom particles.
 *
 * Client side component
 *
 * Warning: Experimental.
 * 
 */
public class ApiParticlesClient {
	
	/**
	 * Registers a custom particle.
	 * 
	 * @param particle			ParticleType previously created by registering a particle with ApiParticles
	 * @param factory			IParticleFactory to generate an EntityFX when needed
	 * 
	 * @return ParticleType representing your particle. Use this to spawn your particle.
	 */
	public static IParticle registerParticleFactory(IParticle particle, IParticleFactory factory) {
		return ParticlesRegister.instance().setFactory(particle, factory);
	}
	
	/**
	 * Spawns a particle emitter in the world attached to the given entity.
	 * <p>Client only. Calling this on the server will not do anything.
	 * 
	 * @param e			Entity to attach to
	 * @param particle	Particle to spawn
	 */
	public static void spawnParticleEmitter(Entity e, ParticleData particle) {
		ParticlesRegister.instance().spawnParticleEmitter(e, particle);
	}
	
    /**
     * Adds a particle to the client renderer
     * 
     * @param fx	EntityFX to add.
     */
    public static void addEffectToRenderer(EntityFX fx) {
    	ParticlesRegister.instance().addEffectToRenderer(fx);
    }
}
