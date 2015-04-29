package com.blazeloader.api.world;

import net.minecraft.world.WorldType;

public class ApiWorldType {
	
	/**
	 * Creates a new world type with the given name.
	 * 
	 * Translation keys for world types made this way will have the following format:
	 * <p>
	 * {@code modName.generator.name}
	 * <p>
	 * Similarly they will also be identified by a combination of the modName and their name: 
	 * 
	 * @param modName	The mod creating the world type.
	 * @param name		The name of the world type.
	 * @return	A brand new WorldType for use.
	 */
	public static ModWorldType registerWorldType(String modName, String name) {
		return registerWorldType(modName, name, 0);
	}
	
	/**
	 * Creates a new world type with the given name and generator version
	 * 
	 * Translation keys for world types made this way will have the following format:
	 * <p>
	 * {@code modName.generator.name}
	 * 
	 * @param modName	The mod creating the world type.
	 * @param name		The name of the world type.
	 * @param version	The generator version (default 0)
	 * @return	A brand new WorldType for use.
	 */
	public static ModWorldType registerWorldType(String modName, String name, int version) {
		return new ModWorldType(modName, name, version);
	}
	
	/**
	 * Gets the array of all known WorldTypes
	 */
	public static WorldType[] getWorldTypes() {
		return WorldType.worldTypes;
	}
	
	/**
	 * Gets a WorldType with the given name. Or null if none were found.
	 * @param name	The name of the world type
	 */
	public static WorldType getWorldType(String name) {
		return WorldType.parseWorldType(name);
	}
	
	/**
	 * Gets a world type for the given id or null if the given id is not valid.
	 * @param id	The id of the world type
	 */
	public static WorldType getWorldType(int id) {
		if (id < 0 || id > WorldType.worldTypes.length) {
			return null;
		}
		return WorldType.worldTypes[id];
	}
	
	/**
	 * Gets the name of a world type.
	 * If the world type is one registered by ApiWOrldType.registerWorldType will return the name without the mod prefix.
	 * @param type	The world type
	 * @return	A string name.
	 */
	public static String getWorldTypeName(WorldType type) {
		if (type instanceof ModWorldType) {
			return ((ModWorldType)type).getPlainName();
		}
		return type.getWorldTypeName();
	}
	
	/**
	 * Gets the mod name a world type was registered with.
	 * @param type	The world type
	 * @return "vanilla" for non mod world types and "unknown" for unrecognised mods otherwise the mod name it was registered with.
	 */
	public static String getWorldTypeMod(WorldType type) {
		if (type instanceof ModWorldType) {
			return ((ModWorldType)type).getModName();
		}
		if (type.getClass() != WorldType.class) {
			return "unknown";
		}
		return "vanilla";
	}
}
