package net.acomputerdog.BlazeLoader.util.time;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A standard date and time class utilizing System.currentTimeMillis() and Calendar.
 */
public class StandardClock implements IDate, ITimePrecise {

    /**
     * The calendar used to process epoch time.
     */
    protected Calendar theCalendar = new GregorianCalendar();

    /**
     * Updates the calendar's time.
     */
    protected void updateCalendar() {
        theCalendar.setTimeInMillis(System.currentTimeMillis());
    }

    /**
     * Gets the date as a String.  Formatted [month]-[day]-[year].  Ex: 10-9-13
     *
     * @return Return the date as a String.
     */
    public String getDateAsString() {
        updateCalendar();
        int month = theCalendar.get(Calendar.MONTH) + 1;
        int day = theCalendar.get(Calendar.DAY_OF_MONTH);
        int year = theCalendar.get(Calendar.YEAR);
        return month + "-" + day + "-" + year;
    }

    /**
     * Gets the time in 24 hour format excluding milliseconds.  Format [hours]:[minute]:[seconds]
     *
     * @return Return the time excluding milliseconds.
     */
    public String getSimpleTimeAsString() {
        updateCalendar();
        int hour = theCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = theCalendar.get(Calendar.MINUTE);
        int second = theCalendar.get(Calendar.SECOND);
        return hour + ":" + minute + ":" + second;
    }

    /**
     * Gets the time in 24 hour format including milliseconds.  Format [hours]:[minutes]:[seconds]:[milliseconds]
     *
     * @return Return the time including milliseconds
     */
    public String getComplexTimeAsString() {
        updateCalendar();
        int hour = theCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = theCalendar.get(Calendar.MINUTE);
        int second = theCalendar.get(Calendar.SECOND);
        int millis = theCalendar.get(Calendar.MILLISECOND);
        return hour + ":" + minute + ":" + second + ":" + millis;
    }

    /**
     * Gets the ID of the day of the week.
     *
     * @return Return a number from 0 - 6 representing the day of the week, starting from sunday.
     */
    @Override
    public int getWeekdayID() {
        updateCalendar();
        return theCalendar.get(GregorianCalendar.DAY_OF_WEEK);
    }

    /**
     * Gets the name of the day of the week.
     *
     * @return Return a String representing the name of the day of the week.
     */
    @Override
    public String getWeekdayName() {
        return EWeekdays.values()[getWeekdayID()].getName();
    }

    /**
     * Gets the day of the month.
     *
     * @return Return a number from 0 to 30 representing the day of the month.
     */
    @Override
    public int getDay() {
        updateCalendar();
        return theCalendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * Gets the ID of the month.
     *
     * @return Return a number from 0 - 11 representing the ID of the month.
     */
    @Override
    public int getMonthID() {
        updateCalendar();
        return theCalendar.get(GregorianCalendar.MONTH);
    }

    /**
     * Gets the name of the month.
     *
     * @return Return a String representing the name of the month.
     */
    @Override
    public String getMonthName() {
        return EMonths.values()[getMonthID()].getName();
    }

    /**
     * Gets the year.
     *
     * @return Return an int representing the year.  BC years are negative, AD are positive, 0AD is 0.
     */
    @Override
    public int getYear() {
        updateCalendar();
        return theCalendar.get(Calendar.YEAR);
    }

    /**
     * Is the year a leap year?
     *
     * @return Return true if the year is a leap year, false if not.
     */
    @Override
    public boolean isLeapYear() {
        int year = getYear();
        return year % 400 == 0 || year % 100 == 0 && year % 4 == 0;

    }

    /**
     * Is the year in BC?
     *
     * @return Return true if the year is BC (negative), false if it is 0 or positive.
     */
    @Override
    public boolean isBC() {
        updateCalendar();
        return theCalendar.get(Calendar.ERA) == GregorianCalendar.BC;
    }

    /**
     * Gets the milliseconds represented by this time.
     *
     * @return Return an int from 0 - 999 representing the milliseconds.
     */
    @Override
    public int getMillis() {
        updateCalendar();
        return theCalendar.get(Calendar.MILLISECOND);
    }

    /**
     * Gets the number of hours represented by this time.
     *
     * @return Return an int from 0 - 23 representing the hours.
     */
    @Override
    public int getHours() {
        updateCalendar();
        return theCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Gets the number of minutes represented by this time.
     *
     * @return Return an int from 0 - 59 representing the minutes.
     */
    @Override
    public int getMinutes() {
        updateCalendar();
        return theCalendar.get(Calendar.MINUTE);
    }

    /**
     * Gets the number of seconds represented by this time.
     *
     * @return Return and int from 0 - 59 representing the seconds.
     */
    @Override
    public int getSeconds() {
        updateCalendar();
        return theCalendar.get(Calendar.SECOND);
    }
}
