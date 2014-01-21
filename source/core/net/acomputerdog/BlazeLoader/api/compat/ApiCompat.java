package net.acomputerdog.BlazeLoader.api.compat;

import net.acomputerdog.BlazeLoader.annotation.Beta;

import java.util.List;

@Beta(stable = true)
/**
 * Provides compatibility between mods that perform similar functions.
 */
public class ApiCompat {

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
     * @param type Type of compat to get compatibilities for.
     * @return Return a List of all registered compatibilities for type 'type'
     */
    public List<ICompatAccess> getCompats(ECompatType type) {
        return CompatTable.getCompats(type);
    }
}
