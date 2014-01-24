package net.BlazeLoader.api.compat;

import net.acomputerdog.BlazeLoader.annotation.Beta;
import net.acomputerdog.BlazeLoader.mod.Mod;

@Beta(stable = true)
/**
 * Interface to be implemented by mods wishing to use the compatibility API.
 */
public interface ICompatAccess {

    /**
     * Gets the ECompatType of this Compat Access.
     *
     * @return Return the ECompatType of this compat.
     */
    public ECompatType getCompatType();

    /**
     * Gets the mod that has registered this Compat Access.
     *
     * @return Return the instance of the mod that registered this compat.
     */
    public Mod getMod();
}
