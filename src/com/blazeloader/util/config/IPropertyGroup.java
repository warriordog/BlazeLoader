package com.blazeloader.util.config;

/**
 * Container grouping numerous properties together in a config file.
 */
public interface IPropertyGroup {
	
	/**
	 * Returns the name attached to the section.
	 * @return
	 */
	public String getName();
	
	/**
	 * Sets a description/comment to store alongside this section. 
	 */
	public void setDescription(String desc);
	
	/**
	 * Checks if this group has a property for the given key.
	 * 
	 * @param key	The name of the key.
	 * 
	 * @return true if the property exists.
	 */
	public boolean has(String key);
	
	/**
	 * Gets or adds a new property with the given key and default value.
	 * 
	 * @param key	Key to identify the property
	 * 
	 * @param def	A default value. If the key does not exist it will be instantiated with this value
	 *  
	 * @return Resulting property object.
	 */
	public <T> IProperty<T> get(String key, T def);
}
