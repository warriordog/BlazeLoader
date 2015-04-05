package com.blazeloader.api.item;

import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param block  The block itself
     */
    public static void registerItemBlock(Block block) {
    	registerItemBlock(block, new ItemBlock(block));
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param block  The item itself
     * @param item 	 An ItemBlock to register with the given block,
     */
    public static void registerItemBlock(Block block, ItemBlock item) {
    	registerItemBlock(Block.getIdFromBlock(block), (ResourceLocation)Block.blockRegistry.getNameForObject(block), block, item);
    }
    
    /**
     * Registers an ItemBlock in the game registry for the given block.
     *
     * @param id    The item ID
     * @param name  The name to register the item as
     * @param item  The item itself
     */
    public static void registerItemBlock(int id, ResourceLocation name, Block block, Item item) {
    	registerItem(id, name, item);
    	Item.BLOCK_TO_ITEM.put(block, item);
    }
    
    /**
     * Gets the item by it's associated block.
     * 
     * @param block	The block
     * 
     * @return An item for the block
     */
    public static Item getItemByBlock(Block block) {
    	return Item.getItemFromBlock(block);
    }
    
    /**
     * Gets an item from by given id
     * 
     * @param id The id
     * 
     * @return and item associated with that id or null
     */
    public static Item getItemById(int id) {
    	return Item.getItemById(id);
    }
    
    /**
     * Gets the id associated with the given Item.
     * 
     * @param item The item
     * 
     * @return the item's id
     */
    public static int getItemId(Item item) {
    	return Item.getIdFromItem(item);
    }
    
    /**
     * Gets the name of an item.
     *
     * @param item The Item to get the name for
     * @return Return a string of the name belonging to param item
     */
    public static ResourceLocation getItemName(Item item) {
        return (ResourceLocation)Item.itemRegistry.getNameForObject(item);
    }
    
    /**
     * Gets the name of an item.
     *
     * @param item The Item to get the name for
     * @return Return a string of the name belonging to param item
     */
    public static String getStringItemName(Item item) {
        return getItemName(item).toString();
    }
}
