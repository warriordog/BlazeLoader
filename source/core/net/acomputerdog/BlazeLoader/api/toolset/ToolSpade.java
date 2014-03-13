package net.acomputerdog.BlazeLoader.api.toolset;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.acomputerdog.BlazeLoader.util.reflect.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

public class ToolSpade extends ItemSpade {
	private final ToolSetAttributes my_material;
	
	public static final HashSet<Block> effectiveBlocks = (HashSet<Block>)ReflectionUtils.getField(ItemSpade.class, null, 0).get();
	
	private float damageValue = 4;
	public ToolSpade(ToolSetAttributes material) {
		super(ToolMaterial.WOOD);
		my_material = material; 
		setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(1);
	}

    public int getItemEnchantability() {
        return my_material.getEnchantability();
    }

    public String getToolMaterialName() {
        return my_material.toString();
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return my_material.getIsRepairable(par2ItemStack);
    }

    public Multimap getItemAttributeModifiers() {
        return my_material.getAttributeModifiers(super.getItemAttributeModifiers(), field_111210_e, damageValue, "Tool modifier");
    }
}
