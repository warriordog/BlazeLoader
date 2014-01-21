package net.acomputerdog.BlazeLoader.util.time;

/**
 * Represents a time with 24 total hours each composed of 60 minutes, each composed of 60 seconds.
 */
public interface ITime {

    /**
     * Gets the number of hours represented by this time.
     *
     * @return Return an int from 0 - 23 representing the hours.
     */
    public int getHours();

    /**
     * Gets the number of minutes represented by this time.
     *
     * @return Return an int from 0 - 59 representing the minutes.
     */
    public int getMinutes();

    /**
     * Gets the number of seconds represented by this time.
     *
     * @return Return and int from 0 - 59 representing the seconds.
     */
    public int getSeconds();
}
