package com.blazeloader.util.config;

import java.util.HashMap;
import java.util.List;

public class Section implements IPropertyGroup {
	private final HashMap<String, Prop> properties = new HashMap<String, Prop>();
	
	private final IConfig cfg;
	
	private String description = "";
	
	private String sectionName;
	protected boolean loaded = false;
	
	public Section(IConfig config, List<String> lines) {
		cfg = config;
		String first = lines.get(0);
		lines.remove(0);
		if (first.startsWith("#")) {
			description = first.substring(1, first.length());
			first = lines.get(0);
		}
		sectionName = first.substring(0, first.length() - 2);
		do {
			Prop next = new Prop(cfg, lines);
			if (next.loaded) {
				properties.put(next.propertyName, next);
			}
		} while (lines.get(0).indexOf("}") != 0);
		loaded = true;
	}
	
	public Section(IConfig config, String sectionName) {
		cfg = config;
		sectionName = cfg.applyNameRegexString(sectionName);
	}
	
	
	public String getName() {
		return sectionName;
	}
	
	public void setDescription(String desc) {
		if (desc == null) {
			description = "";
		} else {
			description = cfg.applyDescriptionRegexString(desc);
		}
	}
	
	public boolean has(String key) {
		return properties.containsKey(key);
	}
	
	public <T> Prop<T> get(String key, T def) {
		if (has(key)) {
			return properties.get(key);
		}
		
		Prop<T> result = new Prop<T>(cfg, key, def);
		properties.put(key, result);
		return result;
	}
	
	protected void writeTo(StringBuilder builder) {
		builder.append(sectionName);
		builder.append(" {\n");
		for (Prop i : properties.values()) {
			i.writeTo(builder);
			builder.append("\n");
		}
		builder.append("}");
	}
}
