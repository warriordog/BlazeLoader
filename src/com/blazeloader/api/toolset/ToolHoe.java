package com.blazeloader.api.toolset;

import net.minecraft.item.ItemHoe;

public class ToolHoe extends ItemHoe {
    private final ToolSetAttributes attributes;

    public ToolHoe(ToolSetAttributes material) {
        super(ToolMaterial.WOOD);
        attributes = material;
        super.setMaxDamage(material.getMaxUses());
    }

    @Override
    public String getMaterialName() {
        return attributes.toString();
    }
}
