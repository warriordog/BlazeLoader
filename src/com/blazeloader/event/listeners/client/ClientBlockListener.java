package com.blazeloader.event.listeners.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.blazeloader.bl.mod.BLMod;

/**
 * Interface for mods that handle block events
 */
public interface ClientBlockListener extends BLMod {
    /**
     * Called when a block is placed or destroyed in the world.
     *
     * @param world 		The world being changed.
     * @param position		A location for where the change has occurred
     * @param oldState		The previous block state for that location
     * @param newState		What the block state for that location has become
     */
    public void onBlockChanged(World world, BlockPos position, IBlockState oldState, IBlockState newState);
}
