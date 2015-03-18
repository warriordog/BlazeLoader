package com.blazeloader.util.shape;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * A lonely Line. The simplest form of shape.
 *
 */
public class Line implements IShape {
	
	double len;
	
	double dX;
	double dY;
	double dZ;
	
	double sX;
	double sY;
	double sZ;
	
	private float yaw = 0;
	private float pitch = 0;
	
	/**
	 * Creates a line with a given length, starting point, and gradient represented
	 * by another point.
	 * 
	 * @param length	Length of this line
	 * @param startX	Offset X from origin
	 * @param startY	Offset Y from origin
	 * @param startZ	Offset Z from origin
	 * @param deltaX	Change in X
	 * @param deltaY	Change in Y
	 * @param deltaZ	Change in Z
	 */
	public Line(double length, double startX, double startY, double startZ, double deltaX, double deltaY, double deltaZ) {
		len = length;
		dX = deltaX;
		dY = deltaY;
		dZ = deltaZ;
		sX = startX;
		sY = startY;
		sZ = startZ;
	}
	
	public double getVolumeOfSpawnableSpace() {
		return len;
	}
	
	public double getXOffset() {
		return sX;
	}
	
	public double getYOffset() {
		return sY;
	}
	
	public double getZOffset() {
		return sZ;
	}
	
	public Vec3 computePoint(Random rand) {
		double distance = MathHelper.getRandomDoubleInRange(rand, 0, len);
		return (new Vec3(distance * dX, distance * dY, distance * dZ)).rotateYaw(yaw).rotatePitch(pitch);
	}
	
	public Line setRotation(float u, float v) {
		yaw = u;
		pitch = v;
		return this;
	}
}
