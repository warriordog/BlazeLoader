package com.blazeloader.bl.interop;

/**
 * A buffered version of Var that keeps a cached copy of
 * the field's value and wraps an instance.  
 *
 * @param <T>	The declaring class for this field.
 * @param <V>	The value type accepted and returned by this field.
 */
public class BufferedVar<T, V> extends Var<T, V> {
	
	private final T instance;
	
	private boolean valueSet;
	private V cached;
	
	protected BufferedVar(T newInstance, Var<T,V> original) {
		super(original);
		instance = newInstance;
	}
	
	public BufferedVar(T newInstance, Class<V> type, String name) {
		super((Class<T>)newInstance.getClass(), type, name);
		instance = newInstance;
	}
	
	protected V handleGetFail(T instance, V def) {
		return valueSet ? cached : def;
	}
	
	/**
	 * Attempts to get the underlying value.
	 * 
	 * @param def		A default value to return if it fails.
	 * @return	T object referenced by this field
	 */
	public V get(V def) {
		valueSet = true;
		return cached = super.get(instance, def);
	}
	
	/**
	 * Attempts to set the underlying value.
	 * 
	 * @param val		The value to assign to the underlying field
	 */
	public void set(V val) {
		valueSet = true;
		cached = val;
		super.set(instance, val);
	}
	
	/**
	 * Creates a new BufferedVar bound to the given instance
	 */
	public BufferedVar<T,V> newWithInstance(T newInstance) {
		return new BufferedVar(newInstance, this);
	}
}
