package net.acomputerdog.BlazeLoader.api.compat;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that contains a map of Strings to Objects.  Mods can get and set the value of the items "on the wall".
 */
public class Wall {
    private static Map<String, Object> wallMap = new HashMap<String, Object>();

    /**
     * Gets an item from the wall.
     * @param name The name of the item to get.
     * @return Return the item with the given name, or null if none exists.
     */
    public static Object getItem(String name){
        return wallMap.get(name);
    }

    /**
     * Sets an item on the wall.
     * @param name The name of the item to get.
     * @param value The value to set the item to.
     */
    public static void setItem(String name, Object value){
        wallMap.put(name, value);
    }

    /**
     * Gets the type of an item on the wall.  The type is the name of the item's class.
     * @param name The name of the item to get.
     * @return Return the type of the item, or null if the item does not exist.
     */
    public static String getItemType(String name){
        Object item = wallMap.get(name);
        if(item != null){
            return item.getClass().getName();
        }else{
            return null;
        }
    }

    /**
     * Checks if the given item exists.
     * @param name The name of the item to check for.
     * @return Return true if the item exists, false if not.
     */
    public static boolean isDefined(String name){
        return wallMap.containsKey(name);
    }
}
