package net.acomputerdog.BlazeLoader.api.render;

import net.minecraft.src.Block;

/**
 * An interface representing a block that can be rendered.
 */
public interface IBlockRenderer {
    public boolean renderBlockInWorld(Block block, int x, int y, int z);
    public boolean renderBlockAsItem(Block block, int i, float f);
}
