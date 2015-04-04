package com.blazeloader.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ToolSword extends ItemSword {
    private final ToolSetAttributes attributes;

    private float damageValue = 4;

    public ToolSword(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
        damageValue = material.getDamageVsEntity(4);
    }

    @Override
    public float getDamageVsEntity() {
        return attributes.getDamageVsEntity(0);
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
        return attributes.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Weapon modifier");
    }
}
