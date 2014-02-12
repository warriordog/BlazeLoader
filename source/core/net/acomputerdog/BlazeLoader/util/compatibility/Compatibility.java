package net.acomputerdog.BlazeLoader.util.compatibility;

import java.util.List;

/**
 * Provides compatibility between mods that perform similar functions.
 */
public class Compatibility {

    /**
     * Registers a Compatibility Access.
     *
     * @param access The ICompatAccess to register.
     */
    public void registerCompat(ICompatAccess access) {
        CompatTable.addCompat(access);
    }

    /**
     * Gets all registered compatibilities for a given compatibility type.
     *
     * @param type Type of compatibility to get compatibilities for.
     * @return Return a List of all registered compatibilities for type 'type'
     */
    public List<ICompatAccess> getCompats(ECompatType type) {
        return CompatTable.getCompats(type);
    }
}
