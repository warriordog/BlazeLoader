package com.blazeloader.util.config;

/**
 * Container object of a value-key pair in a config file.
 *
 * @param <T> the type of value it contains
 */
public interface IProperty<T> {
	/**
	 * Sets the default value
	 */
	public void setDefault(T newDef);
	
	/**
	 * Gets the default value
	 */
	public T getDefault();
	
	/**
	 * Sets the curent value back to the default.
	 */
	public void reset();
	
	/**
	 * Gets the current value.
	 * @return
	 */
	public T get();
	
	/**
	 * Sets the current value to the given value.
	 */
	public void set(T val);
	
	/**
	 * Sets a description/comment to be stored with this property.
	 */
	public void setDescription(String... desc);
	
	/**
	 * Gets a string representation of the type this property takes.
	 */
	public Class getType();
	
	/**
	 * The name of this property. Is also the key this property is registered under in config files and categories.
	 */
	public String getName();
	
	/**
	 * Returns an array of possible values if bounded, otherwise returns null.
	 * 
	 * This may be useful if you're working with enums.
	 */
	public T[] getPossibleValues();
}
