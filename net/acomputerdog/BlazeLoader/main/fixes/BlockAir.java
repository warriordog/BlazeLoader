package net.acomputerdog.BlazeLoader.main.fixes;

import java.util.Random;

import net.minecraft.src.*;

/**
 * Represents an air block. Can be extended as a base for custom air blocks
 */
public class BlockAir extends Block {
    private static boolean hasInjected = false;

    private BlockAir() {
        super(0, Material.air);
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        return false;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
    
    /**
     * Return whether this block can drop from an explosion.
     */
    @Override
    public boolean canDropFromExplosion(Explosion par1Explosion) {
    	return false;
    }
    
    /**
     * Returns whether this block is collideable based on the arguments passed in \n@param par1 block metaData \n@param
     * par2 whether the player right-clicked while holding a boat
     */
    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
    	return false;
    }
    
    /**
     * Returns the block hardness at a location. Args: world, x, y, z
     */
    @Override
    public float getBlockHardness(World par1World, int par2, int par3, int par4) {
    	return 0.0f;
    }
    
    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
    	return -1;
    }
    
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random par1Random) {
    	return 0;
    }
    
    public static void injectBlockAir(){
        if(!hasInjected){
            hasInjected = true;
            new BlockAir();
        }
    }
}
