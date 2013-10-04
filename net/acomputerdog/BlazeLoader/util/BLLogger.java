package net.acomputerdog.BlazeLoader.util;

import net.acomputerdog.BlazeLoader.mod.Mod;

/**
 * Customized logger for use with BlazeLoader and Mods.  Tagged with a name specified by the creator of the logger.
 * Sample output:
 * [Test Mod][INFO] This is BLLogger.logInfo().
 * [UNKNOWN][FATAL] The chat monitor thread has died unexpectedly!
 * [TPSMonitor][WARNING] The game tick rate has dropped below 50%!
 * [Awesome Logger of Awesomeness!] I'm Awesome!
 */
public class BLLogger {

    /**
     * Object that is the owner of this BLLogger.  Can be null.
     */
    protected Object owner;

    /**
     * Name used to tag this logger's output.
     */
    protected String name;

    /**
     * Creates a new BLLogger.
     * @param owner The Object that created this BLLogger.  Used to tag output.  Object.getClass().getSimpleName() will be used unless owner is one of:
     *              null -> "UNKNOWN"
     *              Mod -> Mod.getModName()
     *              String -> will use string
     */
    public BLLogger(Object owner){
        this.owner = owner;

        if(owner == null){
            name = "UNKNOWN";
        }else if(owner instanceof Mod){
            name = ((Mod)owner).getModName();
        }else if(owner instanceof String){
            name = (String)owner;
        }else{
            name = owner.getClass().getSimpleName();
        }

        if(name == null){
            name = "UNKNOWN";
        }
    }

    /**
     * Prints out a message in the format [{name}]{message}/n.
     * @param message The message to print.
     */
    protected void log(String message){
        System.out.println("[" + name + "]");
    }

    /**
     * Prints out a message in the format [{name}] {message}/n.
     * @param message The message to print.
     */
    public void logRaw(String message){
        log(" " + message);
    }

    /**
     * Prints out a message in the format [{name}][DEBUG] {message}/n.
     * @param message The message to print.
     */
    public void logDebug(String message){
        log("[DEBUG] " + message);
    }

    /**
     * Prints out a message in the format [{name}][DETAIL] {message}/n.
     * @param message The message to print.
     */
    public void logDetail(String message){
        log("[DETAIL] " + message);
    }

    /**
     * Prints out a message in the format [{name}][INFO] {message}/n.
     * @param message The message to print.
     */
    public void logInfo(String message){
        log("[INFO] " + message);
    }

    /**
     * Prints out a message in the format [{name}][WARNING] {message}/n.
     * @param message The message to print.
     */
    public void logWarning(String message){
        log("[WARNING] " + message);
    }

    /**
     * Prints out a message in the format [{name}][ERROR] {message}/n.
     * @param message The message to print.
     */
    public void logError(String message){
        log("[ERROR] " + message);
    }

    /**
     * Prints out a message in the format [{name}][FATAL] {message}/n.
     * @param message The message to print.
     */
    public void logFatal(String message){
        log("[FATAL] " + message);
    }

}
