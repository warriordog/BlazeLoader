package com.blazeloader.util.config;

import com.google.gson.Gson;

/**
 * A wrapper class responsible for converting arrays of items to and from strings.
 *
 * @param <T> The element type
 */
public class StringableArray<T> implements IWrapObject<T[]> {
	private final Gson gson = new Gson();
	private T[] array;
	
	public StringableArray() {
		this(null);
	}
	
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
	
	/**
	 * Returns a {@code StringableArray} with the value of the given string.
	 * @param value		The string to be parsed
	 * @return A StringableArray with the value of the given string.
	 */
	public static <T> StringableArray<T> valueOf(String value) {
		StringableArray<T> result = new StringableArray<T>();
		result.fromString(null, value);
		return result;
	}
	
	public StringableArray<T> fromString(String string) {
		return valueOf(string);
	}
}
