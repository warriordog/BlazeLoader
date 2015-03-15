package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ToolPickaxe extends ItemPickaxe {
    private final ToolSetAttributes my_material;

    private float damageValue = 4;

    public ToolPickaxe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(2);
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
        return my_material.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }

    public boolean func_150897_b(Block p_150897_1_) {
        return p_150897_1_ == Blocks.obsidian ? my_material.getHarvestLevel() == 3 : (p_150897_1_ != Blocks.diamond_block && p_150897_1_ != Blocks.diamond_ore ? (p_150897_1_ != Blocks.emerald_ore && p_150897_1_ != Blocks.emerald_block ? (p_150897_1_ != Blocks.gold_block && p_150897_1_ != Blocks.gold_ore ? (p_150897_1_ != Blocks.iron_block && p_150897_1_ != Blocks.iron_ore ? (p_150897_1_ != Blocks.lapis_block && p_150897_1_ != Blocks.lapis_ore ? (p_150897_1_ != Blocks.redstone_ore && p_150897_1_ != Blocks.lit_redstone_ore ? (p_150897_1_.getMaterial() == Material.rock || (p_150897_1_.getMaterial() == Material.iron || p_150897_1_.getMaterial() == Material.anvil)) : my_material.getHarvestLevel() >= 2) : my_material.getHarvestLevel() >= 1) : my_material.getHarvestLevel() >= 1) : my_material.getHarvestLevel() >= 2) : my_material.getHarvestLevel() >= 2) : my_material.getHarvestLevel() >= 2);
    }
}
