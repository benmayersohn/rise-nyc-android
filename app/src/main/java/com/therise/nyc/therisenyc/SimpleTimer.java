package com.therise.nyc.therisenyc;

/**
 * SimpleTimer.java: counts number of seconds into the workout we've gone. That's all.
 */

class SimpleTimer{

    private long timeElapsed; // in milliseconds
    long runningCheckInterval;

    // Distance from current time to second mark (will be between 0 [inclusive] and 1000 [exclusive] milliseconds)
    private long getDistanceFromSecondMark(){
        long numSecsElapsed = (long)Math.floor(timeElapsed / WorkoutStatic.ONE_SECOND);
        return timeElapsed - numSecsElapsed * WorkoutStatic.ONE_SECOND;
    }

    // Are we on the second mark?
    //
    boolean isOnSecondMark(){
        return getDistanceFromSecondMark() == 0;
    }

    SimpleTimer(long checkInterval){
        timeElapsed = 0;
        runningCheckInterval = checkInterval;
    }

    // steps forward
    public void step(){
        timeElapsed += runningCheckInterval;
    }

    long getTimeElapsed(){
        return timeElapsed;
    }

    long getRunningCheckInterval(){
        return runningCheckInterval;
    }

}
