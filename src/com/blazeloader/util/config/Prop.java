package com.blazeloader.util.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Enums;

public class Prop<T> implements IProperty<T> {
	private final IConfig cfg;
	
	private static final Map<Class, String> classToType = new HashMap<Class, String>();
	private static final Map<String, Class> typeToClass = new HashMap<String, Class>();
	
	private final Class typeClass;
	private T currentValue;
	private T defaultValue;
	
	protected final String propertyName;
	
	private String description = "";
	
	protected boolean loaded = false;
	
	protected Prop(IConfig config, List<String> lines) {
		cfg = config;
		checkForComment(lines);
		String first = cfg.popNextLine(lines);
		String def = null;
		if (first.startsWith("@default: ")) {
			def = first.substring("@default:".length(), first.length()).split("(")[0].trim();
		}
		checkForComment(lines);
		first = cfg.popNextLine(lines);
		propertyName = first.split("<")[0];
		String[] remain = first.substring(propertyName.length() + 1).split(">:");
		String type = remain[0].substring(0, remain[0].length());
		String value = "";
		for (int i = 1; i < remain.length; i++) {
			value += remain[i];
		}
		if (def != null) {
			defaultValue = currentValue = (T)parseValue(type, def.trim());
		}
		currentValue = (T)parseValue(type, value.trim());
		typeClass = defaultValue.getClass();
		loaded = true;
	}
	
	private void checkForComment(List<String> lines) {
		while (lines.get(0).trim().startsWith("#")) {
			String first = cfg.popNextLine(lines);
			if (!description.isEmpty()) {
				description += "\r\n";
			}
			description += first.substring(1, first.length());
		}
	}
	
	protected Prop(IConfig config, String name, T def) {
		cfg = config;
		typeClass = def.getClass();
		propertyName = cfg.applyNameRegexString(name);
		defaultValue = def;
		currentValue = def;
	}
	
	public String getName() {
		return propertyName;
	}
	
	public void setDefault(T newDef) {
		defaultValue = newDef;
	}
	
	public void reset() {
		set(defaultValue);
	}
	
	public T get() {
		return currentValue;
	}
	
	public T[] getPossibleValues() {
		if (defaultValue instanceof Boolean) {
			return (T[])new Boolean[] {true,false};
		} else if (typeClass.isEnum()) {
			return (T[])typeClass.getEnumConstants();
		}
		return null;
	}
	
	public void set(T val) {
		currentValue = val;
	}
	
	public void setDescription(String... desc) {
		StringBuilder full = new StringBuilder();
		for (String i : desc) {
			full.append(i);
			full.append("\r\n");
		}
		if (desc == null || desc.length == 0 || full.toString().isEmpty()) {
			description = "";
		} else {
			description = cfg.applyDescriptionRegexString(full.toString().trim());
		}
	}
	
	public String getType() {
		if (defaultValue instanceof String) return "S";
		if (defaultValue instanceof Integer) return "I";
		if (defaultValue instanceof Float) return "F";
		if (defaultValue instanceof Character) return "C";
		if (defaultValue instanceof Boolean) return "B";
		return getType(defaultValue.getClass());
	}
	
	protected void writeTo(StringBuilder builder) {
		if (!description.isEmpty()) {
			String[] descriptions = description.split("\n");
			for (int i = 0; i < descriptions.length; i++) {
				builder.append("\t#");
				builder.append(descriptions[i].trim());
				builder.append("\r\n");
			}
		}
		builder.append("\t@default: ");
		String type = getType();
		if (type.contentEquals("S")) {
			builder.append("\"" + defaultValue.toString() + "\"");
		} else {
			builder.append(defaultValue.toString());
		}
		T[] possibles = getPossibleValues();
		if (possibles != null) {
			builder.append(" (");
			for (int i = 0; i < possibles.length; i++) {
				if (i > 0) builder.append(", ");
				builder.append(possibles[i].toString());
			}
			builder.append(")");
		}
		builder.append("\r\n\t");
		builder.append(propertyName);
		if (!"~null~".contentEquals(type)) {
			builder.append("<");
			builder.append(type);
			builder.append(">");
		}
		builder.append(": ");
		if (type.contentEquals("S")) {
			builder.append("\"" + currentValue.toString() + "\"");
		} else {
			builder.append(currentValue.toString());
		}
	}
	
	private static Object parseValue(String type, String value) {
		if (type.endsWith("[]")) {
			type = type.substring(0, type.length() - 2);
			String[] values = value.substring(1, value.length() -1).split(", ");
			Object[] arr = new Object[values.length];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = parseValue(type, values[i]);
			}
			return arr;
		}
		if ("S".contentEquals(type)) {
			if (value.startsWith("\"")) value = value.substring(1, value.length());
			if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
			return value;
		}
		if ("I".contentEquals(type)) return Integer.valueOf(value);
		if ("F".contentEquals(type)) return Float.valueOf(value);
		if ("C".contentEquals(type)) return Character.valueOf(value.toCharArray()[0]);
		if ("B".contentEquals(type)) return Character.valueOf(value.toCharArray()[0]);
		Class typeClass = getTypeClass(type);
		if (typeClass != null) {
			try {
				if (IStringable.class.isAssignableFrom(typeClass)) {
					return ((IStringable)typeClass.newInstance()).valueOf(value);
				} else {
					Method m = typeClass.getMethod("valueOf", String.class);
					if (!m.isAccessible()) {
						m.setAccessible(true);
					}
					return m.invoke(typeClass.newInstance(), value);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static boolean hasType(Class type) {
		return classToType.containsKey(type);
	}
	
	public static boolean hasType(String typeString) {
		return typeToClass.containsKey(typeString);
	}
	
	public static String getType(Class type) {
		if (hasType(type)) {
			return classToType.get(type);
		} else {
			if (type.isArray()) {
				return getType(type.getComponentType()) + "[]";
			}
		}
		return "~null~";
	}
	
	public static Class getTypeClass(String typeString) {
		if (hasType(typeString)) {
			return typeToClass.get(typeString);
		}
		return null;
	}
	
	public static void registerType(Class<? extends IStringable> type, String typeString) {
		classToType.put(type, typeString);
		typeToClass.put(typeString, type);
	}
}
