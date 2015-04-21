package com.blazeloader.api.world;

import net.minecraft.world.WorldProvider;

/**
 * 
 * All the Moon's possible phases.
 *
 */
public enum MoonPhase {
	FULL_MOON,
	WAXING_GIBBOUS,
	FIRST_QUARTER,
	WAXING_CRESCENT,
	NEW_MOON,
	WANING_CRESCENT,
	THIRD_QUARTER,
	WANING_GIBBOUS;
	
	/**
	 * Gets the MoonPhase factor for this MoonPhase based on the factors in WorldProvider
	 * @return
	 */
	public float getFactor() {
		return WorldProvider.moonPhaseFactors[toInt()];
	}
	
	/**
	 * Gets the ordinal value of this MoonPhase as used natively by minecraft.
	 */
	public int toInt() {
		return ordinal();
	}
	
	/**
	 * Gets a MoonPhase value from its ordinal provided by the Minecraft world.
	 * @param phase		An integer ordinal for the desired MoonPhase
	 * @return A MoonPhase corresponding to the id given, wrapped round to a valid index if the given one is out of bounds.
	 */
	public static MoonPhase fromInt(int phase) {
		return values()[phase % values().length];
	}
	
	/**
	 * Checks if the given phase corresponds to a MoonPhase index.
	 * @param phase		The integer ordinal to check
	 * @return True if the integer is an index that fits within the set of valid MoonPhases (0-7)
	 */
	public static boolean isValidMoonPhase(int phase) {
		return phase >= 0 && phase < values().length;
	}
}
