package net.acomputerdog.BlazeLoader.fix;

import net.acomputerdog.BlazeLoader.annotation.Beta;

@Beta(stable = true)
/**
 * A one-time-run class that is loaded at various times of startup to fix issues with Mojang code.
 */
public abstract class Fix {

    /**
     * Gets the stage of game startup/run to apply at.  Defaults to EFixType.INIT.
     * @return Return an EFixType representing the type of fix to apply.
     */
    public EFixType getFixType(){
        return EFixType.INIT;
    }

    /**
     * Applies the fix.
     */
    public abstract void apply();

    /**
     * Gets the name of the fix to be displayed in debug messages.
     * @return Returns the name of the fix.
     */
    public String getFixName(){
        return this.getClass().getSimpleName();
    }
}
