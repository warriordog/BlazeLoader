package com.blazeloader.api.block;

import com.blazeloader.api.item.ApiItem;
import com.blazeloader.util.version.Versions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.util.ModUtilities;

import net.acomputerdog.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * 
     * Registers the given block as 'flammable' by fire.
     * 
     * @param block				The block to register
     * @param encouragement		How likely it is that this block will spread fire
     * @param flamability		How flamable this block is
     * 
     * @return the block for simplicity
     */
    public static <T extends Block> T registerFireInfo(T block, int encouragement, int flamability) {
    	Blocks.fire.setFireInfo(block, encouragement, flamability);
    	return block;
    }
    
    /**
     * Utility method to register a block.
     * Will register the block together with an item and setup unlocalized names for both.
     *
     * @param id        The ID of the block.
     * @param mod        The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name    The name to register the block as. Will be set as the name for both the block and the item
     * @param block    The block to add
     *
     * @return the block for simplicity
     */
    public static <T extends Block> T quickRegisterBlock(int id, String mod, String name, T block) {
        return registerBlock(id, new ResourceLocation(mod, name), (T) block.setUnlocalizedName(name), (new ItemBlock(block)).setUnlocalizedName(name));
    }

    /**
     * Registers and initialises a block in the block registry together with an item for it.
     *
     * @param id        The ID of the block.
     * @param mod       The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name      The name to register the block as
     * @param block     The block to add
     * @param itemBlock The item to be used for this block
     * @return the block for simplicity
     */
    public static <T extends Block> T registerBlock(int id, String mod, String name, T block, ItemBlock itemBlock) {
        return registerBlock(id, new ResourceLocation(mod, name), block, itemBlock);
    }

    /**
     * Registers and initialises a block in the block registry together with an item for it.
     *
     * @param id        The ID of the block.
     * @param name      The name to register the block as
     * @param block     The block to add
     * @param itemBlock The item to be used for this block
     * @return the block for simplicity
     */
    public static <T extends Block> T registerBlock(int id, ResourceLocation name, T block, ItemBlock itemBlock) {
        registerBlock(id, name, block);
        ApiItem.registerItemBlock(block, itemBlock);
        return block;
    }

    /**
     * Registers and initialises a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param mod	The domain used for this mod. eg. "minecraft:stone" has the domain "minecraft"
     * @param name  The name to register the block as
     * @param block The block to add
     *
     * @return the block for simplicity
     */
    public static <T extends Block> T registerBlock(int id, String mod, String name, T block) {
        return registerBlock(id, new ResourceLocation(mod, name), block);
    }
    
    /**
     * Registers and initialises a block in the block registry.
     *
     * @param id    The ID of the block.
     * @param name  The name to register the block as
     * @param block The block to add
     *
     * @return the block for simplicity
     */
    public static <T extends Block> T registerBlock(int id, ResourceLocation name, T block) {
        injectBlock(id, name, block);
        for (IBlockState state : (ImmutableList<IBlockState>)block.getBlockState().getValidStates()) {
            int metadata = Block.blockRegistry.getIDForObject(block) << 4 | block.getMetaFromState(state);
            Block.BLOCK_STATE_IDS.put(state, metadata);
        }
        return block;
    }
    
    /**
     * Registers names for all the variants the given block has.
     * 
     * @param block		The block
     * @param variants	Names for all the item's variants
     *
     * @return the block for simplicity
     */
    public static <T extends Block> T registerBlockVarientNames(T block, String... variants) {
        ApiItem.registerItemVariantNames(ApiItem.getItemByBlock(block), variants);
        return block;
    }
    
    /**
     * Replaces an existing block with the given block and gives it an
     *  ItemMultiTexture with the given function for determining what name to display.
     * <p>
     * Works best if the replacement block supports all the states of the one it is replacing.
     * @param original	Original block to replace
     * @param block		New block to insert
     * @param nameFunc	Function used by the itemblock to get the display name
     * @param <K> The type of the block you are replacing
     * @param <V> The type of the new block you are replacing it with. Must extend the original.
     */
    public static <K extends Block,V extends K> void replaceBlock(K original, V block, Function nameFunc) {
    	replaceBlock(original, block, new ItemMultiTexture(block, block, nameFunc));
    }
    
    /**
     * Replaces an existing block with the given block and gives it the provided itemblock.
     * <p>
     * Works best if the replacement block supports all the states of the one it is replacing.
     * @param original	Original block to replace
     * @param block		New block to insert
     * @param item		An item to be used for the replacement block
     * @param <K> The type of the block you are replacing
     * @param <V> The type of the new block you are replacing it with. Must extend the original.
     */
    public static <K extends Block,V extends K> void replaceBlock(K original, V block, ItemBlock item) {
    	replaceBlock(original, block);
    	ApiItem.registerItemBlock(block, item);
    }
    
    /**
     * Replaces an existing block with the given block.
     * <p>
     * Works best if the replacement block supports all the states of the one it is replacing.
     * @param original	Original block to replace
     * @param block		New block to insert
     * @param <K> The type of the block you are replacing
     * @param <V> The type of the new block you are replacing it with. Must extend the original.
     */
    public static <K extends Block,V extends K> void replaceBlock(K original, V block) {
    	Map<Integer, IBlockState> builtStates = new HashMap<Integer, IBlockState>();
    	for (IBlockState original_state : (List<IBlockState>)original.getBlockState().getValidStates()) {
    		int original_metadata = getBlockId(original) << 4 | original.getMetaFromState(original_state);
    		builtStates.put(Integer.valueOf(original_metadata), original_state);
    	}
    	for (IBlockState state : (List<IBlockState>)block.getBlockState().getValidStates()) {
    		int metadata = getBlockId(original) << 4 | block.getMetaFromState(state);
    		builtStates.put(Integer.valueOf(metadata), state);
    	}
    	for (Map.Entry<Integer, IBlockState> i : builtStates.entrySet()) {
    		if (i.getValue().getBlock() == original) {
    			Block.BLOCK_STATE_IDS.put(block.getStateFromMeta(i.getKey()), i.getKey());
    		}
    		Block.BLOCK_STATE_IDS.put(i.getValue(), i.getKey());
    	}
    	injectBlock(getBlockId(original), getBlockName(original), block);
    	if (Versions.isClient()) {
    		com.blazeloader.api.client.render.ApiRenderBlock.swapoutBlockModels(original, block);
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
}
