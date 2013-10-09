package net.acomputerdog.BlazeLoader.time;

/**
 * Enum for getting the names of months of the year.  Ex: EMonths.values()[0].getName() == "January"
 */
public enum EMonths {

    JANUARY("January"),
    FEBRUARY("February"),
    MARCH("March"),
    APRIL("April"),
    MAY("May"),
    JUNE("June"),
    JULY("July"),
    AUGUST("August"),
    SEPTEMBER("September"),
    OCTOBER("October"),
    NOVEMBER("November"),
    DECEMBER("December");

    private String name;

    EMonths(String name){
        this.name = name;
    }

    /**
     * Gets the name of this month.
     * @return Return the name of this month.
     */
    public String getName(){
        return name;
    }
}
