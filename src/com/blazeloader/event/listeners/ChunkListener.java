package com.blazeloader.event.listeners;

import com.blazeloader.bl.mod.BLMod;
import net.minecraft.world.chunk.Chunk;

/**
 * Chunk based events.
 */
public interface ChunkListener extends BLMod {

    /**
     * Called when a chunk is created or loaded
     *
     * @param chunk the chunk
     */
    public void onChunkLoad(Chunk chunk);

    /**
     * Caleld when a chunk is unloaded
     *
     * @param chunk the chunk
     */
    public void onChunkUnload(Chunk chunk);
}
