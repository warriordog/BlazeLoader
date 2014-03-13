package net.acomputerdog.BlazeLoader.api.toolset;

import java.util.HashSet;

import com.google.common.collect.Multimap;

import net.acomputerdog.BlazeLoader.util.reflect.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;

public class ToolHoe extends ItemHoe {
	private final ToolSetAttributes my_material;
	
	public ToolHoe(ToolSetAttributes material) {
		super(ToolMaterial.WOOD);
		my_material = material; 
		setMaxDamage(material.getMaxUses());
	}

    public String getMaterialName() {
        return my_material.toString();
    }
}
