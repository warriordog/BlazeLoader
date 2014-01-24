package net.acomputerdog.BlazeLoader.api.render;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

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
     * Returns the color this block should be rendered.
     */
    public int getRenderColor2(int metadata);
    
    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier2(IBlockAccess access, int x, int y, int z);
    
    /**
     * Retrieve the side overlay texture for this block
     */
    public IIcon getIconSideOverlay(IBlockAccess access, int metadata, int x, int y, int z, int side);
}
