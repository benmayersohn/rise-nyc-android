package com.therise.nyc.therisenyc;

// General static variables for workouts

// Contains static variables used across fragments/activities
 class WorkoutStatic {

     static final String EXERCISE_FILE = "exercises.json";

    // REFRESH FILES (discards saved exercises/presets for defaults)
     static final boolean REFRESH_FILES = false;

     static final int[] FIELD_IDS = new int[]{
            R.id.exercise1, R.id.exercise2, R.id.exercise3, R.id.exercise4, R.id.exercise5, R.id.exercise6};

     static final int[] SAVE_BUTTONS = new int[]{
            R.id.save1,R.id.save2,R.id.save3,R.id.save4,
            R.id.save5,R.id.save6};

     static final String FINISH_DIALOG = "FINISH_DIALOG";
     static final String STOP_DIALOG = "STOP_DIALOG";
     static final String SAVE_DIALOG = "SAVE_DIALOG";
     static final String LOAD_DIALOG = "LOAD_DIALOG";

     static final int ONE_SECOND = 1000; // one second in milliseconds
     static final int FIVE_SECONDS = 5*ONE_SECOND;
     static final int TEN_SECONDS = 10*ONE_SECOND;
     static final int FIFTEEN_SECONDS = 15*ONE_SECOND;
     static final int ONE_MINUTE = 60*ONE_SECOND; // one minute in milliseconds
     static final int RUNNING_CHECK_INTERVAL = 50; // MUST be factor of 1000

     static final String TIMER = "TIMER";
     static final String CARDS = "CARDS";
     static final String DICE = "DICE";

     static final String TEST = "TEST";

     static final String INDEX = "INDEX";
     static final int EXERCISE_MAX_LENGTH = 17; // max length of exercises

     static final String SELECTED_ENTRY = "SELECTED_ENTRY";
     static final String DELETE_ENTRY = "DELETE_ENTRY";
     static final String CHOSEN_NAME = "CHOSEN_NAME";
     static final String CHOSEN_EXERCISES = "CHOSEN_EXERCISES";
     static final String WORKOUT_TYPE = "WORKOUT_TYPE";

     static final String SIMPLE_TIMER_THREAD = "SIMPLE_TIMER_THREAD";

     static final String COLON = ":";
     static final String SPACE_DELIM = " ";
     static final String SLASH_DELIM = "/";

     static final String TIMER_INACTIVE = "TIMER_INACTIVE"; // before workout started
     static final String TIMER_PAUSED = "TIMER_PAUSED"; // while paused
     static final String TIMER_RUNNING = "TIMER_RUNNING"; // during workout

     static final int LOAD_CODE = 2;
     static final int SAVE_CODE = 3;

     static final String PRESETS = "PRESETS";
     static final String EXERCISE = "EXERCISE";
     static final String TYPED_NAME = "TYPED_NAME";
     static final String PRESET_TYPE = "PRESET_TYPE";
     static final String PRESET = "PRESET";

     static final String BLANK = "";

     static final int NUM_VIEWS = 3;
     static final int TIMER_VIEW = 0;
     static final int DICE_VIEW = 1;


}
