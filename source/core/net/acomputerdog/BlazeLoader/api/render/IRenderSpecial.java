package net.acomputerdog.BlazeLoader.api.render;

import net.minecraft.client.renderer.RenderBlocks;


public interface IRenderSpecial {
	/**
     * Renders the block at the given coordinates
     */
	public boolean renderWorldBlock(BLRenderBlocks blRenderBlocks, int x, int y, int z);
	
	/**
     * Renders the block as an item in the inventory
     */
	public void renderInventoryBlock(BLRenderBlocks renderer, int metadata);
	
	/**
	 * Returns true if the contents of renderInventoryBlock must be used in place of the standard procedure
	 */
	public boolean overrideInventoryRender();
}
