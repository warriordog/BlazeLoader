package com.blazeloader.api.block;

import com.mumfrey.liteloader.util.ModUtilities;
import net.acomputerdog.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Api for block-specific functions
 */
public class ApiBlock {
    /**
     * Gets a block by it's name or ID
     *
     * @param identifier A string representing the name or ID of the block.
     * @return The block defined by parameter identifier
     */
    public static Block getBlockByNameOrId(String identifier) {
        return MathUtils.isInteger(identifier) ? getBlockById(Integer.parseInt(identifier)) : getBlockByName(identifier);
    }

    /**
     * Gets a block by it's name
     *
     * @param name The name of the block
     * @return Gets the block defined by param name.
     */
    public static Block getBlockByName(String name) {
        return Block.getBlockFromName(name);
    }

    /**
     * Gets a block by it's BlockId.
     *
     * @param id The ID of the block.
     * @return Return the block defined by param id.
     */
    public static Block getBlockById(int id) {
        return Block.getBlockById(id);
    }

    /**
     * Gets a block by it's item version.
     *
     * @param item The item to get the block from.
     * @return Return the block associated with param item.
     */
    public static Block getBlockByItem(Item item) {
        return Block.getBlockFromItem(item);
    }

    /**
     * Registers a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param mod	The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, String mod, String name, Block block) {
    	registerBlock(id, new ResourceLocation(mod, name), block);
    }
    
    /**
     * Registers a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, ResourceLocation name, Block block) {
    	ModUtilities.addBlock(id, name, block, true);
    }

    /**
     * Registers or replaces a TileEntity
     *
     * @param clazz Tile entity class
     * @param name  Entity name. Used as its id.
     */
    public static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        TileEntity.classToNameMap.put(clazz, name);
        TileEntity.nameToClassMap.put(name, clazz);
    }

    /**
     * Gets the name of a block.
     *
     * @param block The block to get the name for
     * @return Return a string of the name belonging to param block
     */
    public static String getBlockName(Block block) {
        return (String)Block.blockRegistry.getNameForObject(block);
    }

}
