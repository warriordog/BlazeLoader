package com.blazeloader.api.particles;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.version.Versions;
import com.mumfrey.liteloader.core.PluginChannels.ChannelPolicy;
import com.mumfrey.liteloader.core.ServerPluginChannels;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * Manages the registration, creation, and spawning of custom particles. 
 *
 * Server side.
 * 
 * Warning: Not yet fully implemented. It may be best to keep spawning your stuff on the client for now.
 *
 */
public class ParticlesRegister {
	private static ParticlesRegister instance;
	
	protected static final ArrayList<String> particleNames = new ArrayList<String>();
	protected static final HashMap<Integer, IParticle> particleIds = new HashMap<Integer, IParticle>();
	protected static final ArrayList<IParticle> particlesRegistry = new ArrayList<IParticle>();
	
	static {
		for (String i : EnumParticleTypes.getParticleNames()) {
			if (!particleNames.contains(i)) particleNames.add(i);
		}
	}
	
	protected ParticlesRegister() {
		if (instance != null) {
			throw new IllegalStateException("ParticlesRegister has already been initialized. Cannot initialize again.");
		}
		instance = this;
	}

	public static ParticlesRegister instance() {
		if (instance == null) {
			if (Versions.isClient()) {
				return new ParticlesRegisterClient();
			}
			return new ParticlesRegister();
		}
		return instance;
	}
	
	public static String[] getParticleNames() {
		return particleNames.toArray(new String[particleNames.size()]);
	}
	
	public static IParticle getParticleFromId(int id) {
		if (particleIds.containsKey(id)) {
			return particleIds.get(id);
		}
		
		if (EnumParticleTypes.PARTICLES.containsKey(id)) {
			return getParticleFromEnum(EnumParticleTypes.getParticleFromId(id));
		}
		return ParticleType.NONE;
	}
	
	public static IParticle getParticleFromEnum(EnumParticleTypes vanillaType) {
		if (particleIds.containsKey(vanillaType.getParticleID())) {
			return particleIds.get(vanillaType.getParticleID());
		}
		
		IParticle result = instance().getParticle(vanillaType);
		particleIds.put(vanillaType.getParticleID(), result);
		return result;
	}
	
	public static void initialiseParticleIds() {
		instance().initialiseIds();
	}
	
	//TODO: Have the loader call this after mod initialisation to set IDs for custom particles.
	public void initialiseIds() {
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
	
	protected IParticle getParticle(EnumParticleTypes vanillaType) {
		return (new ParticleType(vanillaType.getParticleName(), vanillaType.func_179344_e(), vanillaType.getArgumentCount())).setId(vanillaType.getParticleID());
	}
	
    public void addBlockHitEffectsToEntity(Entity e, IBlockState blockState) {
    	for (int side = 0; side < 6; side++) {
    		addBlockHitEffectsToEntity(e, blockState, side);
    	}
    }
    
    public void addBlockHitEffectsToEntity(Entity e, IBlockState blockState, int side) {
    	side = side % 6;
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
	
	public void spawnParticleEmitter(Entity e, ParticleData particle) {}
	
    public void spawnParticle(ParticleData particle, World world) {
    	if (particle.getType() == ParticleType.NONE) return;
    	
    	//TODO: Setup a channel for Blazeloader to send/recieve custom particles. For now this will only send particles recognized by vanilla minecraft.
    	if (EnumParticleTypes.PARTICLES.containsKey(particle.getType().getId())) {
    		Packet packet = new S2APacketParticles(EnumParticleTypes.getParticleFromId(particle.getType().getId()), particle.getIgnoreDistance(), (float)particle.posX, (float)particle.posY, (float)particle.posZ, 0, 0, 0, (float)particle.getVel().lengthVector(), 1, particle.getArgs());
	        for (EntityPlayerMP player : (ArrayList<EntityPlayerMP>)(((WorldServer)world).playerEntities)) {
	            BlockPos pos = player.getPosition();
	            double dist = pos.distanceSq(particle.posX, particle.posY, particle.posZ);
	            if (dist <= particle.getMaxRenderDistance() || particle.getIgnoreDistance() && dist <= 65536.0D) {
	            	//sendPacket(player, packet);
	                player.playerNetServerHandler.sendPacket(packet);
	            }
	        }
    	}
    }
    
    public void handleParticleSpawn(World w, Packet p) {
    	//Do nothing. Since we're on the server.
    }
    
    private void sendPacket(EntityPlayerMP player, Packet p) {
    	PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
    	try {
			p.writePacketData(buf);
			ServerPluginChannels.sendMessage(player, BLMain.instance().getPluginChannelName() + "PARTICLES", buf, ChannelPolicy.DISPATCH_ALWAYS);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void addEffectToRenderer(Entity fx) {}
    
    protected void spawnDigginFX(World w, double x, double y, double z, double vX, double vY, double vZ, IBlockState blockState, float multScale, float multVel) {
    	((WorldServer)w).spawnParticle(EnumParticleTypes.BLOCK_CRACK, false, x, y, z, 1, 0, 0, 0, Math.sqrt(vX * vX + vY * vY + vZ * vZ) * multVel, Block.getStateId(blockState));
    }
}
