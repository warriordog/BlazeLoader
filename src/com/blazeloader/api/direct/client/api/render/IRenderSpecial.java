package com.blazeloader.api.direct.client.api.render;

/**
 * Used to allow a block to have better control over its own rendering
 *
 * @author Sollace
 */
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
