package com.therise.nyc.therisenyc;

// Static variables for Rise timer

import java.util.Map;
import java.util.HashMap;

 class RiseTimerStatic {
     static final String PRESET_FILE = "timer_presets.json";

    // four segments
     static final int NUM_SEGMENTS = 4; // work, rest, prep, break

     static final int NUM_TIME_SPLITS = 3; // mins, tens of secs, secs

     static final String RISE_TIMER_ISSUE_DIALOG = "RISE_TIMER_ISSUE_DIALOG";

     static final String WORKOUT_TYPE = "TIMER";

     static final int WORK_INDEX = 0;
     static final int REST_INDEX = 1;
     static final int PREP_INDEX = 2;
     static final int BREAK_INDEX = 3;

     static final int MINS_INDEX = 0;
     static final int SECS_TENS_INDEX = 1;
     static final int SECS_ONES_INDEX = 2;

     static final int[] FUN_CLIPS = new int[]{R.raw.yeah_buddy};

    // Possible timer states
     static final String PREP = "PREP";
     static final String WORK = "WORK";
     static final String REST = "REST";
     static final String BREAK = "BREAK";

     private static Map<String,Integer> makeSegmentMap(){
        Map<String,Integer> map = new HashMap<>();
        map.put(WORK,WORK_INDEX);
        map.put(REST,REST_INDEX);
        map.put(PREP,PREP_INDEX);
        map.put(BREAK,BREAK_INDEX);
        return map;
    }

     static final Map<String,Integer> SEGMENT_MAP = makeSegmentMap();

     static final int ONE_CHAR = 1;
     static final int MAX_SETS_LENGTH = 4;
     static final int MAX_REPS_LENGTH = 4;

     static final boolean ENABLE_FUN_CLIPS = true;
     static final int NUM_FUN_CLIP_SEGMENTS = 2;
     static final int NUM_FUN_CLIPS_ALLOWED = 3;

    // request code for dialog in reference to to this fragment
     static final int REQUEST_CODE = 1;

     static final String RISE_TIMER_THREAD = "RISE_TIMER_THREAD";

     static final String SET = "Set";
     static final String REP = "Rep";

    // Start countdown 3 seconds before
     static final int COUNTDOWN_BEGIN = 3* WorkoutStatic.ONE_SECOND;

     static final int TEN_SECONDS_MIN_WORK_TIME = 20 * WorkoutStatic.ONE_SECOND;

    // probability of drawing a "yeah buddy" on each number draw
     static final double DRAW_PROBABILITY_WORK = 0.1;

    // Size to display number of sets and reps while running (in sp units)
     static final float SETS_TEXT_SIZE = 30;
     static final float REPS_TEXT_SIZE = 30;

}
