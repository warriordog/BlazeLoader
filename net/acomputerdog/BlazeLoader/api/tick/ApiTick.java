package net.acomputerdog.BlazeLoader.api.tick;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.Timer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Api for function related to the world tick.
 */
public class ApiTick {
    private static Field tpsField;
    private static boolean hasTps = false;
    private static float tps = 0.0F;

    /**
     * Gets the game's tick rate.  Uses reflection only on first run.
     * @return Returns the game's current tick rate.
     */
    public static float getTPS(){
        if(!hasTps){
            for(Field f : Timer.class.getDeclaredFields()){
                int mods = f.getModifiers();
                if(float.class.isAssignableFrom(f.getType()) && (!Modifier.isPrivate(mods) && !Modifier.isProtected(mods) && !Modifier.isPublic(mods))){
                    hasTps = true;
                    tpsField.setAccessible(true);
                    tpsField = f;
                    try {
                        tps = (Float) tpsField.get(BlazeLoader.gameTimer);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Could not get TPS!", e);
                    }
                }
            }
            if(tpsField == null){
                throw new RuntimeException("Could not get TPS!");
            }else{
                return tps;
            }
        }else{
            return tps;
        }
    }

    /**
     * Sets the game tick rate.
     * @param newTps The new tick rate.
     */
    public static void setTPS(float newTps){
        try {
            tpsField.set(BlazeLoader.gameTimer, tps);
            tps = newTps;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not set TPS!", e);
        }
    }
}
