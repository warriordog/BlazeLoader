package net.acomputerdog.BlazeLoader.api.compat;

import net.acomputerdog.BlazeLoader.annotation.Beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Beta(stable = true)
/**
 * Compatibility mapping table
 */
public class CompatTable {
    private static Map<ECompatType, List<ICompatAccess>> table;

    static {
        table = new HashMap<ECompatType, List<ICompatAccess>>();
        for (ECompatType type : ECompatType.values()) {
            table.put(type, new ArrayList<ICompatAccess>());
        }
    }

    public static List<ICompatAccess> getCompats(ECompatType type) {
        return table.get(type);
    }

    public static void addCompat(ICompatAccess compat) {
        table.get(compat.getCompatType()).add(compat);
    }

}
