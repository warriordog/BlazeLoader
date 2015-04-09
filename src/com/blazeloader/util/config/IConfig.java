package com.blazeloader.util.config;

import java.io.File;
import java.util.List;

/**
 * Wrapper object for a config file. Allows loading and saving of properties.
 */
public interface IConfig {
	
	/**
	 * Loads the the contents from the given file if it exists.
	 */
	public void load(File file);
	
	/**
	 * Saves all keys and values to the underlying file.
	 */
	public void save();
	
	/**
	 * Checks if a value with the given name exists in a section of the given section name.
	 * 
	 * @param section	Name of section to look in.
	 * @param name		Name of property to look for.
	 * 
	 * @return true if both the section exists and it contains a property by the given name.
	 */
	public boolean has(String section, String name);
	
	/**
	 * Gets a property for the given section and name. Creates both if they do not exist and initialises the property to the given default value.
	 * 
	 * @param section		Name of the section
	 * @param name			Name of the property
	 * @param defaultValue	The default value of the property
	 * 
	 * @return	A property object for the given keys.
	 */
	public <T> IProperty<T> getProperty(String section, String name, T defaultValue);
	
	/**
	 * Gets a section for the given section name.
	 */
	public IPropertyGroup getSection(String section);
	
	/**
	 * Applies a regex to cleanup property names.
	 * @param name Name to clean
	 * @return The now safe to use name.
	 */
	public String applyNameRegexString(String name);
	
	/**
	 * Applies a regex to cleanup a description/comment.
	 * @param description The description to clean
	 * @return The noew safe to use description
	 */
	public String applyDescriptionRegexString(String description);
	
	/**
	 * Gets the next valid line to read from the properties file.
	 * @param lines List of lines available
	 */
	public String popNextLine(List<String> lines);
}
