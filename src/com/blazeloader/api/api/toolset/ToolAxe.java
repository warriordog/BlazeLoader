package com.blazeloader.api.api.toolset;

import com.blazeloader.api.util.java.Reflect;
import com.blazeloader.api.util.obf.BLOBF;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public class ToolAxe extends ItemAxe {
    private final ToolSetAttributes my_material;

    public static final HashSet<Block> effectiveBlocks = Reflect.getFieldValue(ItemAxe.class, null, BLOBF.getFieldMCP("net.minecraft.item.ItemAxe.field_150917_c").getValue());

    private float damageValue = 4;

    public ToolAxe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        setMaxDurability(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(3);
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
