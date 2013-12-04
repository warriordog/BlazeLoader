package net.acomputerdog.BlazeLoader.api.item;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Item;

/**
 * Api functions for items.
 */
public class ApiItem {

    /**
     * Gets an available item ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Item ID
     */
    public static int getFreeItemId(){
        if(Item.itemsList[BlazeLoader.currFreeItemId] == null){
            int id = BlazeLoader.currFreeItemId;
            BlazeLoader.currFreeItemId++;
            return id;
        }else{
            int id = BlazeLoader.updateFreeItemId();
            BlazeLoader.currFreeItemId++;
            return id;
        }
    }

    /**
     * Gets an available item ID, checking for used IDs that have been freed.
     * Throws a RuntimeException if none are available.
     * @return Returns a free Item ID.
     */
    public static int recheckItemIds(){
        int id = BlazeLoader.resetFreeItemId();
        BlazeLoader.currFreeItemId++;
        return id;
    }
}
