package com.blazeloader.bl.network;

import java.io.IOException;

import com.blazeloader.api.particles.IParticle;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.api.particles.ParticleType;
import com.blazeloader.api.particles.ParticlesRegister;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumParticleTypes;

public class BLPacketParticles implements Packet {
	    private IParticle particleType;
	    private float xCoord;
	    private float yCoord;
	    private float zCoord;
	    private float xOffset;
	    private float yOffset;
	    private float zOffset;
	    private float particleSpeed;
	    private int particleCount;
	    private boolean ignoreDistance;
	    private int[] arguments;

	    public BLPacketParticles() {}

	    public BLPacketParticles(IParticle type, boolean longDist, float x, float y, float z, float xOffsetIn, float yOffsetIn, float zOffsetIn, float speed, int count, int ... args) {
	        particleType = type;
	        ignoreDistance = longDist;
	        xCoord = x;
	        yCoord = y;
	        zCoord = z;
	        xOffset = xOffsetIn;
	        yOffset = yOffsetIn;
	        zOffset = zOffsetIn;
	        particleSpeed = speed;
	        particleCount = count;
	        arguments = args;
	    }

	    /**
	     * Reads the raw packet data from the data stream.
	     */
	    public void readPacketData(PacketBuffer buf) throws IOException {
	        particleType = ParticlesRegister.getParticleFromId(buf.readInt());
	        
	        if (particleType == null) particleType = ParticleType.NONE;
	        
	        ignoreDistance = buf.readBoolean();
	        xCoord = buf.readFloat();
	        yCoord = buf.readFloat();
	        zCoord = buf.readFloat();
	        xOffset = buf.readFloat();
	        yOffset = buf.readFloat();
	        zOffset = buf.readFloat();
	        particleSpeed = buf.readFloat();
	        particleCount = buf.readInt();
	        arguments = new int[particleType.getArgumentCount()];
	        for (int i = 0; i < arguments.length; i++) {
	            arguments[i] = buf.readVarIntFromBuffer();
	        }
	    }
	    
	    public void writePacketData(PacketBuffer buf) throws IOException {
	        buf.writeInt(particleType.getId());
	        buf.writeBoolean(ignoreDistance);
	        buf.writeFloat(xCoord);
	        buf.writeFloat(yCoord);
	        buf.writeFloat(zCoord);
	        buf.writeFloat(xOffset);
	        buf.writeFloat(yOffset);
	        buf.writeFloat(zOffset);
	        buf.writeFloat(particleSpeed);
	        buf.writeInt(particleCount);
	        for (int i = 0; i < particleType.getArgumentCount(); ++i) {
	            buf.writeVarIntToBuffer(arguments[i]);
	        }
	    }

	    public IParticle getType() {
	        return particleType;
	    }

	    public boolean isLongDistance() {
	        return ignoreDistance;
	    }
	    
	    public double getX() {
	        return xCoord;
	    }
	    
	    public double getY() {
	        return yCoord;
	    }
	    
	    public double getZ() {
	        return zCoord;
	    }
	    
	    public float getXOffset() {
	        return xOffset;
	    }
	    
	    public float getYOffset() {
	        return yOffset;
	    }
	    
	    public float getZOffset() {
	        return zOffset;
	    }
	    
	    public float getSpeed() {
	        return particleSpeed;
	    }
	    
	    public int getCount() {
	        return particleCount;
	    }
	    
	    public int[] getArguments() {
	        return arguments;
	    }
	    
	    public void processPacket(INetHandler handler) {
	        ParticlesRegister.instance().handleParticleSpawn(Minecraft.getMinecraft().theWorld, this);
	    }
}