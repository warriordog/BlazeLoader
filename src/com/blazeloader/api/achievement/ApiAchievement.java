package com.blazeloader.api.achievement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

public class ApiAchievement {
	/**
	 * Unlocks an achievement for the given player.
	 * 
	 * @param player		Player who has achieved
	 * @param achievement	What the player has achieved
	 */
	public static void unlockAchievement(EntityPlayer player, Achievement achievement) {
		player.triggerAchievement(achievement);
	}
	
	/**
	 * Creates and registers a new achievement
	 * 
	 * @param unlocalisedName	The name for this achievement. Used for its id and chat message
	 * @param gridX				X coordinate on the achievement screen
	 * @param gridY				Y coordinate on the achievement screen
	 * @param displayedItem		Item to display next to this achievement
	 * @return New achievement ready to use
	 */
	public static Achievement registerAchievement(String unlocalisedName, int gridX, int gridY, ItemStack displayedItem) {
		return registerAchievement(new Achievement(unlocalisedName, unlocalisedName, gridX, gridY, displayedItem, null)).setIndependent();
	}
	
	/**
	 * Creates and registers a new achievement
	 * 
	 * @param unlocalisedName		The name for this achievement. Used for its id and chat message
	 * @param gridX					X coordinate on the achievement screen
	 * @param gridY					Y coordinate on the achievement screen
	 * @param displayedItem			Item to display next to this achievement
	 * @param requiredAchievement 	Achievement that must be unlocked before this one
	 * @return New achievement ready to use
	 */
	public static Achievement registerAchievement(String unlocalisedName, int gridX, int gridY, ItemStack displayedItem, Achievement requiredAchievement) {
		return registerAchievement(new Achievement(unlocalisedName, unlocalisedName, gridX, gridY, displayedItem, requiredAchievement));
	}
	
	/**
	 * Registers an achievement that has already been initialised
	 * 
	 * 
	 * @return New achievement ready to use
	 */
	public static Achievement registerAchievement(Achievement achievement) {
		return achievement.registerAchievement();
	}
}
