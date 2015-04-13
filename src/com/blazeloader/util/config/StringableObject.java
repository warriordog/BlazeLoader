package com.blazeloader.util.config;

import java.lang.reflect.Method;

public class StringableObject<T> implements IWrapObject<T> {
	
	private T object;
	
	public StringableObject(T obj) {
		object = obj; 
	}
	
	public String toString() {
		return object.toString();
	}
		
	public T get() {
		return object;
	}
	
	public void set(T value) {
		object = value;
	}
	
	public void fromString(T def, String value) {
		if (def != null) {
			Class typeClass = def.getClass();
			try {
				if (typeClass.isEnum()) {
					object = (T)Enum.valueOf(typeClass, value);
				} else if (IStringable.class.isAssignableFrom(def.getClass())) {
					object = (T)((IStringable)def).valueOf(value);
				} else {
					Method m = typeClass.getMethod("valueOf", String.class);
					if (!m.isAccessible()) {
						m.setAccessible(true);
					}
					object = (T)m.invoke(typeClass.newInstance(), value);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				object = def;
			}
		}
	}
}
