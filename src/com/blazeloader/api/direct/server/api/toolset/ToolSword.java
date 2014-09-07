package com.blazeloader.api.direct.server.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ToolSword extends ItemSword {
    private final ToolSetAttributes my_material;

    private float damageValue = 4;

    public ToolSword(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        setMaxDurability(material.getMaxUses());
        damageValue = material.getDamageVsEntity(4);
    }

    public float func_150931_i() {
        return my_material.getDamageVsEntity(0);
    }

    public int getItemEnchantability() {
        return my_material.getEnchantability();
    }

    public String func_150932_j() {
        return my_material.toString();
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return my_material.getIsRepairable(par2ItemStack);
    }

    public Multimap getItemAttributeModifiers() {
        return my_material.getAttributeModifiers(super.getItemAttributeModifiers(), field_111210_e, damageValue, "Weapon modifier");
    }
}
