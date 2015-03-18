package com.blazeloader.api.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;

/**
 * 
 * Manages the registration, creation, and spawning of custom particles. 
 *
 * Server side currently Not Yet Implemented
 *
 */
public class ParticlesRegister {
	public static ParticlesRegister instance;
	
	protected static final ArrayList<String> particleNames = new ArrayList<String>();
	protected static final HashMap<Integer, IParticle> particleIds = new HashMap<Integer, IParticle>();
	protected static final ArrayList<IParticle> particlesRegistry = new ArrayList<IParticle>();
	
	static {
		for (String i : EnumParticleTypes.getParticleNames()) {
			if (!particleNames.contains(i)) particleNames.add(i);
		}
	}
	
	public ParticlesRegister() {
		if (instance != null) {
			throw new RuntimeException("Only 1 instance of ParticlesRegister allowed");
		}
		instance = this;
	}
	
	public void initialiseParticleIds() {
		Set<Integer> registeredIds = EnumParticleTypes.PARTICLES.keySet();
		int injected = 0;
		Iterator<IParticle> types = particlesRegistry.iterator();
		for (int i = 0; types.hasNext();) {
			if (registeredIds.contains(i)) {
				i++;
			} else {
				IParticle type = types.next();
				if (!particleIds.containsValue(type)) {
					particleIds.put(i, type.setId(i));
					i++;
				}
			}
		}
	}
	
	public IParticle registerParticle(String name, boolean ignoreDistance, int argumentCount) {
		ParticleType result = new ParticleType(name, ignoreDistance, argumentCount);
		particlesRegistry.add(result);
		if (!particleNames.contains(name) && !name.endsWith("_")) {
			particleNames.add(name);
		}
		return result;
	}
	
	public IParticle setFactory(IParticle particle, Object factory) {
		return particle;
	}
	
	public static String[] getParticleNames() {
		return particleNames.toArray(new String[particleNames.size()]);
	}
	
	public static IParticle getParticleFromId(int id) {
		if (particleIds.containsKey(id)) {
			return particleIds.get(id);
		}
		
		try {
			return instance.getParticle(EnumParticleTypes.getParticleFromId(id));
		} catch (Throwable e) {}
		return ParticleType.NONE;
	}
	
	public static IParticle getParticle(EnumParticleTypes vanillaType) {
		try {
			IParticle result = instance.internalGetParticle(vanillaType);
			particleIds.put(vanillaType.getParticleID(), result);
			return result;
		} catch (Throwable e) {}
		
		return ParticleType.NONE;
	}
	
	protected IParticle internalGetParticle(EnumParticleTypes vanillaType) {
		return (new ParticleType(vanillaType.getParticleName(), vanillaType.func_179344_e(), vanillaType.getArgumentCount())).setId(vanillaType.getParticleID());
	}
	
    public void addBlockHitEffectsToEntity(Entity e, IBlockState blockState) {
    	for (int side = 0; side < 6; side++) {
    		addBlockHitEffectsToEntity(e, blockState, side);
    	}
    }
    
    public void addBlockHitEffectsToEntity(Entity e, IBlockState blockState, int side) {
    	float f = 0.25f;
    	double x = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posX - e.width/2 - f, e.posX + e.width/2 + f);
    	double y = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posY - f, e.posY + e.height + f);
    	double z = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posZ - e.width/2 - f, e.posZ + e.width/2 + f);
    	
    	double vX = 0;
    	double vY = 0;
    	double vZ = 0;
    	
    	if (side == 0) y = e.posY - f;
        if (side == 1) {
        	y = e.posY + e.height + f;
        	vY += 0.5;
        }
        if (side == 2) {
        	z = e.posZ - e.width/2 - f;
        	vZ -= 0.5;
        }
        if (side == 3) {
        	z = e.posZ + e.width/2 + f;
        	vZ += 0.5;
        }
        if (side == 4) {
        	x = e.posX - e.width/2 - f;
        	vX -= 0.5;
        }
        if (side == 5) {
        	x = e.posX + e.width/2 + f;
        	vX += 0.5;
        }
        spawnDigginFX(e.worldObj, x, y, z, vX, vY, vZ, blockState, 0.2F, 0.6F);
    }
    
    public void addBlockDestroyEffectsToEntity(Entity e, IBlockState blockState) {
    	float f = 0.1f;
    	int total = 64 * (int)(e.width * e.height * e.width);
    	for (int i = 0; i < total; i++) {
	    	double x = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posX - e.width/2 - f, e.posX + e.width/2 + f);
	    	double y = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posY - f, e.posY + e.height + f);
	    	double z = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posZ - e.width/2 - f, e.posZ + e.width/2 + f);
	    	spawnDigginFX(e.worldObj, x, y, z, x - (int)x - 0.5, y - (int)y - 0.5, z - (int)z - 0.5, blockState, 1, 1);
    	}
    }
	
	public void spawnParticleShape(ParticleData particle, World world, double x, double y, double z, IShape shape, int total) {
		total *= shape.getVolumeOfSpawnableSpace();
		for (int i = 0; i < total; i++) {
			Vec3 point = shape.computePoint(world.rand);
			spawnParticle(particle.setPos(x + shape.getXOffset() + point.xCoord, y + shape.getYOffset() + point.yCoord, z + shape.getZOffset() + point.zCoord), world);
		}
	}
	
	public void spawnParticleEmitter(Entity e, ParticleData particle) {
		
	}
	
    public void spawnParticle(ParticleData particle, World world) {
    	if (particle.getType() == ParticleType.NONE) return;
    	
    	
    }
    
    public void addEffectToRenderer(Entity fx) {}
    
    protected void spawnDigginFX(World w, double x, double y, double z, double vX, double vY, double vZ, IBlockState blockState, float multScale, float multVel) {
    	
    }
}
