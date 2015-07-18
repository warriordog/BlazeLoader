package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

public abstract class Variable<T, V> {
	
	protected final VarHandle handle;
	
	public Variable(Class<T> declarer, Class<V> type, boolean isStatic, String name) {
		handle = new VarHandle(declarer, type, isStatic, name);
	}
	
	public Variable(boolean isStatic, BLOBF obf) {
		this(isStatic, obf.getValue());
	}
	
	public Variable(boolean isStatic, String descriptor) {
		handle = new VarHandle(isStatic, descriptor);
	}
	
	protected Variable(Variable<T,V> original) {
		handle = original.handle;
	}
	
	protected V _get(T instance, V def) {
		if (handle.get != null) {
			try {
				return (V)handle.get.invoke(instance);
			} catch (Throwable e) {
				handle.get = null;
			}
		}
		return handleGetFail(instance, def);
	}
	
	protected void _set(T instance, V val) {
		if (handle.set != null) {
			try {
				handle.set.invoke(instance, val);
				return;
			} catch (Throwable e) {
				handle.set = null;
			}
		}
		handleSetFail(instance, val);
	}
	
	/**
	 * Marks this as broken.
	 */
	public void invalidate() {
		handle.invalidate();
	}
	
	/**
	 * Checks if this is still a functional method.
	 * @return true if you can safely call it.
	 */
	public boolean valid() {
		return handle.valid();
	}
	
	/**
	 * Gets the bytecode descriptor for the underlying field
	 */
	public String descriptor() {
		return handle.descriptor();
	}
	
	/**
	 * Gets the name of the field represented by this Variable
	 */
	public String name() {
		return handle.varName;
	}
	
	protected V handleGetFail(T instance, V def) {
		return def;
	}
	
	protected void handleSetFail(T instance, V val) {
		
	}
	
	public String toString() {
		return handle.toString();
	}
}
