package com.example.scheduledcarillon;

import static org.joda.time.DateTimeConstants.*;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.text.ParseException;


public class Seasons {
    public enum Season {
        CHRISTMAS ("Christmas"),
        EASTER ("Easter"),
        GENERAL ("Regular");

        private final String name;
        Season(String name){
            this.name = name;
        }
        public String getName(){ return name; }
    }

    /*
     * calculating Easter is difficult:
     * https://www.rmg.co.uk/stories/topics/when-easter#:%7E:text=The%20simple%20standard%20definition%20of,Easter%20is%20the%20next%20Sunday
     */
    private LocalDate getEasterThisYear(LocalDate now) {
        int year = now.getYear();

        //  Calculate D=225 - 11(Y MOD 19).
        int d = 225 - 11 * (year % 19);

        // If D is greater than 50 then subtract multiples of 30 until the resulting new value of D is less than 51.
        while(d > 50)
            d -= 30;

        // If D is greater than 48 subtract 1 from it.
        if(d > 48)
            d--;

        //  E=(Y + [Y/4] + D + 1) MOD 7. (NB Integer part of [Y/4])
        int e = (year + year / 4 + d + 1) % 7;

        //  Q=D + 7 - E.
        int q = d + 7 - e;

        // If Q is less than 32 then Easter is in March. If Q is greater than 31 then Q - 31 is its date in April.
        if (q < 32)
            return new LocalDate(year, 3, q);
        else
            return new LocalDate(year, 4, q - 31);
    }

    public Season getCurrentSeason() {
      //  Calendar now = new GregorianCalendar();
        LocalDate now = LocalDate.now();

        // Christmas: Month of December
        if(now.getMonthOfYear() == DECEMBER)
            return Season.CHRISTMAS;

        // Easter: complicated
        // easter and the week following
        LocalDate easter = getEasterThisYear(now);
        if(now.isEqual(easter) || (now.isAfter(easter) && now.isBefore(easter.plusDays(8))))
            return Season.EASTER;

        // No match
        return Season.GENERAL;
    }

    public static Season checkFilename(String filename){
        if(filename.contains("christmas/"))
            return Season.CHRISTMAS;
        if(filename.contains("easter/"))
            return Season.EASTER;
        return Season.GENERAL;
    }
}
