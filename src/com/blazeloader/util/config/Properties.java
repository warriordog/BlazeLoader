package com.blazeloader.util.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Properties implements IConfig {
	private final HashMap<String, Section> sections = new HashMap<String, Section>();
	
	private File file;
	
	public Properties(File file) {
		load(file);
	}
	
	public void load(File file) {
		if (file.exists() && !(file.canRead() && file.isFile())) {
			throw new IllegalArgumentException("Given file is not a file or is not accessible.");
		}
		this.file = file;
		try {
			if (file.exists()) {
				List<String> lines = FileUtils.readLines(file);
				readFrom(lines);
			} else {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			FileWriter writer = new FileWriter(file);
			writeTo(writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean hasSection(String section) {
		return sections.containsKey(section);
	}
	
	public boolean has(String section, String name) {
		if (hasSection(section)) {
			return sections.get(section).has(name);
		}
		return false;
	}
	
	public <T> Prop<T> getProperty(String section, String name, T defaultValue) {
		return getSection(section).get(name, defaultValue);
	}
	
	public Section getSection(String section) {
		if (hasSection(section)) {
			return sections.get(section);
		}
		Section result = new Section(this, section);
		sections.put(section, result);
		return result;
	}
	
	protected void writeTo(FileWriter writer) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (Section i : sections.values()) {
			i.writeTo(builder);
			builder.append("\r\n");
		}
		writer.append(builder.toString());
	}
	
	
	protected void readFrom(List<String> lines) {
		while (lines.size() > 0) {
			try {
				Section section = new Section(this, lines);
				if (section.loaded) {
					sections.put(section.getName(), section);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public String applyNameRegexString(String name) {
		return name.replaceAll("<|>|\t(\t)*|\n(\n)*|\r(\r)*| ", "_");
	}
	
	public String applyDescriptionRegexString(String description) {
		return description.replaceAll("\t(\t)*", " ");
	}
	
	public String popNextLine(List<String> lines) {
		String next = "";
		do {
			next = lines.remove(0).trim();
		} while (next.isEmpty());
		return next;
	}
}
