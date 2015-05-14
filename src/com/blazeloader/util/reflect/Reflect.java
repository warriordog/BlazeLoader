package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

/**
 * A collection of utility methods for getting fields and methods.
 */
public final class Reflect {
	/**
	 * Finds a hook into a field by a given BLOBF mapping.
	 */
	public static <T, V> Var<T, V> lookupField(BLOBF obf) {
		return new Var(obf);
	}
	
	/**
	 * Finds a hook into a field by its declaring class, field type and field name.
	 * 
	 * @param declarer	The class in which this field is declared
	 * @param type		The type of values this field takes
	 * @param name		The name of this field
	 */
	public static <T, V> Var<T, V> lookupField(Class<T> declarer, Class<V> type, String name) {
		return new Var(declarer, type, name);
	}
	
	/**
	 * Finds a hook into a field by its string descriptor.
	 */
	public static <T, V> Var<T, V> lookupField(String descriptor) {
		return new Var(descriptor);
	}
	
	/**
	 * Finds a hook into a method by a given BLOBF mapping.
	 * <p>
	 * This version does not support lambda creation.
	 */
	public static <I, R> SimpleFunc<I, R> lookupMethod(BLOBF obf) {
		return new SimpleFunc(obf);
	}
	
	/**
	 * Finds a hook into a method by its declaring class, return type, name, and parameter types.
	 * <p>
	 * This version does not support lambda creation.
	 *  
	 * @param declarer			The class in which this method is declared.
	 * @param returnType		The method return type
	 * @param name				The method name
	 * @param pars				The parameter types this method takes
	 */
	public static <I, R> SimpleFunc<I, R> lookupMethod(Class<I> declarer, Class<R> returnType, String name, Class... pars) {
		return new SimpleFunc(declarer, returnType, name, pars);
	}
	
	/**
	 * Finds a hook into a method by a given BLOBF mapping.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access.
	 */
	public static <I, T, R> Func<I, T, R> lookupMethod(Class<T> interfaceType, BLOBF obf) {
		return new Func(interfaceType, obf);
	}
	
	/**
	 * Finds a hook into a method by its declaring class, return type, name, and parameter types.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access. 
	 * @param declarer			The class in which this method is declared.
	 * @param returnType		The method return type
	 * @param name				The method name
	 * @param pars				The parameter types this method takes
	 */
	public static <I, T, R> Func<I, T, R> lookupMethod(Class<T> interfaceType, Class<I> declarer, Class<R> returnType, String name, Class... pars) {
		return new Func(interfaceType, declarer, returnType, name, pars);
	}
	
	/**
	 * Finds a hook into a method by its string descriptor.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access.
	 */
	public static <I, T, R> Func<I, T, R> lookupMethod(Class<T> interfaceType, String descriptor) {
		return new Func(interfaceType, descriptor);
	}
	
	/**
	 * Finds a hook into a method by its string descriptor.
	 * <p>
	 * This version does not support lambda creation.
	 */
	public static <I, R> SimpleFunc<I, R> lookupMethod(String descriptor) {
		return new SimpleFunc(descriptor);
	}
	
	/**
	 * Finds a hook into a static field by a given BLOBF mapping.
	 */
	public static <T, V> Var<T, V> lookupStaticField(BLOBF obf) {
		return new Var(true, obf);
	}
	
	/**
	 * Finds a hook into a static field by its declaring class, field type and field name.
	 * 
	 * @param declarer	The class in which this field is declared
	 * @param type		The type of values this field takes
	 * @param name		The name of this field
	 */
	public static <T, V> Var<T, V> lookupStaticField(Class<T> declarer, Class<V> type, String name) {
		return new Var(declarer, type, true, name);
	}
	
	/**
	 * Finds a hook into a static field by its string descriptor.
	 */
	public static <T, V> Var<T, V> lookupStaticField(String descriptor) {
		return new Var(true, descriptor);
	}
	
	/**
	 * Finds a hook into a static method by a given BLOBF mapping.
	 * <p>
	 * This version does not support lambda creation.
	 */
	public static <I, R> SimpleFunc<I, R> lookupStaticMethod(BLOBF obf) {
		return new SimpleFunc(true, obf);
	}
	
	/**
	 * Finds a hook into a static method by its declaring class, return type, name, and parameter types.
	 * <p>
	 * This version does not support lambda creation.
	 *  
	 * @param declarer			The class in which this method is declared.
	 * @param returnType		The method return type
	 * @param name				The method name
	 * @param pars				The parameter types this method takes
	 */
	public static <I, R> SimpleFunc<I, R> lookupStaticMethod(Class<I> declarer, Class<R> returnType, String name, Class... pars) {
		return new SimpleFunc(declarer, returnType, name, true, pars);
	}
	
	/**
	 * Finds a hook into a static method by a given BLOBF mapping.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access.
	 */
	public static <I, T, R> Func<I, T, R> lookupStaticMethod(Class<T> interfaceType, BLOBF obf) {
		return new Func(interfaceType, true, obf);
	}
	
	/**
	 * Finds a hook into a static method by its declaring class, return type, name, and parameter types.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access. 
	 * @param declarer			The class in which this method is declared.
	 * @param returnType		The method return type
	 * @param name				The method name
	 * @param pars				The parameter types this method takes
	 */
	public static <I, T, R> Func<I, T, R> lookupStaticMethod(Class<T> interfaceType, Class<I> declarer, Class<R> returnType, String name, Class... pars) {
		return new Func(interfaceType, declarer, returnType, name, true, pars);
	}
	
	/**
	 * Finds a hook into a static method by its string descriptor.
	 * 
	 * @param interfaceType		An interface for the generated lambda to implement. Must contain a method matching the one you wish to access.
	 */
	public static <I, T, R> Func<I, T, R> lookupStaticMethod(Class<T> interfaceType, String descriptor) {
		return new Func(interfaceType, true, descriptor);
	}
	
	/**
	 * Finds a hook into a static method by its string descriptor.
	 * <p>
	 * This version does not support lambda creation.
	 */
	public static <I, R> SimpleFunc<I, R> lookupStaticMethod(String descriptor) {
		return new SimpleFunc(true, descriptor);
	}
}
