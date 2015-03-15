package com.blazeloader.api.toolset;

import net.minecraft.item.ItemHoe;

public class ToolHoe extends ItemHoe {
    private final ToolSetAttributes my_material;

    public ToolHoe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        my_material = material;
        super.setMaxDamage(material.getMaxUses());
    }

    public String getMaterialName() {
        return my_material.toString();
    }
}
