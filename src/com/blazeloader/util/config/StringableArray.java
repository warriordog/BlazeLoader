package com.blazeloader.util.config;

import com.google.gson.Gson;

public class StringableArray<T> implements IWrapObject<T[]> {
	private final Gson gson = new Gson();
	private T[] array;
	
	public StringableArray(T[] def) {
		array = def;
	}
	
	public StringableArray(T[] def, String data) {
		fromString(def, data);
	}
	
	public String toString() {
		return gson.toJson(array);
	}
	
	public T[] get() {
		return array;
	}
	
	public void set(T[] arr) {
		array = arr;
	}
	
	public void fromString(T[] def, String value) {
		try {
			array = (T[])gson.fromJson(value, def.getClass());
		} catch (Throwable e) {
			e.printStackTrace();
			array = def;
		}
	}
}
