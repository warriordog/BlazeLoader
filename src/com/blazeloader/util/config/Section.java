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
		checkForComment(lines);
		String first = cfg.popNextLine(lines);
		sectionName = first.substring(0, first.length() - 2);
		do {
			try {
				Prop next = new Prop(cfg, lines);
				if (next.loaded) {
					properties.put(next.propertyName, next);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} while (lines.size() > 0 && lines.get(0).trim().indexOf("}") != 0);
		lines.remove(0);
		loaded = true;
	}
	
	public Section(IConfig config, String name) {
		cfg = config;
		sectionName = cfg.applyNameRegexString(name);
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
			Prop<T> result = properties.get(key);
			if (!def.getClass().isAssignableFrom(result.getDefault().getClass())) {
				result.updateType(def);
			}
			return result;
		}
		
		Prop<T> result = new Prop<T>(cfg, key, def);
		properties.put(key, result);
		return result;
	}
	
	protected void writeTo(StringBuilder builder) {
		if (!description.isEmpty()) {
			String[] descriptions = description.split("\n");
			for (int i = 0; i < descriptions.length; i++) {
				builder.append("#");
				builder.append(descriptions[i].trim());
				builder.append("\r\n");
			}
		}
		builder.append(sectionName);
		builder.append(" {\r\n");
		for (Prop i : properties.values()) {
			i.writeTo(builder);
			builder.append("\r\n");
		}
		builder.append("}");
	}
}
