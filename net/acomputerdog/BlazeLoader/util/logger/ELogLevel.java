package net.acomputerdog.BlazeLoader.util.logger;

/**
 * Logging levels such as DEBUG, INFO, ERROR, etc.
 */
public enum ELogLevel {
    DEBUG("debug", 0, true, false),
    DETAIL("detail", 1, true, false),
    INFO("info", 2, false, false),
    WARNING("warning", 3, false, false),
    ERROR("error", 4, false, true),
    FATAL("fatal", 5, false, true);

    private String levelName;
    private int priority;
    private boolean isDebug;
    private boolean isError;

    private ELogLevel(String levelName, int priority, boolean isDebug, boolean isError) {
        this.levelName = levelName;
        this.priority = priority;
        this.isDebug = isDebug;
        this.isError = isError;
    }

    /**
     * Gets a String representing the name of this logging level.
     * @return Return a String representing the name of this logging level.
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Gets an int representing the priority level of this logging level.
     * @return Return an int representing the priority level of this logging level.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Returns true if this Logging level is a debug level (DEBUG or DETAIL).
     * @return Return true if this Logging level is a debug level
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * Returns true if this logging level is an error level (ERROR or FATAL)
     * @return Return true if this logging level is an error level
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns true if this ELogLevel is allowed with the specified priority.
     * @param priority The priority to compare to.
     * @return Returns true if this ELogLevel is allowed with the specified priority.
     */
    public boolean isAllowed(int priority){
        return this.priority >= priority;
    }

    /**
     * Returns true if this ELogLevel is allowed with the specified priority.
     * @param priority The priority to compare to.
     * @return Returns true if this ELogLevel is allowed with the specified priority.
     */
    public boolean isAllowed(ELogLevel priority){
        return this.priority >= priority.getPriority();
    }

    /**
     * Gets the ELogLevel identified by the given name.
     * @param name The name of the ELogLevel to get.
     * @return Return the ELogLevel identified by the given name, or null if none exists.
     */
    public static ELogLevel getByName(String name){
        if(name == null){
            throw new IllegalArgumentException("name cannot be null!");
        }else{
            for(ELogLevel level : values()){
                if(name.equals(level.getLevelName())){
                    return level;
                }
            }
            return null;
        }
    }
}
