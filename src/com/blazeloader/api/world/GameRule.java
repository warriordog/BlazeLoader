package com.blazeloader.api.world;

import net.minecraft.world.GameRules;

public class GameRule {
	private final GameRules parent;
	private final String key;
	
	private GameRules.ValueType type = GameRules.ValueType.ANY_VALUE;
	
	protected GameRule(GameRules rules, String name) {
		key = name;
		parent = rules;
		if (parent.hasRule(name)) {
			for (GameRules.ValueType i : GameRules.ValueType.values()) {
				if (parent.areSameType(name, i)) {
					type = i;
					break;
				}
			}
		} else {
			parent.setOrCreateGameRule(name, ""); //Default value: {string:"",boolean:false,int:0,double:0.0}
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
	 * Sets the value for this gamerule
	 * @param value	A string value
	 */
	public void set(String value) {
		parent.setOrCreateGameRule(key, value);
	}
	
	/**
	 * Gets the value of this gamerule as a boolean
	 */
	public boolean getBool() {
		return parent.getGameRuleBooleanValue(key);
	}
	
	/**
	 * Sets the value for this gamerule
	 * @param value	A boolean value
	 */
	public void setBool(boolean value) {
		set(Boolean.toString(value));
	}
	
	/**
	 * Gets the value of this gamerule as an integer
	 */
	public int getInt() {
		return parent.getInt(key);
	}
	
	/**
	 * Sets the value for this gamerule
	 * @param value	An integer value
	 */
	public void setInt(int value) {
		set(Integer.toString(value));
	}
	
	/**
	 * Gets the value of this gamerule as a double
	 * <p>
	 * Doubles are currently supported by vanilla GameRule but has no way to get them out. This method fixes that.
	 */
	public double getDouble() {
		try {
			return Double.parseDouble(get());
		} catch (NumberFormatException e) {}
		return (double)getInt();
	}
	
	/**
	 * Sets the value for this gamerule
	 * @param value	A double value
	 */
	public void setDouble(double value) {
		set(Double.toString(value));
	}
	
	/**
	 * Gets the type of this gamerule.
	 */
	public GameRules.ValueType getType() {
		return type;
	}
}
