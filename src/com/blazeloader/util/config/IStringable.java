package com.blazeloader.util.config;

/**
 * Utility interface for adding types you want supported by config files
 * 
 * Must have an empty contrsuctor in addition to the below methods.
 *
 */
public interface IStringable {
	
	/**
	 * Just like object. But you really, really need to overrid it.
	 * @return
	 */
	public String toString();
	
	/**
	 * Opposite of toString, converts a string back into an object of this type.
	 * 
	 * @param string String representation of an instance of this class
	 */
	public IStringable valueOf(String string);
}
