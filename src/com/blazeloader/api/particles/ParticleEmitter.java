package com.blazeloader.api.particles;

import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ParticleEmitter extends EntityParticleEmitter {

	private Entity entity;
	private ParticleData particle;
	
	private int age;
	private int maxAge;
	
	public ParticleEmitter(World world, Entity e, ParticleData particleData) {
		super(world, e, null);
		entity = e;
		particle = particleData;
	}
	
	public void onUpdate() {
        for (int i = 0; i < 16; ++i) {
            double vX = rand.nextFloat() * 2 - 1;
            double vY = rand.nextFloat() * 2 - 1;
            double vZ = rand.nextFloat() * 2 - 1;
            if (vX * vX + vY * vY + vZ * vZ <= 1) {
                double x = entity.posX + vX * entity.width/4;
                double y = entity.getEntityBoundingBox().minY + entity.height/2 + vY * entity.height/4;
                double z = entity.posZ + vZ * entity.width/4;
                ApiParticles.spawnParticle(particle.setPos(x, y, z).setVel(vX, vY + 0.2D, vZ), worldObj);
            }
        }
        if (++age >= maxAge) setDead();
    }
}
