package com.blazeloader.api.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

public class ItemRegistry {
	private static ItemRegistry instance = new ItemRegistry();
	
	private static Map<Item, ArrayList<String>> variantNames = new HashMap<Item, ArrayList<String>>();
	
	public static ItemRegistry instance() {
		return instance;
	}
	
	protected void registerVariantNames(Item item, ArrayList<String> variants) {
		variantNames.put(item, variants);
	}
	
	public void insertItemVariantNames(Map<Item, ArrayList<String>> mapping) {
		mapping.putAll(variantNames);
	}
}
