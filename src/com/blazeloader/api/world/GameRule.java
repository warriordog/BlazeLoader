package com.blazeloader.api.world;

import net.minecraft.world.GameRules;

public class GameRule {
	private final GameRules parent;
	private final String key;
	
	private GameRules.ValueType type = null;
	
	protected GameRule(GameRules rules, String name) {
		key = name;
		parent = rules;
		for (GameRules.ValueType i : GameRules.ValueType.values()) {
			if (parent.areSameType(name, i)) {
				type = i;
				break;
			}
		}
		if (type == null) {
			type = GameRules.ValueType.ANY_VALUE;
		}
	}
	
	/**
	 * Gets the name used to identify this gamerule in the world
	 */
	public String getName() {
		return key;
	}
	
	/**
	 * Gets a string value for this gamerule
	 */
	public String get() {
		return parent.getGameRuleStringValue(key);
	}
	
	/**
	 * Gets the value of this gamerule as a boolean
	 */
	public boolean getBool() {
		return parent.getGameRuleBooleanValue(key);
	}
	
	/**
	 * Gets the value of this gamerule as an integer
	 */
	public int getInt() {
		return parent.getInt(key);
	}
	
	/**
	 * Gets the type of this gamerule.
	 */
	public GameRules.ValueType getType() {
		return type;
	}
}
