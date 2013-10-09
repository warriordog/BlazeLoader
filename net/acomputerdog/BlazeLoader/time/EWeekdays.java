package net.acomputerdog.BlazeLoader.time;

/**
 * Enum for getting the names of days of the week.  Ex: EWeekdays.values()[0].getName() == "Sunday"
 */
public enum EWeekdays {

    SUNDAY("Sunday"),
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday");

    private String name;

    EWeekdays(String name){
        this.name = name;
    }

    /**
     * Gets the name of this day.
     * @return Returns the name of this day.
     */
    public String getName(){
        return name;
    }
}
