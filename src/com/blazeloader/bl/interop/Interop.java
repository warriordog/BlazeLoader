package com.blazeloader.bl.interop;

public class Interop {
	
	/**
	 * Returns a class object for the given name, or null if none are found.
	 * 
	 * @param className	Name of class to look for.
	 */
	public static Class getDeclaredClass(String className) {
		try {
			return Interop.class.getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {}
		return null;
	}
	
	/**
	 * Utility method to get the current classloader
	 */
	protected static ClassLoader loader() {
		return Interop.class.getClassLoader();
	}
}
