package net.acomputerdog.BlazeLoader.api.item;


import net.minecraft.item.Item;

/**
 * Api functions for items.
 */
public class ApiItem {

    /**
     * Registers an item in the game registry.
     * @param id The item ID
     * @param name The item name
     * @param item The item itself
     */
    public static void registerItem(int id, String name, Item item) {
        Item.field_150901_e.func_148756_a(id, name, item);
    }
}
