package com.therise.nyc.therisenyc;

import java.util.Locale;

/**
 * Created by mayerzine on 3/11/17.
 */

// TimeTools contains methods for manipulating

public class TimeTools {

    // Take time in milliseconds and convert to "m:ss" format
    public static String millisToTimeString(long millis){
        // floor of quotient is the number of minutes
        int numMins = (int) Math.floor(((double)millis)/ WorkoutStatic.ONE_MINUTE);

        // remainder is seconds
        int numSecs = (int) Math.floor((millis - numMins * WorkoutStatic.ONE_MINUTE)/((double) WorkoutStatic.ONE_SECOND));

        String minString = String.valueOf(numMins);
        String secString = String.format(Locale.US,"%02d",numSecs);

        return minString + WorkoutStatic.COLON + secString;
    }

    // Convert split time (minutes, tens of seconds, seconds) to milliseconds
    public static long splitTimeToMillis(int[] times){
        long time = 0; // so far, no time
        time += times[RiseTimerStatic.MINS_INDEX] * WorkoutStatic.ONE_MINUTE; // converts minutes to milliseconds
        time += times[RiseTimerStatic.SECS_TENS_INDEX] * WorkoutStatic.TEN_SECONDS;
        time += times[RiseTimerStatic.SECS_ONES_INDEX] * WorkoutStatic.ONE_SECOND;
        return time;
    }

}
