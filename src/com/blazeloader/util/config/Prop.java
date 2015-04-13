package com.blazeloader.util.config;

import java.util.List;

public class Prop<T> implements IProperty<T> {
	private final IConfig cfg;
	
	private Class typeClass;
	private IWrapObject<T> currentValue;
	private IWrapObject<T> defaultValue;
	
	private final String propertyName;
	
	private String description = "";
	
	protected boolean loaded = false;
	
	protected Prop(IConfig config, List<String> lines) {
		cfg = config;
		defaultValue = new StringableObject(null);
		currentValue = new StringableObject(null);
		checkForComment(lines);
		String first = cfg.popNextLine(lines);
		String def = null;
		if (first.startsWith("@default: ")) {
			def = first.substring("@default:".length(), first.length()).split("\\(")[0].trim();
		}
		checkForComment(lines);
		first = cfg.popNextLine(lines);
		String[] remain = first.split(":");
		propertyName = remain[0].trim();
		String value = "";
		for (int i = 1; i < remain.length; i++) {
			value += remain[i];
		}
		if (def != null) {
			defaultValue.set(unescapeValue(def));
			currentValue.set(defaultValue.get());
		}
		if (!value.trim().isEmpty()) {
			currentValue.set(unescapeValue(value));
		}
		typeClass = defaultValue.get().getClass();
		loaded = true;
	}
		
	protected Prop(IConfig config, String name, T def) {
		cfg = config;
		typeClass = def.getClass();
		propertyName = cfg.applyNameRegexString(name);
		
		if (typeClass.isArray()) {
			defaultValue = new StringableArray((T[])def); 
			currentValue = new StringableArray((T[])defaultValue.get());
		} else {
			defaultValue = new StringableObject(def);
			currentValue = new StringableObject(def);
		}
	}
	
	public String getName() {
		return propertyName;
	}
	
	public void setDefault(T newDef) {
		defaultValue.set(newDef);
	}
	
	public T getDefault() {
		return defaultValue.get();
	}
	
	public void reset() {
		currentValue.set(defaultValue.get());
	}
	
	public T get() {
		return currentValue.get();
	}
	
	public void set(T val) {
		currentValue.set(val);
	}
	
	public Class getType() {
		return typeClass;
	}
	
	public T[] getPossibleValues() {
		if (defaultValue.get() instanceof Boolean) {
			return (T[])new Boolean[] {true,false};
		} else if (typeClass.isEnum()) {
			return (T[])typeClass.getEnumConstants();
		}
		return null;
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
	
	protected void updateType(T def) {
		if (typeClass != def.getClass()) {
			typeClass = def.getClass();
			if (currentValue.get() instanceof String) {
				if (typeClass.isArray()) {
					currentValue = new StringableArray((T[])def, (String)currentValue.get());
					defaultValue = new StringableArray((T[])def);
				} else {
					currentValue.fromString(def, ((String)currentValue.get()));
					defaultValue.set(def);
				}
			}
		}
	}
	
	protected void writeTo(StringBuilder builder) {
		if (!description.isEmpty()) {
			String[] descriptions = description.split("\n");
			for (int i = 0; i < descriptions.length; i++) {
				builder.append("   #");
				builder.append(descriptions[i].trim());
				builder.append("\r\n");
			}
		}
		builder.append("   @default: ");
		
		if (typeClass == String.class) {
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
		builder.append("\r\n   ");
		builder.append(propertyName);
		builder.append(": ");
		if (typeClass == String.class) {
			builder.append("\"" + currentValue.toString() + "\"");
		} else {
			builder.append(currentValue.toString());
		}
	}
	
	private T unescapeValue(String value) {
		value = value.trim();
		if (value.startsWith("\"")) value = value.substring(1, value.length());
		if (value.endsWith("\"")) value = value.substring(0, value.length() - 1);
		return (T)value;
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
}
