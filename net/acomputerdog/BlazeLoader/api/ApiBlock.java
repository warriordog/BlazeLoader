package net.acomputerdog.BlazeLoader.api;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Block;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    /**
     * Gets an available block ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Block ID
     */
    public static int getFreeBlockId(){
        if(Block.blocksList[BlazeLoader.freeBlockId] == null)return BlazeLoader.freeBlockId;
        else return BlazeLoader.updateFreeBlockId();
    }

    /**
     * Gets an available block ID, checking for used IDs that have been freed.
     * Throws a RuntimeException if none are available.
     * @return Returns a free Block ID.
     */
    public static int recheckBlockIds(){
        return BlazeLoader.resetFreeBlockId();
    }
}
