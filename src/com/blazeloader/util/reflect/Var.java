package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

/**
 * Wrapper for getting and setting a variable.
 *
 * @param <T>	The declaring class for this field.
 * @param <V>	The value type accepted and returned by this field.
 */
public class Var<T, V> extends Variable<T, V> {
	
	public Var(Class<T> declarer, Class<V> type, String name) {
		this(declarer, type, false, name);
	}
	
	public Var(Class<T> declarer, Class<V> type, boolean isStatic, String name) {
		super(declarer, type, isStatic, name);
	}
	
	public Var(BLOBF obf) {
		this(false, obf);
	}
	
	public Var(boolean isStatic, BLOBF obf) {
		super(isStatic, obf);
	}
	
	public Var(String descriptor) {
		this(false, descriptor);
	}
	
	public Var(boolean isStatic, String descriptor) {
		super(isStatic, descriptor);
	}
	
	protected Var(Variable<T,V> original) {
		super(original);
	}
	
	/**
	 * Attempts to get the underlying value. Will return the default value if it fails.
	 * 
	 * @param instance	The instance to act on
	 * @param def		A default value to return if it fails.
	 * @return	T object referenced by this field
	 */
	public V get(T instance, V def) {
		return _get(instance, def);
	}
	
	/**
	 * Attempts to set the underlying value.
	 * 
	 * @param instance	The instance to act on
	 * @param val		The value to assign to the underlying field
	 */
	public void set(T instance, V val) {
		_set(instance, val);
	}
	
	/**
	 * Attempts to get a copy of this field bound to the given instance.
	 * <br>
	 * Will simply return {@code this} if it is a static field.
	 * 
	 * @param instance	The object instance to bind to
	 * 
	 * @return The bound field or {@code this} if static.
	 */
	public Variable<T, V> bindTo(T instance) {
		if (handle.staticField) {
			return this;
		}
		return new BufferedVar<T, V>(instance, this);
	}
}