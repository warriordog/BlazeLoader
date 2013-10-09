package net.acomputerdog.BlazeLoader.time;

/**
 * Same as ITime, but includes millisecond precision.
 */
public interface ITimePrecise extends ITime{

    /**
     * Gets the milliseconds represented by this time.
     * @return Return an int from 0 - 999 representing the milliseconds.
     */
    public int getMillis();
}
