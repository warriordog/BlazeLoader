package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public class ToolSpade extends ItemSpade {
    private final ToolSetAttributes attributes;

    private float damageValue = 4;

    public ToolSpade(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        damageValue = material.getDamageVsEntity(1);
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
}
