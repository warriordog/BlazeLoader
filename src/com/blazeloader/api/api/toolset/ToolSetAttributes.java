package com.blazeloader.api.api.toolset;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ToolSetAttributes {
    public static final ToolSetAttributes
            WOOD = new ToolSetAttributes("WOOD", new ItemStack(Blocks.planks), 0, 59, 2.0F, 0.0F, 15),
            STONE = new ToolSetAttributes("STONE", new ItemStack(Blocks.cobblestone), 1, 131, 4.0F, 1.0F, 5),
            IRON = new ToolSetAttributes("IRON", new ItemStack(Items.iron_ingot), 2, 250, 6.0F, 2.0F, 14),
            EMERALD = new ToolSetAttributes("EMERALD", new ItemStack(Items.diamond), 3, 1561, 8.0F, 3.0F, 10),
            GOLD = new ToolSetAttributes("GOLD", new ItemStack(Items.gold_ingot), 0, 32, 12.0F, 0.0F, 22);

    private final String string;
    private final int hl;
    private final int uses;
    private final float efficiencyOnProperMaterial;
    private final float damageVsEntity;
    private final int enchant;

    private final String item;

    public ToolSetAttributes(String name, ItemStack materialItem, int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
        string = name;
        hl = harvestLevel;
        uses = maxUses;
        efficiencyOnProperMaterial = efficiency;
        damageVsEntity = damage;
        enchant = enchantability;
        item = (String) Item.itemRegistry.getNameForObject(materialItem.getItem());
    }

    public int getMaxUses() {
        return uses;
    }

    public float getEfficiencyOnProperMaterial() {
        return efficiencyOnProperMaterial;
    }

    public float getDamageVsEntity(float offset) {
        return offset + damageVsEntity;
    }

    public int getHarvestLevel() {
        return hl;
    }

    public int getEnchantability() {
        return enchant;
    }

    public Item getItem() {
        return (Item) Item.itemRegistry.getObject(item);
    }

    public boolean getIsRepairable(ItemStack stack) {
        return getItem() == stack.getItem();
    }

    public Multimap getAttributeModifiers(Multimap map, UUID attr, double damage, String name) {
        map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(attr, name, damage, 0));
        return map;
    }

    public String toString() {
        return string;
    }
}
