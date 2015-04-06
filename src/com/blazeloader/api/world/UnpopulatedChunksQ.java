package com.blazeloader.api.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.chunk.Chunk;

public class UnpopulatedChunksQ {
	public static final UnpopulatedChunksQ instance = new UnpopulatedChunksQ();
	
	private UnpopulatedChunksQ() {}
	
	public static UnpopulatedChunksQ instance() {
		return instance;
	}
	
	/**
	 * This should not get very big since chunks get populated (and thus popped) directly after their onload event.
	 */
	private static List<Chunk> unpopulatedChunksQ = new ArrayList<Chunk>();
	
    public boolean pop(Chunk chunk) {
    	if (unpopulatedChunksQ.contains(chunk)) {
    		unpopulatedChunksQ.remove(chunk);
    		return true;
    	}
    	return false;
    }
    
    public void push(Chunk chunk) {
    	if (!unpopulatedChunksQ.contains(chunk)) {
    		unpopulatedChunksQ.add(chunk);
    	}
    }
    
    public void flush() {
    	unpopulatedChunksQ.clear();
    }
}
