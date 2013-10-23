package net.acomputerdog.BlazeLoader.proxy;

import net.acomputerdog.BlazeLoader.api.render.IBlockRenderer;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;

/**
 * A proxy for RenderBlocks.
 */
public class RenderBlocksProxy extends RenderBlocks {
    public RenderBlocksProxy(IBlockAccess par1IBlockAccess)
    {
        super(par1IBlockAccess);
    }
    public RenderBlocksProxy(){
        super();
    }

    /**
     * Renders the block at the given coordinates using the block's rendering type
     */
    @Override
    public boolean renderBlockByRenderType(Block par1Block, int par2, int par3, int par4) {
        if(!(par1Block instanceof IBlockRenderer)){
            return super.renderBlockByRenderType(par1Block, par2, par3, par4);
        }else{
            return ((IBlockRenderer)par1Block).renderBlockInWorld(par1Block, par2, par3, par4);
        }
    }

    /**
     * Is called to renderBlockInWorld the image of a block on an inventory, as a held item, or as a an item on the ground
     */
    @Override
    public void renderBlockAsItem(Block par1Block, int par2, float par3) {
        if(!(par1Block instanceof IBlockRenderer)){
            super.renderBlockAsItem(par1Block, par2, par3);
        }else{
            ((IBlockRenderer)par1Block).renderBlockAsItem(par1Block, par2, par3);
        }
    }
}
