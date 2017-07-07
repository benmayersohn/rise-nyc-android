package com.therise.nyc.therisenyc;

import android.content.Context;

import java.util.Random;

/**
 * RiseTimerAudioController extends AudioController to sync with RiseTimer cues.
 * Also plays fun sound clips at random, like "Yeah buddy!"
 */

// The RiseTimerAudioController has an awareness of the necessities of the RiseTimer

class RiseTimerAudioController extends AudioController {

    // Store timer
    private RiseTimer riseTimer;

    // The only fun clip we have so far is "Yeah Buddy!"
    private long[] funClipsLowerBounds; // lowerBound[i] pairs with upperBound[i]
    private long[] funClipsUpperBounds;

    private boolean funClipsEnabled;

    private volatile boolean countdownStarted = false;

    private int numFunClipsPlayed = 0;

    // Random variable gives us probability of playing a fun clip
    private Random r = new Random();

    RiseTimerAudioController(Context context, RiseTimer riseTimer, boolean funClipsEnabled){
        super(context);

        this.funClipsEnabled = funClipsEnabled;
        this.riseTimer = riseTimer;

        // We set these later
        // There are two segments. One before the "Ten Seconds" notification, one after.
        funClipsLowerBounds = new long[RiseTimerStatic.NUM_FUN_CLIP_SEGMENTS];
        funClipsUpperBounds = new long[RiseTimerStatic.NUM_FUN_CLIP_SEGMENTS];

        setFunClipBounds();
    }

    // Set appropriate intervals to play clips based on riseTimer info

    private void setFunClipBounds(){
        // Upper bound is the "TEN SECONDS!" mark, if work is longer than 10 seconds
        if (riseTimer.getTime(RiseTimerStatic.WORK_INDEX) > WorkoutStatic.TEN_SECONDS){
            funClipsUpperBounds[0] = WorkoutStatic.TEN_SECONDS - WorkoutStatic.ONE_SECOND;
            funClipsLowerBounds[0] = RiseTimerStatic.COUNTDOWN_BEGIN + WorkoutStatic.ONE_SECOND;
        }
        // Otherwise, not worth having this segment at all. Make it impossible to reach
        else{
            funClipsUpperBounds[0] = 0;
            funClipsLowerBounds[0] = 0;
        }

        // For second branch, go from work time - 1 to ten seconds + 1
        // If work is not 15 seconds or more, not worth it.
        if (riseTimer.getTime(RiseTimerStatic.WORK_INDEX) >= WorkoutStatic.FIFTEEN_SECONDS){
            funClipsUpperBounds[1] = riseTimer.getTime(RiseTimerStatic.WORK_INDEX) - WorkoutStatic.ONE_SECOND;
            funClipsLowerBounds[1] = WorkoutStatic.TEN_SECONDS + WorkoutStatic.ONE_SECOND;
        }
        // Otherwise, not worth having this segment at all. Make it impossible to reach
        else{
            funClipsUpperBounds[1] = 0;
            funClipsLowerBounds[1] = 0;
        }
    }

    private void resetFunClips(){
        numFunClipsPlayed = 0;
    }

    // Are we in the right range to play a fun clip?
    private boolean inFunClipRange(){

        // only play fun clips during work
        if (riseTimer.getCurrSegment().equals(RiseTimerStatic.WORK)){
            for (int i = 0; i < RiseTimerStatic.NUM_FUN_CLIP_SEGMENTS; i++){
                if (riseTimer.getTimeLeftInSegment() > funClipsLowerBounds[i] && riseTimer.getTimeLeftInSegment() < funClipsUpperBounds[i]){
                    return true;
                }
            }
        }

        return false;
    }

    // Include run, which should be calculated every second
    public void run(){

        // We run down the list in order of priorities

        // If the workout is done and the countdown is not enabled, play out the final whistle
        // Since the next segment never starts, the whistle won't play otherwise.
        if (riseTimer.isWorkoutFinished() && !riseTimer.isCountdownEnabled()){
            playSample(R.raw.whistle);
        }

        // If the time left in the current segment is equal to the total time in that segment...
        // reset fun clips and countdown
        if (riseTimer.getTime(RiseTimerStatic.SEGMENT_MAP.get(riseTimer.getCurrSegment())) == riseTimer.getTimeLeftInSegment()){
            resetFunClips();
            countdownStarted = false;

            // play the whistle at the beginning of each new segment, except PREP
            if (!riseTimer.getCurrSegment().equals(RiseTimerStatic.PREP) &&
                    (!riseTimer.isCountdownEnabled() || riseTimer.getTimeElapsed()==0 )){
                playSample(R.raw.whistle);
            }
        }


        // Only play other sounds if we're not in the countdown range
        if (!countdownStarted){

            if (riseTimer.getTimeLeftInSegment() == RiseTimerStatic.COUNTDOWN_BEGIN && riseTimer.isCountdownEnabled()){
                playSample(R.raw.countdown_and_whistle);
                countdownStarted = true;
            }
        }

        // Ten second marker
        // Only play if our work time is at least 20 seconds
        if (riseTimer.getTime(RiseTimerStatic.WORK_INDEX) >= RiseTimerStatic.TEN_SECONDS_MIN_WORK_TIME){
            if (riseTimer.isOnTime(WorkoutStatic.TEN_SECONDS)){
                playSample(R.raw.ten_seconds);
            }
        }

        // Resume if we paused something earlier and started playing again
        if (isMediaPaused()){
            resume();
        }

        // Lastly, play fun clips if nothing else is playing
        if (funClipsEnabled && numFunClipsPlayed < RiseTimerStatic.NUM_FUN_CLIPS_ALLOWED && inFunClipRange() && isStopped()){

            if (r.nextDouble() < RiseTimerStatic.DRAW_PROBABILITY_WORK){
                playSample(RiseTimerStatic.FUN_CLIPS[r.nextInt(RiseTimerStatic.FUN_CLIPS.length)]);
                numFunClipsPlayed++;
            }

        }

    }


}
