package com.blazeloader.api.client.effects;

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
	private static final ArrayList<String> particleNames;
	private static Map<Integer, IParticleFactory> vanillaRegistry;
	private static final HashMap<Integer, ParticleType> particleIds = new HashMap<Integer, ParticleType>();
	private static final ArrayList<ParticleType> particlesRegistry = new ArrayList<ParticleType>();
	
	static {
		particleNames = new ArrayList<String>();
		for (String i : EnumParticleTypes.getParticleNames()) {
			if (!particleNames.contains(i)) particleNames.add(i);
		}
	}
	
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
	public static ParticleType registerParticle(IParticleFactory factory, String name, boolean ignoreDistance, int argumentCount) {
		ParticleType result = new ParticleType(factory, name, ignoreDistance, argumentCount);
		particlesRegistry.add(result);
		if (!particleNames.contains(name) && !name.endsWith("_")) {
			particleNames.add(name);
		}
		return result;
	}
	
	/**
	 * Gets the names of all registered particles.
	 * 
	 * @return String[] array of names.
	 */
	public static String[] getParticleNames() {
		return particleNames.toArray(new String[particleNames.size()]);
	}
	
	/**
	 * Retrieves a ParticleType by it's id.
	 * 
	 * @param id	Particle ID
	 * 
	 * @return Associated particle type
	 */
	public static ParticleType getParticleFromId(int id) {
		if (particleIds.containsKey(id)) {
			return particleIds.get(id);
		}
		
		try {
			return getParticleFromId(EnumParticleTypes.getParticleFromId(id));
		} catch (Throwable e) {}
		return ParticleType.NONE;
	}
	
	/**
	 * Retrieves a ParticleType for one of vanilla Minecraft's particles.
	 * 
	 * @param id	Particle ID
	 * 
	 * @return Associated particle type
	 */
	public static ParticleType getParticleFromId(EnumParticleTypes vanillaType) {
		try {
			ParticleType result = get(vanillaType);
			particleIds.put(vanillaType.getParticleID(), result);
			return result;
		} catch (Throwable e) {}
		
		return ParticleType.NONE;
	}
	
	private static ParticleType get(EnumParticleTypes vanillaType) {
		return (new ParticleType(vanillaRegistry.get(vanillaType.getParticleID()), vanillaType.getParticleName(), vanillaType.func_179344_e(), vanillaType.getArgumentCount())).setId(vanillaType.getParticleID());
	}
	
	/**
	 * Initializes particle IDs and loads them into the vanilla registry for external API support.
	 * @param mapping	Mapping of pre-registered vanilla Particles
	 * 
	 * @returns A new, or previously cached, mapping with all custom particles added.
	 */
	public static Map<Integer, IParticleFactory> syncroniseParticlesRegistry(Map<Integer, IParticleFactory> mapping) {
		if (vanillaRegistry == null || !vanillaRegistry.equals(mapping)) {
			vanillaRegistry = mapping;
			int injected = 0;
			Iterator<ParticleType> types = particlesRegistry.iterator();
			for (int i = 0; types.hasNext();) {
				if (mapping.containsKey(i)) {
					i++;
				} else {
					ParticleType type = types.next();
					if (!particleIds.containsValue(type)) {
						mapping.put(i, type.getFactory());
						particleIds.put(i, type.setId(i));
						i++;
					} else {
						if (!mapping.containsKey(type.getId())) {
							mapping.put(type.getId(), type.getFactory());
						}
					}
				}
			}
		}
		return vanillaRegistry;
	}
	
    /**
     * Spawns block hit particles around an entity.
     * 
     * @param e				Entity to apply the affect to.
     * @param blockState	Blockstate for the block to base the particles off of.
     */
    public static void addBlockHitEffectsToEntity(Entity e, IBlockState blockState) {
    	for (int side = 0; side < 6; side++) {
    		addBlockHitEffectsToEntity(e, blockState, side);
    	}
    }
    
    /**
     * Spawns block hit particles along one wide of the given Entity's hitbox.
     * 
     * @param e				Entity to apply the affect to.
     * @param blockState	Blockstate for the block to base the particles off of.
     * @param side			The side of the entity's hit box to apply this effect to.
     */
    public static void addBlockHitEffectsToEntity(Entity e, IBlockState blockState, int side) {
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
        addEffectToRenderer(buildDiggingEffect(e.worldObj, x, y, z, vX, vY, vZ, blockState).func_174846_a(new BlockPos((int)x, (int)y, (int)z)).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }
    
    /**
     * Spawns block destruction particles inside an entity's hit area.
     * Useful for creating some fancy effects.
     * 
     * @param e				Entity to apply this effect to.
     * @param blockState	BlockState for the block to base the particles off of.
     */
    public static void addBlockDestroyEffectsToEntity(Entity e, IBlockState blockState) {
    	float f = 0.1f;
    	int total = 64 * (int)(e.width * e.height * e.width);
    	for (int i = 0; i < total; i++) {
	    	double x = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posX - e.width/2 - f, e.posX + e.width/2 + f);
	    	double y = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posY - f, e.posY + e.height + f);
	    	double z = MathHelper.getRandomDoubleInRange(e.worldObj.rand, e.posZ - e.width/2 - f, e.posZ + e.width/2 + f);
	        addEffectToRenderer(buildDiggingEffect(e.worldObj, x, y, z, x - (int)x - 0.5, y - (int)y - 0.5, z - (int)z - 0.5, blockState).func_174846_a(new BlockPos((int)x, (int)y, (int)z)));
    	}
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
		total *= shape.getVolumeOfSpawnableSpace();
		for (int i = 0; i < total; i++) {
			Vec3 point = shape.computePoint(world.rand);
			spawnParticle(particle.setPos(x + shape.getXOffset() + point.xCoord, y + shape.getYOffset() + point.yCoord, z + shape.getZOffset() + point.zCoord), world);
		}
	}
	
	/**
	 * Spawns a particle emitter in the world attached to the given entity.
	 * 
	 * @param e			Entity to attach to
	 * @param particle	Particle to spawn
	 */
	public static void spawnParticleEmitter(Entity e, ParticleData particle) {
		addEffectToRenderer(new ParticleEmitter(e.worldObj, e, particle));
	}
	
	/**
	 * Spawns a particle
	 * 
     * @param particle		ParticleData for the particle to spawn
     * @param world			World to spawn the particle in
	 */
    public static void spawnParticle(ParticleData particle, World world) {
    	if (particle.getType() == ParticleType.NONE) return;
    	
    	Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            int particleSetting = mc.gameSettings.particleSetting;

            if (particleSetting == 1 && mc.theWorld.rand.nextInt(3) == 0) {
            	particleSetting = 2;
            }
            
            IParticleFactory factory = particle.getType().getFactory();
            try {
	            if (particle.getIgnoreDistance()) {
	            	spawnCustomParticle(particle, factory, world);
	            } else {
    	            double disX = mc.getRenderViewEntity().posX - particle.posX;
    	            double disY = mc.getRenderViewEntity().posY - particle.posY;
    	            double disZ = mc.getRenderViewEntity().posZ - particle.posZ;
	                if (disX * disX + disY * disY + disZ * disZ <= particle.getMaxRenderDistance() && particleSetting <= 1) {
	                	spawnCustomParticle(particle, factory, world);
	                }
	            }
            } catch (Throwable e) {
            	reportParticleError(e, factory, particle.getType(), particle.posX, particle.posY, particle.posZ, particle.getArgs());
            }
        }
    }
    
    private static void spawnCustomParticle(ParticleData particle, IParticleFactory factory, World world) {
    	addEffectToRenderer(factory.getEntityFX(particle.getType().getId(), world, particle.posX, particle.posY, particle.posZ, particle.velX, particle.velY, particle.velZ, particle.getArgs()));
    }
    
    private static void reportParticleError(Throwable e, IParticleFactory factory, ParticleType particle, final double x, final double y, final double z, int[] args) {
    	CrashReport report = CrashReport.makeCrashReport(e, "Exception while adding custom particle");
        CrashReportCategory category = report.makeCategory("Particle being added");
    	category.addCrashSection("ID", particle.getId());
    	category.addCrashSection("Particle Factory Class", factory == null ? "Null" : factory.getClass().toString());
    	
        if (args != null && args.length > 0) category.addCrashSection("Parameters", args);
        category.addCrashSectionCallable("Position", new Callable() {
            public String call() {
                return CrashReportCategory.getCoordinateInfo(x, y, z);
            }
        });
        throw new ReportedException(report);
    }
    
    /**
     * Adds a particle to the client renderer
     * 
     * @param fx	EntityFX to add.
     */
    public static void addEffectToRenderer(EntityFX fx) {
    	if (fx != null) {
    		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    	}
    }
    
    private static EntityDiggingFX buildDiggingEffect(World w, double x, double y, double z, double vX, double vY, double vZ, IBlockState blockState) {
    	return (EntityDiggingFX)(new EntityDiggingFX.Factory()).getEntityFX(0, w, x, y, z, vX, vY, vZ, Block.getStateId(blockState));
    }
}
