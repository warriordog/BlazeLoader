package com.blazeloader.api.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ApiRenderItem {
	
	public static void registerBlock(Block block, String identifier) {
		registerBlock(block, 0, identifier);
	}
	
	public static void registerItem(Item item, String identifier) {
		registerItem(item, 0, identifier);
	}
	
	public static void registerBlock(Block block, int subType, String identifier) {
		registerItem(Item.getItemFromBlock(block), subType, identifier);
	}
	
	public static void registerItem(Item item, int subType, String identifier) {
		Minecraft.getMinecraft().renderItem.getItemModelMesher().register(item, subType, new ModelResourceLocation(identifier, "inventory"));
	}
	
	public static void registerItem(Item item, ItemMeshDefinition mesh) {
		Minecraft.getMinecraft().renderItem.getItemModelMesher().register(item, mesh);
	}
}
