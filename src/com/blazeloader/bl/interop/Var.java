package com.blazeloader.bl.interop;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import net.minecraft.world.World;

import com.blazeloader.util.version.Versions;

/**
 * Wrapper for getting and setting a variable.
 *
 * @param <T>	The declaring class for this field.
 * @param <V>	The value type accepted and returned by this field.
 */
public class Var<T, V> {
	
	private MethodHandle get;
	private MethodHandle set;
	
	protected Var(Var<T,V> original) {
		get = original.get;
		set = original.set;
	}
	
	public Var(Class<T> declarer, Class<V> type, String name) {
		if (Versions.isForgeInstalled()) {
			Field field;
    		try {
				field = declarer.getDeclaredField(name);
			} catch (Throwable e) {
				field = null;
			}
    		if (field != null) {
    			MethodHandles.Lookup lookup = MethodHandles.lookup();
    			try {
					get = lookup.findGetter(World.class, name, type);
					set = lookup.findSetter(World.class, name, type);
				} catch (Throwable e) {
					get = set = null;
				}
    		}
		}
	}
	
	/**
	 * Attempts to get the underlying value. Will return the default value if it fails.
	 * 
	 * @param instance	The instance to act on
	 * @param def		A default value to return if it fails.
	 * @return	T object referenced by this field
	 */
	public V get(T instance, V def) {
		if (get != null) {
			try {
				return (V)get.invoke(instance);
			} catch (Throwable e) {
				get = null;
			}
		}
		return handleGetFail(instance, def);
	}
	
	/**
	 * Attempts to set the underlying value.
	 * 
	 * @param instance	The instance to act on
	 * @param val		The value to assign to the underlying field
	 */
	public void set(T instance, V val) {
		if (set != null) {
			try {
				set.invoke(instance, val);
			} catch (Throwable e) {
				handleSetFail(instance, val);
			}
		}
	}
	
	protected V handleGetFail(T instance, V def) {
		return def;
	}
	
	protected void handleSetFail(T instance, V val) {
		set = null;
	}
}