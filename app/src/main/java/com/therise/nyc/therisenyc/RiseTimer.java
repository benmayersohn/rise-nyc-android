package com.therise.nyc.therisenyc;

/**
 * The Rise Timer!
 */

class RiseTimer extends SimpleTimer{

    private long workoutLength;

    long getWorkoutLength(){
        return workoutLength;
    }

    // times will be in a vector
    private long times[];

    private int numSets;
    private int numReps;

    String getRepsString(){
        return String.valueOf(getCurrRep()) + " / " + String.valueOf(getNumReps());
    }

    String getSetsString(){
        return String.valueOf(getCurrSet()) + " / " + String.valueOf(getNumSets());
    }

    private boolean countdownEnabled; // Is the countdown turned on?

    private volatile boolean workoutFinished = false; // switch to true when time has run out

    private long getDistanceFromTime(long time){
        return timeLeftInSegment - time;
    }

    // Use this for times between 0 and the length of the current segment, NOT INCLUSIVE
    // (e.g. 10 seconds for "10 seconds" announcement or 3 seconds for countdown)
    boolean isOnTime(long time){
        return getDistanceFromTime(time) == 0;
    }

    private volatile boolean firstRun = true;

    // current set number (number from 1 - numSets (inclusive), but reset to 0 during prep/break)
    private int currSet;

    // current set number (number from 1 - numSets (inclusive), but reset to 0 during prep/break)
    private int currRep;

    private long timeLeftInWorkout; // time left in workout
    private long timeLeftInSegment; // time left in current segment

    private String currSegment;

    // Should have a constructor that takes all these values in
    RiseTimer(int numSets, int numReps, long[] times,
                     String initialSegment, long checkInterval, boolean countdownEnabled) {
        super(checkInterval);

        this.numSets = numSets;
        this.numReps = numReps;

        this.times = new long[RiseTimerStatic.NUM_SEGMENTS];

        // convert split times to milliseconds
        System.arraycopy(times,0,this.times,0,times.length);

        currSet = 0;
        currRep = 0;
        currSegment = initialSegment;

        this.countdownEnabled = countdownEnabled;

        // calculate workout length from times, reps and sets
        workoutLength = times[RiseTimerStatic.PREP_INDEX]
                + numSets * (numReps * times[RiseTimerStatic.WORK_INDEX] + (numReps - 1) * times[RiseTimerStatic.REST_INDEX])
        + (numSets - 1) * times[RiseTimerStatic.BREAK_INDEX];

        timeLeftInWorkout = workoutLength;
        timeLeftInSegment = times[RiseTimerStatic.SEGMENT_MAP.get(initialSegment)];
    }

    // Set initial configuration (only to be run during first run)
    void init(){
        // If initial segment is work, we start right away!
        if (firstRun) {
            if (currSegment.equals(RiseTimerStatic.WORK)) {

                // update sets/reps, we've started!
                currRep++;
                currSet++;
            }

            firstRun = false;
        }
    }

    // Rise timer has a step method, overrides that of SimpleTimer

    @Override
    public void step(){

        if (!(workoutFinished)){
            super.step();

            // Deduct checking interval from time left
            timeLeftInWorkout -= runningCheckInterval;
            timeLeftInSegment -= runningCheckInterval;

            // If we've stepped and our time left in the workout is now 0, finish!
            if (timeLeftInWorkout == 0){
                workoutFinished = true;
            }

            else if (timeLeftInSegment == 0) {

                switch (currSegment) {

                    // If we're coming from PREP, switch to work
                    case RiseTimerStatic.PREP:
                        currSegment = RiseTimerStatic.WORK;
                        timeLeftInSegment = times[RiseTimerStatic.WORK_INDEX];
                        currRep++;
                        currSet++;
                        break;

                    case RiseTimerStatic.WORK:

                        // We either switch to REST or BREAK
                        if (currRep == numReps)
                        {
                            if (times[RiseTimerStatic.BREAK_INDEX] > 0)
                            {
                                currSegment = RiseTimerStatic.BREAK;
                                timeLeftInSegment = times[RiseTimerStatic.BREAK_INDEX];

                                // We're now on rep 0 (i.e. havent started)
                                currRep = 0;

                            }

                            // Otherwise, go back to work on the next set
                            // and we're on the first rep
                            else
                            {
                                if (currSet != numSets)
                                {
                                    timeLeftInSegment = times[RiseTimerStatic.WORK_INDEX];
                                    currRep = 1;
                                    currSet++;
                                }
                            }

                        }

                        // Otherwise we're switching to REST if restTime>0
                        else if (times[RiseTimerStatic.REST_INDEX] > 0)
                        {
                            currSegment = RiseTimerStatic.REST;
                            timeLeftInSegment = times[RiseTimerStatic.REST_INDEX];
                        }
                        else // ...and if not, back to work!
                        {
                            timeLeftInSegment = times[RiseTimerStatic.WORK_INDEX];
                            currRep++;

                        }

                        break;

                    case RiseTimerStatic.REST:

                        // We switch to work
                        currSegment = RiseTimerStatic.WORK;
                        timeLeftInSegment = times[RiseTimerStatic.WORK_INDEX];

                        // We're on a new rep
                        currRep++;

                        break;

                    case RiseTimerStatic.BREAK:

                        // We switch to work
                        currSegment = RiseTimerStatic.WORK;
                        timeLeftInSegment = times[RiseTimerStatic.WORK_INDEX];

                        // We're coming on a new set and new rep
                        currRep++;
                        currSet++;

                        break;
                }
            }
        }
    }

    // Getters (no need for setters, nothing should be able to modify properties after initialization)
    String getCurrSegment(){
        return currSegment;
    }
    long getTimeLeftInSegment(){
        return timeLeftInSegment;
    }
    private int getCurrSet(){
        return currSet;
    }
    private int getCurrRep(){
        return currRep;
    }
    private int getNumReps(){
        return numReps;
    }
    private int getNumSets(){
        return numSets;
    }
    long getTime(int index){ return times[index];}
    boolean isWorkoutFinished(){return workoutFinished;}
    boolean isCountdownEnabled(){return countdownEnabled;}
}
