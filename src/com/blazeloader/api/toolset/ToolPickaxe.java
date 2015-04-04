package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ToolPickaxe extends ItemPickaxe {
    private final ToolSetAttributes attributes;

    private float damageValue = 4;

    public ToolPickaxe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(2);
    }

    @Override
    public int getItemEnchantability() {
        return attributes.getEnchantability();
    }

    @Override
    public String getToolMaterialName() {
        return attributes.toString();
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return attributes.getIsRepairable(par2ItemStack);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }

    @Override
    public boolean canHarvestBlock(Block p_150897_1_) {
        return p_150897_1_ == Blocks.obsidian ? attributes.getHarvestLevel() == 3 : (p_150897_1_ != Blocks.diamond_block && p_150897_1_ != Blocks.diamond_ore ? (p_150897_1_ != Blocks.emerald_ore && p_150897_1_ != Blocks.emerald_block ? (p_150897_1_ != Blocks.gold_block && p_150897_1_ != Blocks.gold_ore ? (p_150897_1_ != Blocks.iron_block && p_150897_1_ != Blocks.iron_ore ? (p_150897_1_ != Blocks.lapis_block && p_150897_1_ != Blocks.lapis_ore ? (p_150897_1_ != Blocks.redstone_ore && p_150897_1_ != Blocks.lit_redstone_ore ? (p_150897_1_.getMaterial() == Material.rock || (p_150897_1_.getMaterial() == Material.iron || p_150897_1_.getMaterial() == Material.anvil)) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 1) : attributes.getHarvestLevel() >= 1) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 2) : attributes.getHarvestLevel() >= 2);
    }
}
