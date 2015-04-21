package com.blazeloader.api.world;

import net.minecraft.world.WorldProvider;

public enum MoonPhase {
	FULL_MOON,
	WAXING_GIBBOUS,
	FIRST_QUARTER,
	WAXING_CRESCENT,
	NEW_MOON,
	WANING_CRESCENT,
	THIRD_QUARTER,
	WANING_GIBBOUS;
	
	public float getFactor() {
		return WorldProvider.moonPhaseFactors[toInt()];
	}
	
	public int toInt() {
		return ordinal();
	}
	
	public static MoonPhase fromInt(int phase) {
		return values()[phase % values().length];
	}
}
