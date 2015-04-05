package com.blazeloader.api.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

public class ApiRenderBlock {
    
    /**
     * Registers a mapper for the given block that takes a given BlockState and gives back a prebaked model.
     *  
     * @param block		Block for rendering
     * @param mapper	IStateMapper to provide the models
     */
    public static void registerBlockModelMapper(Block block, IStateMapper mapper) {
    	Minecraft.getMinecraft().modelManager.getBlockModelShapes().registerBlockWithStateMapper(block, mapper);
    }
    
    /**
     * Registers the given blocks with the game to be rendered by some other managed code.
     * <p>
     * Currently used for air, water, lava, pistons, heads, banners, and signs.
     * 
     * @param blocks The blocks to register.
     */
    public static void registerBuiltInBlocks(Block... blocks) {
    	Minecraft.getMinecraft().modelManager.getBlockModelShapes().registerBuiltInBlocks(blocks);
    }
    
    /**
     * Remaps the models from an already registered block onto the given one.
     * 
     * @param original	Original block
     * @param block		Block to assign the models to
     */
    public static void swapoutBlockModels(Block original, Block block) {
    	BlockModelShapes mapper = Minecraft.getMinecraft().modelManager.getBlockModelShapes();
    	mapper.getBlockStateMapper().registerBlockStateMapper(block, (IStateMapper)mapper.getBlockStateMapper().blockStateMap.get(original));
    	mapper.reloadModels();
    }
}
