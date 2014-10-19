package com.blazeloader.api.direct.server.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;


public class ToolAxe extends ItemAxe {
    private final ToolSetAttributes my_material;

    private float damageValue = 4;

    public ToolAxe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        super.setMaxDamage(material.getMaxUses());
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
        return my_material.getAttributeModifiers(super.getItemAttributeModifiers(), null, damageValue, "Tool modifier");
    }
}
