package net.acomputerdog.BlazeLoader.api.block;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Block;

/**
 * Api for block-specific functions
 */
public class ApiBlock {

    @Beta(stable = true)
    /**
     * Gets an available block ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Block ID
     */
    public static int getFreeBlockId(){

        if(Block.blocksList[BlazeLoader.freeBlockId] == null){
            int id =  BlazeLoader.freeBlockId;
            BlazeLoader.freeBlockId++;
            return id;
        }
        else{
            int id =  BlazeLoader.updateFreeBlockId();
            BlazeLoader.freeBlockId++;
            return id;
        }
    }

    @Beta(stable = true)
    /**
     * Gets an available block ID, checking for used IDs that have been freed.
     * Throws a RuntimeException if none are available.
     * @return Returns a free Block ID.
     */
    public static int recheckBlockIds(){
        int id =  BlazeLoader.resetFreeBlockId();
        BlazeLoader.freeBlockId++;
        return id;
    }
}
