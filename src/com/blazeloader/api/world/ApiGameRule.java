package com.blazeloader.api.world;

import net.minecraft.world.World;
import net.minecraft.world.GameRules;

/**
 * Api functions relating to a world's GameRules
 *
 */
public class ApiGameRule {
	
	/**
	 * Adds a new game rule for the given world
	 * 
	 * @param w				World
	 * @param name			Name used to identity the gamerule
	 * @param defaultValue	A default initial value
	 * @param type			The type of value this rule accepts
	 */
	public static void addGameRule(World w, String name, String defaultValue, GameRules.ValueType type) {
		w.getGameRules().addGameRule(name, defaultValue, type);
	}
	
	/**
	 * Gets an array of all game rule entries for a given world.
	 * 
	 * @param w		World
	 */
	public static GameRule[] getAllGameRules(World w) {
		GameRules rules = w.getGameRules();
		String[] keys = rules.getRules();
		GameRule[] result = new GameRule[keys.length];
		for (int i = 0; i < keys.length; i++) {
			result[i] = new GameRule(rules, keys[i]);
		}
		return result;
	}
}
