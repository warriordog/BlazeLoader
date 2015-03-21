package com.blazeloader.util.shape;

import java.util.Random;

import org.lwjgl.util.vector.Vector;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * A sphere, or 2d circle of you so desire.
 *
 */
public class Sphere implements IShape {
	
	private final Vec3 stretch; 
	private final boolean hollow;
	private final double rad;
	
	private float yaw = 0;
	private float pitch = 0;
	
	/**
	 * Creates a uniform sphere.
	 * 
	 * @param hollow	True if this shape must be hollow.
	 * @param radius	Sphere radius
	 */
	public Sphere(boolean hollow, double radius) {
		this(hollow, radius, 1, 1, 1);
	}
	
	/**
	 * Creates a sphere of arbitrary dimensions.
	 * 
	 * Can be used to create a flat circle by setting one of the stretch paramaters to 0.
	 * If you set two of them to 0 it will probably produce a line.
	 * 
	 * @param hollow	True if this shape must be hollow.
	 * @param radius	Sphere radius
	 * @param stretchX	Warp this shape's X-axis
	 * @param stretchY	Warp this shape's Y-axis
	 * @param stretchZ	Warp this shape's Z-axis
	 * 
	 */
	public Sphere(boolean hollow, double radius, float stretchX, float stretchY, float stretchZ) {
		this.hollow = hollow;
		stretch = new Vec3(stretchX, stretchY, stretchZ);
		rad = radius; 
	}
	
	public double getVolumeOfSpawnableSpace() {
		return hollow ? 4 * Math.PI * rad * rad : (4/3) * Math.PI * rad * rad *rad;
	}
	
	public double getXOffset() {
		return -rad/2;
	}
	
	public double getYOffset() {
		return -rad/2;
	}
	
	public double getZOffset() {
		return -rad/2;
	}
	
	public Vec3 computePoint(Random rand) {
		double rho = hollow ? rad : MathHelper.getRandomDoubleInRange(rand, 0, rad);
		
		double pheta = MathHelper.getRandomDoubleInRange(rand, 0, Math.PI);
		double phi = MathHelper.getRandomDoubleInRange(rand, 0, Math.PI);
		
		return (new Vec3(rho * Math.sin(phi) * Math.cos(pheta) * stretch.xCoord,rho * Math.sin(phi) * Math.sin(pheta) * stretch.yCoord, rho * Math.cos(phi) * stretch.zCoord)).rotateYaw(yaw).rotatePitch(pitch);
	}
	
	public Sphere setRotation(float u, float v) {
		yaw = u;
		pitch = v;
		return this;
	}
}
