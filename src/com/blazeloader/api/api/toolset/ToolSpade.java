package com.blazeloader.api.api.toolset;

import com.blazeloader.api.util.java.Reflect;
import com.blazeloader.api.util.obf.BLOBF;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public class ToolSpade extends ItemSpade {
    private final ToolSetAttributes my_material;

    public static final HashSet<Block> effectiveBlocks = Reflect.getFieldValue(ItemSpade.class, null, BLOBF.getFieldMCP("net.minecraft.item.ItemSpade.field_150916_c").getValue());

    private float damageValue = 4;

    public ToolSpade(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        setMaxDurability(material.getMaxUses());
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
