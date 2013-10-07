package net.acomputerdog.BlazeLoader.fix;

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
}
