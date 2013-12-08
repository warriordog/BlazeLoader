package net.acomputerdog.BlazeLoader.util.time;

/**
 * Represents a date with the structure 7 days in a week, 28/29/30/31 days in a month, 12 months in a year.
 * Values are weekday, day, and month values are 0 - indexed.  BC years are negative, AD are positive, 0AD is 0.
 */
public interface IDate {

    /**
     * Gets the ID of the day of the week.
     *
     * @return Return a number from 0 - 6 representing the day of the week, starting from sunday.
     */
    public int getWeekdayID();

    /**
     * Gets the name of the day of the week.
     *
     * @return Return a String representing the name of the day of the week.
     */
    public String getWeekdayName();

    /**
     * Gets the day of the month.
     *
     * @return Return a number from 0 to 30 representing the day of the month.
     */
    public int getDay();

    /**
     * Gets the ID of the month.
     *
     * @return Return a number from 0 - 11 representing the ID of the month.
     */
    public int getMonthID();

    /**
     * Gets the name of the month.
     *
     * @return Return a String representing the name of the month.
     */
    public String getMonthName();

    /**
     * Gets the year.
     *
     * @return Return an int representing the year.  BC years are negative, AD are positive, 0AD is 0.
     */
    public int getYear();

    /**
     * Is the year a leap year?
     *
     * @return Return true if the year is a leap year, false if not.
     */
    public boolean isLeapYear();

    /**
     * Is the year in BC?
     *
     * @return Return true if the year is BC (negative), false if it is 0 or positive.
     */
    public boolean isBC();
}
