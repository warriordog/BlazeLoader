package net.acomputerdog.BlazeLoader.time;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A standard date and time class utilizing System.currentTimeMillis() and Calendar.
 */
public class StandardClock implements IDate, ITimePrecise{

    protected Calendar theCalendar= new GregorianCalendar();

    protected void updateCalendar(){
        theCalendar.setTimeInMillis(System.currentTimeMillis());
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
        if(year % 400 == 0){
            return true;
        }else if(year % 100 != 0){
            return false;
        }else if(year % 4 == 0){
            return true;
        }else{
            return false;
        }

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
        return theCalendar.get(Calendar.HOUR);
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
