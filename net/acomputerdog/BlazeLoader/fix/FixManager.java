package net.acomputerdog.BlazeLoader.fix;

import net.acomputerdog.BlazeLoader.fix.core.fixBlockAir.FixBlockAir;
import net.acomputerdog.BlazeLoader.main.BlazeLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains a list of registered fixes that can be loaded at various stages of startup.
 */
public class FixManager {
    private static Map<EFixType, List<Fix>> fixes;

    /**
     * Registers a fix.
     * @param fix A fix to be added to the list of fixes.
     */
    public static void addFix(Fix fix){
        if(fix != null){
            BlazeLoader.getLogger().logDetail("Adding fix: " + fix.getFixName());
            fixes.get(fix.getFixType()).add(fix);
        }
    }

    public static void onStart(){
        for(Fix fix : fixes.get(EFixType.STARTUP)){
            fix.apply();
        }
    }

    public static void onStop(){
        for(Fix fix : fixes.get(EFixType.SHUTDOWN)){
            fix.apply();
        }
    }

    static{
        fixes = new HashMap<EFixType, List<Fix>>();
        for(EFixType type : EFixType.values()){
            fixes.put(type, new ArrayList<Fix>());
        }
        addBasicFixes();
    }

    private static void addBasicFixes(){
        addFix(new FixBlockAir());
    }
}
