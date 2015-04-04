package com.blazeloader.util.shape;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * Half of a sphere.
 * Or some smaller division if you wish. The smallest it can go is 1/8th of a sphere.
 *
 */
public class HemiSphere extends Sphere {
	
	private Boolean[] quadrants;
	
	private float yaw = 0;
	private float pitch = 0;
	
	/**
	 * Creates a uniform hemisphere cut along any of the three axis.
	 * 
	 * @param hollow	True if this shape must be hollow. Cut along any of the three axis.
	 * @param radius	Sphere radius
	 * @param cuts		Array of Booleans representing each cut and their direction. Use Null for no cut.
	 */
	public HemiSphere(boolean hollow, double radius, Boolean... cuts) {
		this(hollow, radius, 1, 1, 1, cuts);
	}
	
	/**
	 * Creates a hemisphere of arbitrary dimensions cut along any of the three axis.
	 * 
	 * Can be used to create a flat circle by setting one of the stretch paramaters to 0.
	 * If you set two of them to 0 it will probably produce a line.
	 * 
	 * @param hollow	True if this shape must be hollow.
	 * @param radius	Sphere radius
	 * @param stretchX	Warp this shape's X-axis
	 * @param stretchY	Warp this shape's Y-axis
	 * @param stretchZ	Warp this shape's Z-axis
	 * @param cuts		Array of Booleans representing each cut and their direction. Use Null for no cut.
	 * 
	 */
	public HemiSphere(boolean hollow, double radius, float stretchX, float stretchY, float stretchZ, Boolean... cuts) {
		super(hollow, radius, stretchX, stretchY, stretchZ);
		
		quadrants = cuts;
	}
	
	public double getVolumeOfSpawnableSpace() {
		double result = super.getVolumeOfSpawnableSpace();
		
		for (int i = 0; i < 3 && i < quadrants.length; i++) {
			if (quadrants[i] != null) {
				result /= 2;
			}
		}
		return result;
	}
	
	public Vec3 computePoint(Random rand) {
		Vec3 result = super.computePoint(rand);
		if (quadrants.length > 0 && quadrants[0] != null) {
			if ((quadrants[0] == true && result.xCoord < 0) || (quadrants[0] == false && result.xCoord > 0)) {
				result = new Vec3(-result.xCoord, result.yCoord, result.zCoord);
			}
		}
		if (quadrants.length > 1 && quadrants[1] != null) {
			if ((quadrants[1] == true && result.yCoord < 0) || (quadrants[1] == false && result.yCoord > 0)) {
				result = new Vec3(result.xCoord, -result.yCoord, result.zCoord);
			}
		}
		if (quadrants.length > 2 && quadrants[2] != null) {
			if ((quadrants[2] == true && result.zCoord < 0) || (quadrants[2] == false && result.zCoord > 0)) {
				result = new Vec3(result.xCoord, result.yCoord, -result.zCoord);
			}
		}
		return result.rotateYaw(yaw).rotatePitch(pitch);
	}
	
	public HemiSphere setRotation(float u, float v) {
		yaw = u;
		pitch = v;
		return this;
	}
}
