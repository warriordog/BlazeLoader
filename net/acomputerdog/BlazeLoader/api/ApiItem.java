package net.acomputerdog.BlazeLoader.api;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Item;

public class ApiItem {

    /**
     * Gets an available item ID.  Throws a RuntimeException if none are available.
     * @return Returns a free Item ID
     */
    public static int getFreeItemId(){
        if(Item.itemsList[BlazeLoader.freeItemId] == null)return BlazeLoader.freeItemId;
        else return BlazeLoader.updateFreeItemId();
    }

    /**
     * Gets an available item ID, checking for used IDs that have been freed.
     * Throws a RuntimeException if none are available.
     * @return Returns an free Item ID.
     */
    public static int recheckItemIds(){
        return BlazeLoader.resetFreeItemId();
    }
}
