package com.blazeloader.api.item;

import com.mumfrey.liteloader.util.ModUtilities;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Api functions for items.
 */
public class ApiItem {

    /**
     * Registers an item in the game registry.
     *
     * @param id    The item ID
     * @param name  The item name
     * @param item  The item itself
     */
    public static void registerItem(int id, String mod, String name, Item item) {
    	registerItem(id, new ResourceLocation(mod, name), item);
    }

    /**
     * Registers an item in the game registry.
     *
     * @param id    The item ID
     * @param name  The name to register the item as
     * @param item  The item itself
     */
    public static void registerItem(int id, ResourceLocation name, Item item) {
    	ModUtilities.addItem(id, name, item, true);
        //Item.itemRegistry.register(id, name, item);
    }
}
