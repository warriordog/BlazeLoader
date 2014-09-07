package com.blazeloader.api.direct.server.api.item;


import net.minecraft.item.Item;

/**
 * Api functions for items.
 */
public class ApiItemServer {

    /**
     * Registers an item in the game registry.
     *
     * @param id   The item ID
     * @param name The item name
     * @param item The item itself
     */
    public static void registerItem(int id, String name, Item item) {
        Item.itemRegistry.addObject(id, name, item);
    }
}
