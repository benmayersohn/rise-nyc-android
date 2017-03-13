package com.therise.nyc.therisenyc;

import java.util.Locale;

/**
 * Created by mayerzine on 3/11/17.
 */

// TimeTools contains methods for manipulating

public class TimeTools {

    private static final int ONE_SECOND = 1000; // one second in milliseconds
    private static final int ONE_MINUTE = 60*ONE_SECOND; // one minute in milliseconds

    // Take time in milliseconds and convert to "m:ss" format
    public static String millisToTimeString(long millis){
        // floor of quotient is the number of minutes
        int numMins = (int) Math.floor(1.0*millis/(1.0*ONE_MINUTE));

        // remainder is seconds
        int numSecs = (int) Math.floor(1.0*(millis - numMins * ONE_MINUTE)/(1.0*ONE_SECOND));

        String minString = String.valueOf(numMins);
        String secString = String.format(Locale.US,"%02d",numSecs);

        return minString + ":" + secString;
    }
}
