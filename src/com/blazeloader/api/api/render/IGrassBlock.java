package com.blazeloader.api.api.render;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Used allow blocks to render as a grass block
 * or with other kinds of overlays/tints
 *
 * @author Sollace
 */
public interface IGrassBlock {
    /**
     * Indicates that this block must be rendered as with snow on top.
     */
    public boolean HasSnow(IBlockAccess access, int x, int y, int z);

    /**
     * Determines whether this Block must be rendered with side overlays.
     */
    public boolean IsGrassBlock(int metadata);

    /**
     * Determines whether this Block must be rendered with side overlays when in the inventory.
     */
    public boolean IsGrassBlockInv(int metadata);

    /**
     * Returns the color overlay that should be used for this block when rendered
     * in the inventory
     */
    public int getInventoryRenderColor(int metadata);

    /**
     * Returns the color overlay used when rendering this block in the world.
     */
    public int getWorldRenderColor(IBlockAccess access, int x, int y, int z);

    /**
     * Retrieve the side overlay texture for this block
     */
    public IIcon getIconSideOverlay(IBlockAccess access, int metadata, int x, int y, int z, int side);
}
