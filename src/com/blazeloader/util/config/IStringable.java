package com.blazeloader.util.config;

/**
 * Utility interface for adding types you want supported by config files
 * 
 * Must have an empty constructor in addition to the below methods.
 *
 */
public interface IStringable<T extends IStringable<T>> {
	
	/**
	 * Just like object. But you really, really need to override it.
	 * @return
	 */
	public String toString();
	
	/**
	 * Opposite of toString, converts a string back into an object of this type.
	 * 
	 * @param string String representation of an instance of this class
	 */
	public T fromString(String string);
}
