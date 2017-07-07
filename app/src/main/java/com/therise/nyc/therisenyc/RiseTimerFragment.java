package com.therise.nyc.therisenyc;

import android.graphics.Typeface;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.view.Gravity;
import android.text.Editable;

import android.text.TextWatcher;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.widget.LinearLayout.LayoutParams;
import android.content.Intent;

import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RiseTimerFragment extends Fragment
implements SavePresetsDialogFragment.UpdatePresets{

    private RiseTimerAudioController audioController;

    // Timer
    private RiseTimer riseTimer; // the timer
    private RiseTimerRepeater riseTimerRepeater; // we use this to loop timer

    private View layout;

    // Times is (NUM_TIME_SPLITS x NUM_SEGMENTS), so (3 x 4)
    private EditText[][] times;

    private TextView timeLeftInSegView;
    private TextView timeElapsedView;
    private TextView timerSegView;

    private TextView setsTextView;
    private TextView repsTextView;

    // Views that only appear when inactive
    private EditText editSets;
    private EditText editReps;

    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;

    // Some buttons get swapped, include containers for these views
    private LinearLayout row2Grid1;
    private LinearLayout row3Grid1;
    private LinearLayout row3Grid2;
    private LinearLayout row2Grid2;

    private Button savePresetButton;
    private Button loadPresetButton;

    // Do we want to have the countdown chimes?
    private CheckBox countdownOnBox;

    // ViewUpdater
    private RiseTimerViewUpdater viewUpdater;
    private volatile boolean editTextNonClickable = false; // do this when we press play for first time

    // FragmentManager for loading dialogs
    private FragmentManager fm;

    // Create dialog fragments
    private DialogFragment stopWorkoutFragment;
    private DialogFragment finishWorkoutFragment;
    private DialogFragment loadPresetsFragment;
    private DialogFragment savePresetsFragment;
    private RiseTimerIssueDialogFragment timerIssueFragment;

    private PresetManager<RiseTimerPreset> presets;

    private Handler viewHandler; // on the UI

    private HandlerThread timerThread; // in the background for timer
    private Handler timerHandler;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String presetName;

        switch (resultCode){
            case WorkoutStatic.LOAD_CODE: // load specified preset

                int position = data.getIntExtra(WorkoutStatic.SELECTED_ENTRY,0);
                presetName = presets.getPreset(position).getName();
                boolean deleteEntry = data.getBooleanExtra(WorkoutStatic.DELETE_ENTRY,false);

                if (deleteEntry){
                    presets.deletePreset(presetName);
                }
                else {
                    // refresh view
                    viewHandler.post(new RiseTimerPresetViewUpdater(position));
                }
                break;

            // Save preset
            case WorkoutStatic.SAVE_CODE:
                presetName = data.getStringExtra(WorkoutStatic.CHOSEN_NAME);

                // Write preset
                presets.addPreset(generateCurrentPreset(presetName));

                break;
            default:
                throw new IndexOutOfBoundsException("Invalid result code passed!");
        }

    }

    // Generate preset from fields that are filled out
    public RiseTimerPreset generateCurrentPreset(String name){

        List<Integer> mins = getCleanTimeList(RiseTimerStatic.MINS_INDEX);
        List<Integer> secsTens = getCleanTimeList(RiseTimerStatic.SECS_TENS_INDEX);
        List<Integer> secsOnes = getCleanTimeList(RiseTimerStatic.SECS_ONES_INDEX);

        return new RiseTimerPreset(name,getCleanNumSets(),getCleanNumReps(),mins,secsTens,secsOnes);
    }

    // Create a Runnable class that calls timer and updates views in a loop.
    private class RiseTimerRepeater implements Runnable{

        volatile boolean isLooping = true; // If this is true, loop.

        // Stop the loop
        public void stop(){
            isLooping = false;
        }

        // set up for looping
        void reset() {isLooping = true;}

        @Override
        public void run() {

            // First run
            if (riseTimer.getTimeElapsed() == 0) {
                riseTimer.init();

                // play audio
                audioController.run();
            }

            if (isLooping) {
                riseTimer.step();

                // Are we on a multiple of a second?
                if (riseTimer.isOnSecondMark()){

                    // Update the view
                    viewHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            viewUpdater.updateViewFromTimer();
                        }
                    });

                    // play audio
                    audioController.run();

                    // Are we done?
                    if (riseTimer.isWorkoutFinished()){

                        // stop loop, destroy timer thread
                        stop();
                        timerThread.quitSafely();

                        finishWorkoutFragment.show(fm, WorkoutStatic.FINISH_DIALOG);
                    }

                }

                // if we get here, loop again!
                if (!riseTimer.isWorkoutFinished()){
                    timerHandler.postDelayed(riseTimerRepeater,riseTimer.getRunningCheckInterval());
                }
            }
        }
    }

    // Updates view from a RiseTimerPreset. Only before starting workout. Called from UI thread.
    private class RiseTimerPresetViewUpdater implements Runnable{

        int presetPosition;

        RiseTimerPresetViewUpdater(int position){
            presetPosition = position;
        }

        @Override
        public void run(){

            RiseTimerPreset p = presets.getPreset(presetPosition);

            // Update all the fields
            // (our SegmentWatchers will take care of updating other views)
            editSets.setText(String.format(Locale.US,"%d",p.getNumSets()));
            editReps.setText(String.format(Locale.US,"%d",p.getNumReps()));

            for (int i = 0; i < RiseTimerStatic.NUM_SEGMENTS; i++){
                times[RiseTimerStatic.MINS_INDEX][i].setText(String.format(Locale.US,"%d",p.getTimeMins(i)));
                times[RiseTimerStatic.SECS_TENS_INDEX][i].setText(String.format(Locale.US,"%d",p.getTimeSecsTens(i)));
                times[RiseTimerStatic.SECS_ONES_INDEX][i].setText(String.format(Locale.US,"%d",p.getTimeSecsOnes(i)));
            }

        }
    }

    // Updates view in general, whether the workout has started or not. Called from UI thread.
    private class RiseTimerViewUpdater{

        // Set the current view state
        // Choice of TIMER_INACTIVE, TIMER_RUNNING, TIMER_PAUSED

        RiseTimerFragment fragment; // we need access to the fragment to call dialogs

        String viewState = WorkoutStatic.TIMER_INACTIVE; // inactive when initialized

        // our play button serves two purposes: starting and resuming
        // Different onclick listener in both cases
        // Keep track of when second onclick listener assigned
        boolean resumeOnClickListenerSet = false;

        RiseTimerViewUpdater(RiseTimerFragment fragment){
            this.fragment = fragment;
        }

        // This is called whenever the timer updates
        void updateViewFromTimer(){

            timeElapsedView.setText(TimeTools.millisToTimeString(riseTimer.getTimeElapsed()));
            timeLeftInSegView.setText(TimeTools.millisToTimeString(riseTimer.getTimeLeftInSegment()));

            switch(riseTimer.getCurrSegment()){
                case RiseTimerStatic.WORK:
                    timerSegView.setText(RiseTimerStatic.WORK);
                    timerSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.work_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.work_color));
                    break;
                case RiseTimerStatic.REST:
                    timerSegView.setText(RiseTimerStatic.REST);
                    timerSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.rest_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.rest_color));
                    break;
                case RiseTimerStatic.PREP:
                    timerSegView.setText(RiseTimerStatic.PREP);
                    timerSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.prep_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.prep_color));
                    break;
                case RiseTimerStatic.BREAK:
                    timerSegView.setText(RiseTimerStatic.BREAK);
                    timerSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.break_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getActivity(), R.color.break_color));
                    break;
            }

            setsTextView.setText(riseTimer.getSetsString());
            repsTextView.setText(riseTimer.getRepsString());
        }

        // Set initial view of Rise Timer fragment
        // We get to create most of our view objects
        void setInitialView() {
            timeLeftInSegView = (TextView) layout.findViewById(R.id.seconds_in_segment_display);
            timeElapsedView = (TextView) layout.findViewById(R.id.time_elapsed_display);
            timerSegView = (TextView) layout.findViewById(R.id.current_state_text);

            countdownOnBox = (CheckBox)layout.findViewById(R.id.countdown_checkbox);

            // Specific vals
            editSets = (EditText) layout.findViewById(R.id.num_sets_picker);
            editReps = (EditText) layout.findViewById(R.id.num_reps_picker);

            row2Grid1 = (LinearLayout) layout.findViewById(R.id.row2_grid1);
            row3Grid1 = (LinearLayout) layout.findViewById(R.id.row3_grid1);
            row3Grid2 = (LinearLayout) layout.findViewById(R.id.row3_grid2);
            row2Grid2 = (LinearLayout) layout.findViewById(R.id.row2_grid2);

            // Restrict number of sets/reps we can enter (so we don't overflow these integer fields)
            editReps.setFilters(new InputFilter[] {new InputFilter.LengthFilter(RiseTimerStatic.MAX_REPS_LENGTH)});
            editSets.setFilters(new InputFilter[] {new InputFilter.LengthFilter(RiseTimerStatic.MAX_SETS_LENGTH)});

            // create times
            times = new EditText[RiseTimerStatic.NUM_TIME_SPLITS][RiseTimerStatic.NUM_SEGMENTS];

            // Assign time views
            times[RiseTimerStatic.MINS_INDEX] = new EditText[]{
                    (EditText) layout.findViewById(R.id.work_time_mins),
                    (EditText) layout.findViewById(R.id.rest_time_mins),
                    (EditText) layout.findViewById(R.id.prep_time_mins),
                    (EditText) layout.findViewById(R.id.break_time_mins)
            };

            times[RiseTimerStatic.SECS_TENS_INDEX] = new EditText[]{
                    (EditText) layout.findViewById(R.id.work_time_secs_tens),
                    (EditText) layout.findViewById(R.id.rest_time_secs_tens),
                    (EditText) layout.findViewById(R.id.prep_time_secs_tens),
                    (EditText) layout.findViewById(R.id.break_time_secs_tens)
            };

            times[RiseTimerStatic.SECS_ONES_INDEX] = new EditText[]{
                    (EditText) layout.findViewById(R.id.work_time_secs_ones),
                    (EditText) layout.findViewById(R.id.rest_time_secs_ones),
                    (EditText) layout.findViewById(R.id.prep_time_secs_ones),
                    (EditText) layout.findViewById(R.id.break_time_secs_ones)
            };

            // Set listeners for text changes, and filters
            // Only one number per slot for times
            for (int i = 0; i < RiseTimerStatic.NUM_SEGMENTS; i++) {
                times[RiseTimerStatic.MINS_INDEX][i].addTextChangedListener(new SegmentWatcher(i));
                times[RiseTimerStatic.SECS_TENS_INDEX][i].addTextChangedListener(new SegmentWatcher(i));
                times[RiseTimerStatic.SECS_ONES_INDEX][i].addTextChangedListener(new SegmentWatcher(i));

                times[RiseTimerStatic.MINS_INDEX][i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(RiseTimerStatic.ONE_CHAR)});
                times[RiseTimerStatic.SECS_TENS_INDEX][i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(RiseTimerStatic.ONE_CHAR)});
                times[RiseTimerStatic.SECS_ONES_INDEX][i].setFilters(new InputFilter[] {new InputFilter.LengthFilter(RiseTimerStatic.ONE_CHAR)});
            }

            // we'll use these later, once we start workout
            setsTextView = new TextView(getActivity());
            repsTextView = new TextView(getActivity());

            // set properties for sets/reps display
            setsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,RiseTimerStatic.SETS_TEXT_SIZE);
            repsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,RiseTimerStatic.REPS_TEXT_SIZE);
            setsTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            repsTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            // These buttons have listeners assigned to them.
            playButton = (ImageButton) layout.findViewById(R.id.play_button_image);
            loadPresetButton = (Button) layout.findViewById(R.id.load_presets_text);
            savePresetButton = (Button) layout.findViewById(R.id.save_presets_text);

            // Create fragment that pops up if our workout is invalid
            timerIssueFragment = RiseTimerIssueDialogFragment.newInstance();
            timerIssueFragment.setTargetFragment(getParentFragment(), RiseTimerStatic.REQUEST_CODE);

            // Start timer when we click play
            playButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){

                    // We only start if certain conditions are met
                    // 1) Work must be at least 5 seconds
                    // 2) Prep, Rest and Break must either be set to 0 or made greater than or equal to 5 seconds
                    // 3) We must have at least one rep and one set

                    if (getCleanTime(RiseTimerStatic.WORK_INDEX) < WorkoutStatic.FIVE_SECONDS
                            || (getCleanTime(RiseTimerStatic.PREP_INDEX) > 0 && getCleanTime(RiseTimerStatic.PREP_INDEX) < WorkoutStatic.FIVE_SECONDS)
                            || (getCleanTime(RiseTimerStatic.REST_INDEX) > 0 && getCleanTime(RiseTimerStatic.REST_INDEX) < WorkoutStatic.FIVE_SECONDS)
                            || (getCleanTime(RiseTimerStatic.BREAK_INDEX) > 0 && getCleanTime(RiseTimerStatic.BREAK_INDEX) < WorkoutStatic.FIVE_SECONDS)
                            || (getCleanNumReps() == 0) || (getCleanNumReps() == 0)
                            ) {

                        // Show timerIssueFragment
                        timerIssueFragment.show(fm, RiseTimerStatic.RISE_TIMER_ISSUE_DIALOG);
                    }
                    else{

                        // We're in the clear! We can safely initialize our timer now
                        riseTimer = new RiseTimer(getCleanNumSets(), getCleanNumReps(), getAllCleanTimes(),
                                timerSegView.getText().toString(), WorkoutStatic.RUNNING_CHECK_INTERVAL, countdownOnBox.isChecked());

                        // Disable checkbox
                        countdownOnBox.setEnabled(false);

                        // We can now enable our AudioController!
                        audioController = new RiseTimerAudioController(getContext(),riseTimer,
                                RiseTimerStatic.ENABLE_FUN_CLIPS);

                        // Set running view
                        viewHandler.post(new Runnable(){
                            @Override
                            public void run(){
                                viewUpdater.setRunningView();
                            }
                        });

                        startTimer();
                    }
                }
            });

            // save and load fragments
            loadPresetButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                updateLoadFragment(WorkoutStatic.PRESET,-1);

                if (presets.getNumPresets()>0) {
                    loadPresetsFragment.show(fm, WorkoutStatic.LOAD_DIALOG);
                }
                else{
                    Toast toast = Toast.makeText(getContext(), R.string.no_presets, Toast.LENGTH_SHORT);
                    toast.show();

                }
            }});

            savePresetButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    updateSaveFragment(WorkoutStatic.PRESET, WorkoutStatic.BLANK);
                    savePresetsFragment.show(fm, WorkoutStatic.SAVE_DIALOG);
            }});


            // Display first preset, if we have one stored
            if (presets.getNumPresets()>0) {
                new RiseTimerPresetViewUpdater(0).run();
            }

            viewState = WorkoutStatic.TIMER_INACTIVE;
        }

        // Set paused view of RiseTimer fragment
        // Create layout for paused mode
        // Basically, the pause button becomes a play button and we can now save presets again
        // We still can't load until we stop the workout
        void setPausedView(){

            // Enable saving the preset again since we're paused
            savePresetButton.setEnabled(true);
            savePresetButton.setClickable(true);
            savePresetButton.setBackgroundResource(R.drawable.rect_border2);
            savePresetButton.setTypeface(null, Typeface.NORMAL);

            // set on click listener for play button
            // only do this once
            if (!resumeOnClickListenerSet) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        resumeTimer();
                    }
                });

                resumeOnClickListenerSet = true;
            }

            // Change pause logo to play
            if (row2Grid1.getChildAt(0) != playButton){
                row2Grid1.removeView(pauseButton);
                row2Grid1.addView(playButton);
            }

            viewState = WorkoutStatic.TIMER_PAUSED;
        }

        // Create layout for running mode
        void setRunningView(){

            // If we're coming from the initial inactive view, we need to change a bunch of fields
            if (viewState.equals(WorkoutStatic.TIMER_INACTIVE)){

                // We only want to do the below once
                // Change reps/sets from editable field to display of number we've done
                if (!editTextNonClickable) {

                    TextView setsTextDisplay = (TextView) row3Grid1.getChildAt(1);
                    setsTextDisplay.setText(RiseTimerStatic.SET);

                    TextView repsTextDisplay = (TextView) row3Grid2.getChildAt(1);
                    repsTextDisplay.setText(RiseTimerStatic.REP);

                    // set gravity of row3grid1 to bottom
                    row3Grid1.setGravity(Gravity.BOTTOM);
                    row3Grid2.setGravity(Gravity.BOTTOM);

                    // Format edit text fields for running view
                    for (int i = 0; i < RiseTimerStatic.NUM_SEGMENTS; i++){
                        formatEditText(times[RiseTimerStatic.MINS_INDEX][i]);
                        formatEditText(times[RiseTimerStatic.SECS_TENS_INDEX][i]);
                        formatEditText(times[RiseTimerStatic.SECS_ONES_INDEX][i]);
                    }

                    // get rid of editreps and editsets

                    row3Grid1.removeView(row3Grid1.getChildAt(0));
                    row3Grid2.removeView(row3Grid2.getChildAt(0));

                    setsTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    repsTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                    // Set padding for aesthetic purposes, otherwise layout looks strange
                    setsTextView.setPadding(0,0,0,(int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                    repsTextView.setPadding(0,0,0,(int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));

                    row3Grid1.addView(setsTextView,0);
                    row3Grid2.addView(repsTextView,0);

                    editTextNonClickable = true;
                }

                if (pauseButton == null) {
                    pauseButton = new ImageButton(getActivity());
                    pauseButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                    pauseButton.setBackgroundResource(R.drawable.rect_border2);

                    // Add an OnClickListener for the pause button
                    pauseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {pauseTimer();
                            }
                        });
                }

                if (stopButton == null){

                    // Create dialog fragment
                    stopWorkoutFragment = StopWorkoutDialogFragment.newInstance(RiseTimerStatic.WORKOUT_TYPE);
                    stopWorkoutFragment.setTargetFragment(fragment, RiseTimerStatic.REQUEST_CODE);

                    stopButton = new ImageButton(getActivity());
                    stopButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    stopButton.setImageResource(R.drawable.ic_stop_black_24dp);
                    stopButton.setBackgroundResource(R.drawable.rect_border2);

                    stopButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            pauseTimer();

                            // Show stopWorkoutFragment
                            stopWorkoutFragment.show(fm, WorkoutStatic.STOP_DIALOG);
                        }
                    });
                }

                // We now need to turn the "Load Preset" button into a stop button
                row2Grid2.removeView(loadPresetButton);
                row2Grid2.addView(stopButton);
            }

            // Make "Save Preset" unclickable while we're running
            savePresetButton.setEnabled(false);
            savePresetButton.setClickable(false);
            savePresetButton.setBackgroundResource(R.drawable.rect_border5);
            savePresetButton.setTypeface(null, Typeface.BOLD_ITALIC);

            // In all cases, replace play button with pause button
            if (row2Grid1.getChildAt(0) != pauseButton) {
                row2Grid1.removeView(playButton);
                row2Grid1.addView(pauseButton);
            }

            setsTextView.setText(riseTimer.getSetsString());
            repsTextView.setText(riseTimer.getRepsString());

            // Finally set view state to running
            viewState = WorkoutStatic.TIMER_RUNNING;
        }

    }

    void startTimer(){
        // Set up handler
        timerThread = new HandlerThread(RiseTimerStatic.RISE_TIMER_THREAD);
        timerThread.start();
        timerHandler = new Handler(timerThread.getLooper());

        riseTimerRepeater = new RiseTimerRepeater();

        // Set running view
        viewHandler.post(new Runnable() {
            @Override
            public void run() {
                viewUpdater.setRunningView();
            }
        });

        // start timer loop
        riseTimerRepeater.reset();
        timerHandler.post(riseTimerRepeater);
    }

    void resumeTimer() {
        audioController.resume();

        // Set timer state to running
        startTimer();
    }

    void stopTimer(){
        if (riseTimerRepeater != null) {
            audioController.pause();
            riseTimerRepeater.stop();
            timerThread.quitSafely();
        }
    }

    void pauseTimer(){
        stopTimer();
        if (riseTimerRepeater != null) {
            viewHandler.post(new Runnable() {
                @Override
                public void run() {
                    viewUpdater.setPausedView();
                }
            });
        }
    }

    // We get the name of the tab and the view from the activity
    public static RiseTimerFragment newInstance() {
        return new RiseTimerFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        fm = getActivity().getSupportFragmentManager();
        super.onCreate(savedInstanceState);

    }

    @Override
    // Use this to keep activity in place after rotation etc.
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Keep around after screen rotation
        setRetainInstance(true);
    }

    // returns time reflected in view for a given segment
    // This can easily be converted to a split array using TimeTools
    private long getCleanTime(int segmentIndex){
        String minsEntry = times[RiseTimerStatic.MINS_INDEX][segmentIndex].getText().toString();
        String secsTensEntry = times[RiseTimerStatic.SECS_TENS_INDEX][segmentIndex].getText().toString();
        String secsOnesEntry = times[RiseTimerStatic.SECS_ONES_INDEX][segmentIndex].getText().toString();

        int[] times = new int[RiseTimerStatic.NUM_TIME_SPLITS];

        times[RiseTimerStatic.MINS_INDEX] = minsEntry.trim().equals("") ? 0 : Integer.valueOf(minsEntry);
        times[RiseTimerStatic.SECS_TENS_INDEX] = secsTensEntry.trim().equals("") ? 0 : Integer.valueOf(secsTensEntry);
        times[RiseTimerStatic.SECS_ONES_INDEX] = secsOnesEntry.trim().equals("") ? 0 : Integer.valueOf(secsOnesEntry);

        return TimeTools.splitTimeToMillis(times);
    }

    // Get all times in milliseconds across segments
    // "Clean up" in case we've left any fields blank
    private long[] getAllCleanTimes(){

        long[] cleanTimes = new long[RiseTimerStatic.NUM_SEGMENTS];
        for (int i = 0; i < RiseTimerStatic.NUM_SEGMENTS; i++){
            cleanTimes[i] = getCleanTime(i);
        }

        return cleanTimes;
    }

    // Returns a given time denomination across segments
    private List<Integer> getCleanTimeList(int timeIndex){
        List<Integer> timeList = new ArrayList<>();
        String timeEntry;

        for (int i = 0; i < RiseTimerStatic.NUM_SEGMENTS; i++){
            timeEntry = times[timeIndex][i].getText().toString();

            // Is the input valid?
            timeList.add(timeEntry.trim().equals("") ? 0 : Integer.valueOf(timeEntry));
        }

        return timeList;
    }

    // number of sets
    private int getCleanNumSets(){
        int numSets;
        String setEntry = editSets.getText().toString();
        if (setEntry.trim().equals("")){
            numSets = 0;
        }
        else {
            numSets = Integer.valueOf(setEntry);
        }
        return numSets;

    }

    // number of reps
    private int getCleanNumReps(){
        int numReps;
        String repEntry = editReps.getText().toString();
        if (repEntry.trim().equals("")){
            numReps = 0;
        }
        else {
            numReps = Integer.valueOf(repEntry);
        }
        return numReps;
    }

    private class SegmentWatcher implements TextWatcher {

        int index; // which segment?

        SegmentWatcher(int index) {
            this.index = index;
        }

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {

            if (index == RiseTimerStatic.PREP_INDEX) {

                // If no prep, set display to amount of work time
                if (getCleanTime(index) == 0) {
                    timerSegView.setText(RiseTimerStatic.WORK);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                    timeLeftInSegView.setText(TimeTools.millisToTimeString(getCleanTime(RiseTimerStatic.WORK_INDEX)));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                }
                else { // otherwise, set display to prep time
                    timerSegView.setText(RiseTimerStatic.PREP);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                    timeLeftInSegView.setText(TimeTools.millisToTimeString(getCleanTime(index)));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                }
            }
            else if (index == RiseTimerStatic.WORK_INDEX) {
                // If our prep time is zero, then we display the work time
                // Otherwise view doesn't change
                if (getCleanTime(index) == 0) {
                    timeLeftInSegView.setText(TimeTools.millisToTimeString(getCleanTime(index)));
                }
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        layout = inflater.inflate(R.layout.fragment_timer_layout, container, false);

        // initialize fragments
        finishWorkoutFragment = FinishWorkoutDialogFragment.newInstance(RiseTimerStatic.WORKOUT_TYPE);
        finishWorkoutFragment.setTargetFragment(this, RiseTimerStatic.REQUEST_CODE);

        // Initialize PresetManager
        presets = new PresetManager<>(RiseTimerStatic.PRESET_FILE, getActivity(), new TypeToken<PresetHolder<RiseTimerPreset>>(){}.getType());
        presets.loadPresets();

        viewHandler = new Handler(Looper.getMainLooper());

        // Create ViewUpdater
        viewUpdater = new RiseTimerViewUpdater(this);

        // Set initial view
        viewUpdater.setInitialView();

        return layout;

    }

    @Override
    public void onDestroy() {

        stopTimer();

        super.onDestroy();
    }

    @Override
    public void onPause() {
        pauseTimer();
        super.onPause();
    }

    @Override
    public void onStop(){
        stopTimer();
        super.onStop();
    }

    private void formatEditText(EditText e){
        // Disable editing of text when we're running
        e.setInputType(InputType.TYPE_NULL);
        e.setFocusable(false);
        e.setClickable(false);

        // if blank set entries to zero
        if (e.getText().length()==0){
            e.setText(0);
        }
    }

    @Override
    public void updateSaveFragment(String presetType, String typedValue){
        // Create save fragment
        if (!(presets == null)) {
            savePresetsFragment = SavePresetsDialogFragment.newInstance(presets.getNames(),RiseTimerStatic.WORKOUT_TYPE,presetType,typedValue);
            savePresetsFragment.setTargetFragment(this, RiseTimerStatic.REQUEST_CODE);
        }
    }

    @Override
    public void updateLoadFragment(String presetType, int index){
        // Create load fragment
        if (!(presets == null) && presets.getNumPresets()>0) {
            loadPresetsFragment = LoadPresetsDialogFragment.newInstance(presets.getNames(),presetType,index);
            loadPresetsFragment.setTargetFragment(this, RiseTimerStatic.REQUEST_CODE);
        }
    }
}