package com.blazeloader.api.block;

import java.util.Iterator;

import net.acomputerdog.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.blazeloader.util.version.Versions;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.util.ModUtilities;

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
     * Gets the id associated with the given block.
     * 
     * @param block The block
     * 
     * @return the block's id
     */
    public static int getBlockId(Block block) {
    	return Block.getIdFromBlock(block);
    }
    
    /**
     * Registers and initialises a block in the block registry.
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
     * Registers and initialises a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void registerBlock(int id, ResourceLocation name, Block block) {
    	injectBlock(id, name, block);
        for (IBlockState state : (ImmutableList<IBlockState>)block.getBlockState().getValidStates()) {
            int metadata = Block.blockRegistry.getIDForObject(block) << 4 | block.getMetaFromState(state);
            Block.BLOCK_STATE_IDS.put(state, metadata);
        }
    }
    
    /**
     * Replaces an existing block with the given block.
     * <p>
     * Works best if the replacement block supports all the states of the one it is replacing.
     * 
     * @param original	Original block to replace
     * @param block		New block to insert
     */
    public static void replaceBlock(Block original, Block block) {
    	Iterator original_states = original.getBlockState().getValidStates().iterator();
    	Iterator new_states = block.getBlockState().getValidStates().iterator();
    	while (original_states.hasNext() && new_states.hasNext()) {
    		IBlockState original_state = (IBlockState)original_states.next();
    		IBlockState state = (IBlockState)new_states.next();
    		int original_metadata = getBlockId(original) << 4 | original.getMetaFromState(original_state);
            int metadata = getBlockId(original) << 4 | block.getMetaFromState(state);
            Block.BLOCK_STATE_IDS.put(state, metadata);
        }
    	while (new_states.hasNext()) {
    		IBlockState state = (IBlockState)new_states.next();
    		int metadata = getBlockId(original) << 4 | block.getMetaFromState(state);
            Block.BLOCK_STATE_IDS.put(state, metadata);
    	}
    	injectBlock(getBlockId(original), getBlockName(original), block);
    	if (Versions.isClient()) {
    		com.blazeloader.api.client.render.ApiRenderClient.swapoutBlockModels(original, block);
    	}
    }
    
    /**
     * Registers a block in the block registry.
     * <p>
     * Is like registerBlock but does not perform any blockstate initialisation.
     *
     * @param id    The ID of the block.
     * @param mod	The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void injectBlock(int id, String mod, String name, Block block) {
    	injectBlock(id, new ResourceLocation(mod, name), block);
    }
    
    /**
     * Registers a block in the block registry.
     * <p>
     * Is like registerBlock but does not perform any blockstate initialisation.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     */
    public static void injectBlock(int id, ResourceLocation name, Block block) {
    	ModUtilities.addBlock(id, name, block, true);
    	//Switched to using Mumfry's implementation as it supports setting the static field as well as forcing past Forge.
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
    public static ResourceLocation getBlockName(Block block) {
        return (ResourceLocation)Block.blockRegistry.getNameForObject(block);
    }
    
    /**
     * Gets the name of a block.
     *
     * @param block The block to get the name for
     * @return Return a string of the name belonging to param block
     */
    public static String getStringBlockName(Block block) {
        return getBlockName(block).toString();
    }
}
