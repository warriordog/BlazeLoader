package com.blazeloader.api;

import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.util.config.IStringable;
import com.blazeloader.util.config.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A container for different types of mod brands
 *
 */
public class Brand implements IStringable<Brand> {
	protected String string;
	protected boolean tech;
	
	protected Brand(String name, boolean technical) {
		string = name;
		tech = technical;
	}
	
	/**
	 * Gets the display name for this brand.
	 */
	public String getString() {
		return string;
	}

	/**
	 * Returns true if this is a technical brand that must not be displayed.
	 */
	public boolean isTechnical() {
		return tech;
	}
	
	public String toString() {
		return "{ brand: \"" + getString() + "\", hidden: " + isTechnical() + " }";
	}
	
	public Brand fromString(String string) {
		return valueOf(string);
	}
	
	public static Brand valueOf(String string) {
		JsonObject json = JsonUtils.parseJSONObj(string);
		if (json != null) {
			if (json.has("brand") && json.has("hidden")) {
				return new Brand(json.get("brand").getAsString(), json.get("hidden").getAsBoolean());
			}
		}
		return null;
	}
}
