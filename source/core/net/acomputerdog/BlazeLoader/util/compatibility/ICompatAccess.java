package net.acomputerdog.BlazeLoader.util.compatibility;

import net.acomputerdog.BlazeLoader.mod.Mod;

/**
 * Interface to be implemented by mods wishing to use the compatibility API.
 */
public interface ICompatAccess {

    /**
     * Gets the ECompatType of this Compat Access.
     *
     * @return Return the ECompatType of this compatibility.
     */
    public ECompatType getCompatType();

    /**
     * Gets the mod that has registered this Compat Access.
     *
     * @return Return the instance of the mod that registered this compatibility.
     */
    public Mod getMod();
}
