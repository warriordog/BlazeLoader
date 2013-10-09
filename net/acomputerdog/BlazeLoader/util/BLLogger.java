package net.acomputerdog.BlazeLoader.util;

import net.acomputerdog.BlazeLoader.mod.Mod;
import net.acomputerdog.BlazeLoader.time.StandardClock;

/**
 * Customized logger for use with BlazeLoader and Mods.  Tagged with a name specified by the creator of the logger.
 * Sample output:
 * [Test Mod][INFO] This is BLLogger.logInfo().
 * [UNKNOWN][FATAL] The chat monitor thread has died unexpectedly!
 * [TPSMonitor][WARNING] The game tick rate has dropped below 50%!
 * [Awesome Logger of Awesomeness!] I'm Awesome!
 */
public class BLLogger {
    protected static StandardClock theClock = new StandardClock();

    /**
     * Object that is the owner of this BLLogger.  Can be null.
     */
    protected Object owner;

    /**
     * Name used to tag this logger's output.
     */
    protected String name;

    /**
     * Include dates in debug messages?
     */
    protected boolean includeDate;

    /**
     * Include time in debug messages?
     */
    protected boolean includeTime;

    /**
     * Creates a new BLLogger.
     * @param owner The Object that created this BLLogger.  Used to tag output.  Object.getClass().getSimpleName() will be used unless owner is one of:
     *              null -> "UNKNOWN"
     *              Mod -> Mod.getModName()
     *              String -> will use string
     */
    public BLLogger(Object owner){
        this(owner, false, false);
    }

    /**
     * Crates a new BLLogger.
     * @param owner The Object that created this BLLogger.  Used to tag output.  Object.getClass().getSimpleName() will be used unless owner is one of:
     *              null -> "UNKNOWN"
     *              Mod -> Mod.getModName()
     *              String -> will use string
     * @param includeDate Set to true to include the date in log messages.
     * @param includeTime Set to true to include the time in log messages.
     */
    public BLLogger(Object owner, boolean includeDate, boolean includeTime){
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
        this.includeDate = includeDate;
        this.includeTime = includeTime;
    }

    /**
     * Prints out a message in the format [{name}]{message}/n.
     * @param message The message to print.
     */
    protected void log(String message){
        System.out.println(getDate() + getTime() + "[" + name + "]" + message);
    }

    /**
     * Gets the date formatted for display, if enabled.
     * @return Returns the date formatted for display, or "" if disabled.
     */
    protected String getDate(){
        if(includeDate){
            return "[" + theClock.getDateAsString() + "]";
        }else{
            return "";
        }
    }

    /**
     * Gets the time formatted for display, if enabled.
     * @return Returns the time formatted for display, or "" if disabled.
     */
    protected String getTime(){
        if(includeTime){
            return "[" + theClock.getSimpleTimeAsString() + "]";
        }else{
            return "";
        }
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
