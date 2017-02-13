package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.Button;
import android.view.Gravity;
import android.text.Editable;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import android.text.TextWatcher;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.widget.LinearLayout.LayoutParams;
import android.content.Intent;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Locale;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.File;
import android.media.MediaPlayer;
import android.widget.Toast;
import java.util.Random;

public class TimerFragment extends Fragment
implements SavePresetsDialogFragment.UpdatePresets{

    // Possible timer states

    // we're on timer view
    private static final String PRESET_FILE = "timer_presets.json";
    private static final String STOP_DIALOG = "STOP_DIALOG";
    private static final String END_DIALOG = "END_DIALOG";
    private static final String LOAD_DIALOG = "LOAD_DIALOG";
    private static final String SAVE_DIALOG = "SAVE_DIALOG";

    private static final String TIMER_INACTIVE = "TIMER_INACTIVE"; // before workout started
    private static final String TIMER_PAUSED = "TIMER_PAUSED"; // while paused

    private static final int ONE_SECOND = 1000; // one second in milliseconds
    private static final int ONE_MINUTE = 60*ONE_SECOND; // one minute in milliseconds
    private static final int RUNNING_CHECK_INTERVAL = 50; // MUST be multiple of 1000 but LESS

    // Start countdown 3 seconds before
    private static final int COUNTDOWN_BEGIN = 3*ONE_SECOND;

    private static final int TEN_SECONDS = 10*ONE_SECOND;

    private static final String PREP = "PREP";
    private static final String WORK = "WORK";
    private static final String REST = "REST";
    private static final String BREAK = "BREAK";

    private static final int YEAH_BUDDY_LENGTH = ONE_SECOND;

    // yeah buddies allowed between two segments (for timeLeft - segLowerBound)
    // workLength - ONE_SECOND and restLength + ONE_SECOND
    // restLength - ONE_SECOND and COUNTDOWN_TIME
    private long yeahBuddyLowerBound1;
    private long yeahBuddyUpperBound1;
    private long yeahBuddyLowerBound2;
    private long yeahBuddyUpperBound2;

    // length of time where yeah buddies should not be called
    private static final long YEAH_BUDDY_BUFFER = 4*ONE_SECOND + COUNTDOWN_BEGIN;
    private long yeahBuddiesLength;

    // Allow two yeah buddies per segment at random intervals
    private final int numYeahBuddiesWork = 2;
    private final int numYeahBuddiesRest = 1;
    private int numYeahBuddiesPlayedWork = 0;
    private int numYeahBuddiesPlayedRest = 0;

    private Random r = new Random();
    private double probability;

    // probability of drawing a "yeah buddy" on each number draw
    private static final double DRAW_PROBABILITY_WORK = 0.1;

    private static final String TEST = "TEST";

    private static final String SET = "Set";
    private static final String REP = "Rep";

    // Size to display number of sets and reps while running (in sp units)
    private static final float SETS_TEXT_SIZE = 30;
    private static final float REPS_TEXT_SIZE = 30;

    // Views

    private View layout;

    private LinearLayout row1Grid1;
    private LinearLayout row1Grid2;
    private LinearLayout row1Grid3;

    private LinearLayout row2Grid1;
    private LinearLayout row2Grid2;
    private LinearLayout row2Grid3;

    private LinearLayout row3Grid1;
    private LinearLayout row3Grid2;
    private LinearLayout row3Grid3;

    private LinearLayout row4Grid1;
    private LinearLayout row4Grid2;
    private LinearLayout row4Grid3;

    private LinearLayout workTimeBox;
    private LinearLayout restTimeBox;
    private LinearLayout prepTimeBox;
    private LinearLayout breakTimeBox;

    private TextView timeLeftInSegView;
    private TextView timeElapsedView;
    private TextView timerSegView;

    private TextView setsTextView;
    private TextView repsTextView;

    // Views that only appear when inactive
    private EditText editSets;
    private EditText editReps;
    private EditText workTimeMins;
    private EditText workTimeSecsTens;
    private EditText workTimeSecsOnes;
    private EditText restTimeMins;
    private EditText restTimeSecsTens;
    private EditText restTimeSecsOnes;
    private EditText prepTimeMins;
    private EditText prepTimeSecsTens;
    private EditText prepTimeSecsOnes;
    private EditText breakTimeMins;
    private EditText breakTimeSecsTens;
    private EditText breakTimeSecsOnes;

    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;
    private Button savePresetButton;
    private Button loadPresetButton;

    private TextView setsTextDisplay;
    private TextView repsTextDisplay;

    private long workoutLength;
    private long restTime;
    private long workTime;
    private long prepTime;
    private long breakTime;
    private int numSets;
    private int numReps;

    private int workMins;
    private int workSecsTens;
    private int workSecsOnes;
    private int restMins;
    private int restSecsTens;
    private int restSecsOnes;
    private int prepMins;
    private int prepSecsTens;
    private int prepSecsOnes;
    private int breakMins;
    private int breakSecsTens;
    private int breakSecsOnes;

    // current set number (number from 1 - numSets (inclusive), but reset to 0 during prep/break)
    private int currSet=0;

    // current set number (number from 1 - numSets (inclusive), but reset to 0 during prep/break)
    private int currRep=0;

    private long elapsedTime = 0;
    private long timeLeft;
    private long timeInSegment;

    // Preset name to save
    private String presetName;

    private String timerState = TIMER_INACTIVE;
    private String currSegment = PREP;

    // Used in count down timer to kill timer when paused/stopped
    private volatile boolean timerRunning;
    private volatile boolean runningViewSet;
    private volatile boolean editTextNonClickable = false; // do this when we press play for first time
    private volatile boolean countdownStarted = false;
    private volatile boolean mediaPlayerPaused = false;
    private volatile boolean firstRun = true;

    // RESULT_CODES
    private static final int PAUSE_CODE = 0;
    private static final int STOP_CODE = 1;
    private static final int LOAD_CODE = 2;
    private static final int SAVE_CODE = 3;

    // Criterion to end segment
    private long segLowerBound;

    // DialogFragment for when we stop timer
    private FragmentTransaction ft;

    // Create dialogs
    private DialogFragment stopFragment;
    private DialogFragment endFragment;
    private DialogFragment loadPresetsFragment;
    private DialogFragment savePresetsFragment;

    // For presets (load while setting up UI)
    private Gson gson;
    private TimerPreset timerPreset;
    private AllTimerPresets presets;
    private JsonReader jsonReader;
    private FileWriter fileWriter;
    private File jsonFile;

    private MediaPlayer mediaPlayer;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private RiseTimer riseTimer = new RiseTimer();

    // request code for dialog in reference to to this fragment
    private static final int REQUEST_CODE = 12345;
    private static final String SELECTED_ENTRY = "SELECTED_ENTRY";
    private static final String DELETE_ENTRY = "DELETE_ENTRY";
    private static final String CHOSEN_NAME = "CHOSEN_NAME";

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PAUSE_CODE){
            timerState = TIMER_PAUSED;
        }
        else if (resultCode == STOP_CODE){
            timerState = TIMER_INACTIVE;

        }

        // Load presets
        else if (resultCode == LOAD_CODE){

            int position = data.getIntExtra(SELECTED_ENTRY,0);
            presetName = presets.getPreset(position).getName();
            boolean deleteEntry = data.getBooleanExtra(DELETE_ENTRY,false);


            if (deleteEntry){
                removeFromJson(presets.getPreset(position));
            }
            else {
                // refresh view
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        insertPreset(presetName);
                    }
                }, RUNNING_CHECK_INTERVAL);
            }
        }

        // Save presets
        else if (resultCode == SAVE_CODE){
            presetName = data.getStringExtra(CHOSEN_NAME);

            // Write preset

            writeToJson(generateCurrentPreset(presetName));
        }


    }

    public TimerPreset generateCurrentPreset(String name){
        return new TimerPreset(name,numSets,numReps,
                workMins,workSecsTens,workSecsOnes,
                restMins,restSecsTens,restSecsOnes,
                prepMins,prepSecsTens,prepSecsOnes,
                breakMins,breakSecsTens,breakSecsOnes);
    }

    public void addPreset(String name, int position){
        presets.addPreset(position, generateCurrentPreset(name));
    }

    public void setPreset(String name, int position){
        presets.setPreset(position, generateCurrentPreset(name));
    }

    public void addPreset(String name){
        presets.addPreset(generateCurrentPreset(name));
    }

    public void insertPreset(String name){
        if (presets != null) {
            timerPreset = presets.getPreset(name);

            // Fill in UI fields from preset
            editReps.setText(String.valueOf(timerPreset.getNumReps()));
            editSets.setText(String.valueOf(timerPreset.getNumSets()));
            workTimeMins.setText(String.valueOf(timerPreset.getNumWorkMins()));
            workTimeSecsOnes.setText(String.valueOf(timerPreset.getNumWorkSecsOnes()));
            workTimeSecsTens.setText(String.valueOf(timerPreset.getNumWorkSecsTens()));
            restTimeMins.setText(String.valueOf(timerPreset.getNumRestMins()));
            restTimeSecsOnes.setText(String.valueOf(timerPreset.getNumRestSecsOnes()));
            restTimeSecsTens.setText(String.valueOf(timerPreset.getNumRestSecsTens()));
            prepTimeMins.setText(String.valueOf(timerPreset.getNumPrepMins()));
            prepTimeSecsOnes.setText(String.valueOf(timerPreset.getNumPrepSecsOnes()));
            prepTimeSecsTens.setText(String.valueOf(timerPreset.getNumPrepSecsTens()));
            breakTimeMins.setText(String.valueOf(timerPreset.getNumBreakMins()));
            breakTimeSecsOnes.setText(String.valueOf(timerPreset.getNumBreakSecsOnes()));
            breakTimeSecsTens.setText(String.valueOf(timerPreset.getNumBreakSecsTens()));

            // update stored fields
            updateWork();
            updateRest();
            updatePrep();
            updateBreak();

            // Set time displayed in segment
            if (prepTime != 0) {
                timeLeftInSegView.setText(millisToTimeString(prepTime));
                timerSegView.setText(PREP);
                timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
            } else {
                timeLeftInSegView.setText(millisToTimeString(workTime));
                timerSegView.setText(WORK);
                timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
            }
        }

    }

    public TimerFragment() {};

    private class RiseTimerViewUpdate implements Runnable{
        @Override public void run(){

            if (!runningViewSet){
                setRunningView();

                runningViewSet = true;
            }

            timeElapsedView.setText(millisToTimeString(elapsedTime));
            timeLeftInSegView.setText(millisToTimeString(timeInSegment));

            switch(currSegment){
                case WORK:
                    timerSegView.setText(WORK);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                    break;
                case REST:
                    timerSegView.setText(REST);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.rest_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.rest_color));
                    break;
                case PREP:
                    timerSegView.setText(PREP);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                    break;
                case BREAK:
                    timerSegView.setText(BREAK);
                    timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.break_color));
                    timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.break_color));
                    break;
            }

            setsTextView.setText(getSetsString());
            repsTextView.setText(getRepsString());
        }
    }

    private void startTimer(){
        timerRunning = true;
        runningViewSet = false;
        timerHandler.postDelayed(riseTimer,RUNNING_CHECK_INTERVAL);
    }

    private class RiseTimer implements Runnable{
        // Set correct mode and view. We're running!

        public void run(){

            // play whistle if this is our first run and we're on work
            // (i.e. if we have no prep. we start the whistle immediately)
            if (firstRun && currSegment.equals(WORK)){
                // This situation will only arise if we have no prep time and start on work

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(getContext(), R.raw.whistle);
                mediaPlayer.start();

                firstRun = false;
            }

            // resume media player if we paused earlier
            if (mediaPlayerPaused){
                mediaPlayer.start();
                mediaPlayerPaused = false;
            }

            if (timerRunning && timeLeft>0){

                timeLeft -= RUNNING_CHECK_INTERVAL;

                // break if we're done
                if (timeLeft <= 0){
                    timerRunning = false;
                    timerState = TIMER_INACTIVE;

                    // Create dialog fragment
                    endFragment = EndTimerDialogFragment.newInstance();
                    endFragment.setTargetFragment(getParentFragment(), REQUEST_CODE);

                    timerRunning = false;
                    timerState = TIMER_INACTIVE;

                    ft = getActivity().getSupportFragmentManager().beginTransaction();

                    // Show stopFragment
                    endFragment.show(ft, END_DIALOG);
                }

                // onTick, update #AllTheThings

                else if (timeLeft % ONE_SECOND == 0) {

                    // Are we playing a sound?

                    // We start our countdown three seconds before the transition

                    // probability of playing a "yeah buddy!" clip
                    probability = r.nextDouble();

                    if (timeLeft - segLowerBound <= COUNTDOWN_BEGIN){

                        if (timeLeft > segLowerBound) {

                            if (mediaPlayer == null) {
                                mediaPlayer = MediaPlayer.create(getContext(), R.raw.countdown_and_whistle);
                            }
                            else if (!(countdownStarted)){
                                mediaPlayer.release();
                                mediaPlayer = null;
                                mediaPlayer = MediaPlayer.create(getContext(), R.raw.countdown_and_whistle);
                            }

                            if (!countdownStarted) {
                                mediaPlayer.start();
                                countdownStarted = true;
                            }
                        }
                    }

                    else if (timeLeft - segLowerBound == TEN_SECONDS && (currSegment.equals(WORK) || currSegment.equals(BREAK))){

                        if (!(mediaPlayer == null)){

                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                            }

                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.ten_seconds);
                        mediaPlayer.start();


                    }

                    else if (
                        ( (timeLeft - segLowerBound > yeahBuddyLowerBound1 && timeLeft - segLowerBound < yeahBuddyUpperBound1) ||
                          (timeLeft - segLowerBound > yeahBuddyLowerBound2 && timeLeft - segLowerBound < yeahBuddyUpperBound2))
                        ){

                        if ((numYeahBuddiesPlayedWork < numYeahBuddiesWork) && ((probability <DRAW_PROBABILITY_WORK) && currSegment.equals(WORK))){

                            // Play yeah buddy! But not if we're already playing
                            if (!(mediaPlayer == null)){

                                if (!(mediaPlayer.isPlaying())) {
                                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.yeah_buddy);
                                    mediaPlayer.start();
                                    numYeahBuddiesPlayedWork++;
                                }
                            }
                            else{
                                mediaPlayer = MediaPlayer.create(getContext(), R.raw.yeah_buddy);
                                mediaPlayer.start();
                                numYeahBuddiesPlayedWork++;
                            }

                            // Release media player if we have no more left
                            if (numYeahBuddiesPlayedWork == numYeahBuddiesWork){
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }

                        }

                    }

                    else{
                        // Otherwise turn off media player
                        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        // no countdown
                        countdownStarted = false;
                    }

                    // update variables
                    elapsedTime += ONE_SECOND;
                    timeInSegment -= ONE_SECOND;

                    // Move on to new segment
                    if (timeLeft <= segLowerBound) {

                        // Reset "yeah buddy" counter
                        numYeahBuddiesPlayedWork = 0;

                        // What kind of segment have we moved on to?
                        switch (currSegment) {

                            // If we're coming from PREP, switch to work
                            case PREP:
                                currSegment = WORK;
                                segLowerBound -= workTime;
                                timeInSegment = workTime;
                                currRep++;
                                currSet++;
                                break;

                            case WORK:

                                // We either switch to REST or BREAK
                                if (currRep == numReps) {
                                    currSegment = BREAK;
                                    segLowerBound -= breakTime;
                                    timeInSegment = breakTime;

                                    // We're now on rep 0 (i.e. havent started)
                                    currRep = 0;
                                    break;
                                }

                                // Otherwise we're switching to REST
                                currSegment = REST;
                                timeInSegment = restTime;
                                segLowerBound -= restTime;

                                break;

                            case REST:

                                // We switch to work
                                currSegment = WORK;
                                timeInSegment = workTime;
                                segLowerBound -= workTime;

                                // We're on a new rep
                                currRep++;

                                break;

                            case BREAK:

                                // We switch to work
                                currSegment = WORK;
                                timeInSegment = workTime;
                                segLowerBound -= workTime;

                                // We're coming on a new set and new rep
                                currRep++;
                                currSet++;

                                break;

                        }
                    }

                    // Now update views based on new information
                    (new Handler(Looper.getMainLooper())).postDelayed(new RiseTimerViewUpdate(),RUNNING_CHECK_INTERVAL);

                // End updates on one second interval
                }

                // If we haven't set running view yet, do so
                if (!runningViewSet){
                    (new Handler(Looper.getMainLooper())).postDelayed(new RiseTimerViewUpdate(),RUNNING_CHECK_INTERVAL);
                }

                // Lastly, here's the key! Run a new handler to make this loop
                // To stop timer, we can simply call timerHandler.removeCallbacks(riseTimer)
                timerHandler.postDelayed(this,RUNNING_CHECK_INTERVAL);
            }

        // end of run method for RiseTimer
        };

    // end of RiseTimer class
    }

    private String getRepsString(){
        return String.valueOf(currRep) + " / " + String.valueOf(numReps);
    }

    private String getSetsString(){
        return String.valueOf(currSet) + " / " + String.valueOf(numSets);
    }

    // Take minutes and seconds and convert to time in milliseconds
    private long timeToMillis(int numMins, int numSecs){
        return ONE_MINUTE * numMins + ONE_SECOND * numSecs;
    }

    // Take time in milliseconds and convert to "m:ss" format
    private String millisToTimeString(long millis){
        // floor of quotient is the number of minutes
        int numMins = (int) Math.floor(1.0*millis/(1.0*ONE_MINUTE));

        // remainder is seconds
        int numSecs = (int) Math.floor(1.0*(millis - numMins * ONE_MINUTE)/(1.0*ONE_SECOND));

        String minString = String.valueOf(numMins);
        String secString = String.format(Locale.US,"%02d",numSecs);

        return minString + ":" + secString;
    }

    // We get the name of the tab and the view from the activity
    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();

        return fragment;
    }

    public void writeToJson(TimerPreset p){
        presets.addPreset(p);

        // save
        try{
            // Set up output writer
            fileWriter = new FileWriter(jsonFile);

            // write
            gson.toJson(presets,fileWriter);

            // close writer
            fileWriter.close();
        }
        catch(Exception e){}
    }

    public void removeFromJson(TimerPreset p){
        presets.removePreset(p);

        // save
        try{
            // Set up output writer
            fileWriter = new FileWriter(jsonFile);

            // write
            gson.toJson(presets,fileWriter);

            // close writer
            fileWriter.close();
        }
        catch(Exception e){}
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If our media player is still around, make it null
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }


    }

    @Override
    // Use this to keep activity in place after rotation etc.
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Keep around after screen rotation
        setRetainInstance(true);
    }

    // updateWork: update stored values of workTime from EditText
    private void updateWork(){
        String workMinsEntry = workTimeMins.getText().toString();
        String workSecsTensEntry = workTimeSecsTens.getText().toString();
        String workSecsOnesEntry = workTimeSecsOnes.getText().toString();


        if (workMinsEntry.trim().equals("")){
            workMins = 0;
        }
        else {
            workMins = Integer.valueOf(workMinsEntry);
        }


        if (workSecsTensEntry.trim().equals("")){
            workSecsTens = 0;
        }
        else {
            workSecsTens = Integer.valueOf(workSecsTensEntry);
        }

        if (workSecsOnesEntry.trim().equals("")){
            workSecsOnes = 0;
        }
        else {
            workSecsOnes = Integer.valueOf(workSecsOnesEntry);
        }

        workTime = timeToMillis(workMins,workSecsOnes + 10*workSecsTens);

        // update segment time
        if (prepTime == 0){
            timeInSegment = workTime;
        }

    }

    // updateRest: update stored values of restTime from EditText
    private void updateRest(){
        String restMinsEntry = restTimeMins.getText().toString();
        String restSecsTensEntry = restTimeSecsTens.getText().toString();
        String restSecsOnesEntry = restTimeSecsOnes.getText().toString();


        if (restMinsEntry.trim().equals("")){
            restMins = 0;
        }
        else {
            restMins = Integer.valueOf(restMinsEntry);
        }


        if (restSecsTensEntry.trim().equals("")){
            restSecsTens = 0;
        }
        else {
            restSecsTens = Integer.valueOf(restSecsTensEntry);
        }

        if (restSecsOnesEntry.trim().equals("")){
            restSecsOnes = 0;
        }
        else {
            restSecsOnes = Integer.valueOf(restSecsOnesEntry);
        }

        restTime = timeToMillis(restMins,restSecsOnes + 10*restSecsTens);

    }

    // updatePrep: update stored values of prepTime from EditText
    private void updatePrep(){

        String prepMinsEntry = prepTimeMins.getText().toString();
        String prepSecsTensEntry = prepTimeSecsTens.getText().toString();
        String prepSecsOnesEntry = prepTimeSecsOnes.getText().toString();

        if (prepMinsEntry.trim().equals("")){
            prepMins = 0;
        }
        else {
            prepMins = Integer.valueOf(prepMinsEntry);
        }


        if (prepSecsTensEntry.trim().equals("")){
            prepSecsTens = 0;
        }
        else {
            prepSecsTens = Integer.valueOf(prepSecsTensEntry);
        }

        if (prepSecsOnesEntry.trim().equals("")){
            prepSecsOnes = 0;
        }
        else {
            prepSecsOnes = Integer.valueOf(prepSecsOnesEntry);
        }

        prepTime = timeToMillis(prepMins,prepSecsOnes + 10*prepSecsTens);

        // update segment time
        if (prepTime != 0){
            timeInSegment = prepTime;
        }
        else{
            timeInSegment = workTime;
        }

    }

    // updateBreak: update stored values of breakTime from EditText
    private void updateBreak(){
        String breakMinsEntry = breakTimeMins.getText().toString();
        String breakSecsTensEntry = breakTimeSecsTens.getText().toString();
        String breakSecsOnesEntry = breakTimeSecsOnes.getText().toString();


        if (breakMinsEntry.trim().equals("")){
            breakMins = 0;
        }
        else {
            breakMins = Integer.valueOf(breakMinsEntry);
        }


        if (breakSecsTensEntry.trim().equals("")){
            breakSecsTens = 0;
        }
        else {
            breakSecsTens = Integer.valueOf(breakSecsTensEntry);
        }

        if (breakSecsOnesEntry.trim().equals("")){
            breakSecsOnes = 0;
        }
        else {
            breakSecsOnes = Integer.valueOf(breakSecsOnesEntry);
        }

        breakTime = timeToMillis(breakMins,breakSecsOnes + 10*breakSecsTens);

    }

    // Update workout length
    private void updateWorkoutLength(){

        workoutLength = prepTime + numSets*(numReps*workTime + (numReps-1)*restTime)
                + (numSets-1)*breakTime;
        // Set timeLeft equal to workout length
        timeLeft = workoutLength;
        if (currSegment.equals(PREP)) {
            segLowerBound = workoutLength - prepTime;
        }

        // Otherwise prep=0
        else{
            segLowerBound = workoutLength - workTime;
        }

        // Update yeah buddy intervals

        // Set bounds for playing "Yeah buddies"
        // workTime - ONE_SECOND and restTime + ONE_SECOND
        // restTime - ONE_SECOND and COUNTDOWN_BEGIN
        yeahBuddyUpperBound1 = workTime - ONE_SECOND;
        yeahBuddyLowerBound1 = restTime + ONE_SECOND;
        yeahBuddyUpperBound2 = restTime - ONE_SECOND;
        yeahBuddyLowerBound2 = COUNTDOWN_BEGIN+ONE_SECOND;

        yeahBuddiesLength = workTime - YEAH_BUDDY_BUFFER;

    }

    // update number of sets
    private void updateNumSets(){
        String setEntry = editSets.getText().toString();
        if (setEntry.trim().equals("")){
            numSets = 0;
        }
        else {
            numSets = Integer.valueOf(setEntry);
        }

    }

    // update number of reps
    private void updateNumReps(){
        String repEntry = editReps.getText().toString();
        if (repEntry.trim().equals("")){
            numReps = 0;
        }
        else {
            numReps = Integer.valueOf(repEntry);
        }

    }

    private class SetsWatcher implements TextWatcher{

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            updateNumSets();
            updateWorkoutLength();
        }
    }

    private class RepsWatcher implements TextWatcher{
        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            updateNumReps();
            updateWorkoutLength();


        }
    }

    private class PrepWatcher implements TextWatcher {

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            updatePrep();
            updateWorkoutLength();

            if (prepTime == 0) {
                currSegment = WORK; // no prep time

                timerSegView.setText(WORK);
                timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
                timeLeftInSegView.setText(millisToTimeString(workTime));
                timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.work_color));
            } else {
                currSegment = PREP; // prep time

                timerSegView.setText(PREP);
                timerSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
                timeLeftInSegView.setText(millisToTimeString(prepTime));
                timeLeftInSegView.setTextColor(ContextCompat.getColor(getContext(), R.color.prep_color));
            }

        }
    }

    private class WorkWatcher implements TextWatcher {

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            updateWork();
            updateWorkoutLength();

            // If our prep time is zero, then we display the work time
            // Otherwise view doesn't change
            if (prepTime==0){
                timeLeftInSegView.setText(millisToTimeString(workTime));
            }

        }
    }

    private class RestWatcher implements TextWatcher {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            updateRest();
            updateWorkoutLength();

        }
    }

    private class BreakWatcher implements TextWatcher{

        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {

            updateBreak();
            updateWorkoutLength();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        layout = inflater.inflate(R.layout.fragment_timer_layout, container, false);

        // load presets
        gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            // Load presets from stored file
            // If we havent stored presets yet, get initial file from assets
            jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), PRESET_FILE);

            if (jsonFile.exists()){

                jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));

                // UNCOMMENT ONLY IF YOU WANT TO REFRESH EXTERNAL JSON FILE
                //jsonFile.delete();
                // jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(PRESET_FILE)));
            }
            else{
                jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(PRESET_FILE)));
            }

            // Load presets via GSON here
            presets = gson.fromJson(jsonReader, AllTimerPresets.class);
            if (presets == null){
                // Generate default
                presets = new AllTimerPresets();
                presets.addPreset(new TimerPreset(TEST));
            }
            else {
                timerPreset = presets.getPreset(0); // Set initial view preset as the first in the list
            }

            // close reader
            jsonReader.close();
        }
        catch(Exception e){e.printStackTrace();}

        setInitialView();

        return layout;

    }


    // Once we're attached to an activity, we can use getActivity() for sure
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPause() {

        // pause mode
        // pause the timer

        timerRunning = false;
        timerState = TIMER_PAUSED;
        timerHandler.removeCallbacks(riseTimer);
        (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
            @Override
            public void run() {
                setPausedView();
            }
        }, RUNNING_CHECK_INTERVAL);

        if (!(mediaPlayer == null) && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayerPaused = true;
        }


        super.onPause();

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onStop(){
        super.onStop();
        // End timer
        timerRunning = false;

    }

    // Create layout for running mode
    private void setRunningView(){

        // If we're coming from an inactive view, we need to change a bunch of fields
        if (timerState.equals(TIMER_INACTIVE)){

            // We want to make the save preset button non-clickable now
            savePresetButton.setClickable(false);

            // We only want to do the below once
            if (!editTextNonClickable) {

                setsTextDisplay = (TextView) row3Grid1.getChildAt(1);
                setsTextDisplay.setText(SET);

                repsTextDisplay = (TextView) row3Grid2.getChildAt(1);
                repsTextDisplay.setText(REP);

                // set gravity of row3grid1 to bottom
                row3Grid1.setGravity(Gravity.BOTTOM);
                row3Grid2.setGravity(Gravity.BOTTOM);

                // Disable editing of text when we're running
                workTimeMins.setInputType(InputType.TYPE_NULL);
                workTimeMins.setFocusable(false);
                workTimeMins.setClickable(false);

                workTimeSecsOnes.setInputType(InputType.TYPE_NULL);
                workTimeSecsOnes.setFocusable(false);
                workTimeSecsOnes.setClickable(false);

                workTimeSecsTens.setInputType(InputType.TYPE_NULL);
                workTimeSecsTens.setFocusable(false);
                workTimeSecsTens.setClickable(false);

                restTimeMins.setInputType(InputType.TYPE_NULL);
                restTimeMins.setFocusable(false);
                restTimeMins.setClickable(false);

                restTimeSecsOnes.setInputType(InputType.TYPE_NULL);
                restTimeSecsOnes.setFocusable(false);
                restTimeSecsOnes.setClickable(false);

                restTimeSecsTens.setInputType(InputType.TYPE_NULL);
                restTimeSecsTens.setFocusable(false);
                restTimeSecsTens.setClickable(false);

                prepTimeMins.setInputType(InputType.TYPE_NULL);
                prepTimeMins.setFocusable(false);
                prepTimeMins.setClickable(false);

                prepTimeSecsOnes.setInputType(InputType.TYPE_NULL);
                prepTimeSecsOnes.setFocusable(false);
                prepTimeSecsOnes.setClickable(false);

                prepTimeSecsTens.setInputType(InputType.TYPE_NULL);
                prepTimeSecsTens.setFocusable(false);
                prepTimeSecsTens.setClickable(false);

                breakTimeMins.setInputType(InputType.TYPE_NULL);
                breakTimeMins.setFocusable(false);
                breakTimeMins.setClickable(false);

                breakTimeSecsOnes.setInputType(InputType.TYPE_NULL);
                breakTimeSecsOnes.setFocusable(false);
                breakTimeSecsOnes.setClickable(false);

                breakTimeSecsTens.setInputType(InputType.TYPE_NULL);
                breakTimeSecsTens.setFocusable(false);
                breakTimeSecsTens.setClickable(false);

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

            // We now need to turn the "Load Preset" button into a stop button
            row2Grid2.removeView(loadPresetButton);
            stopButton = new ImageButton(getActivity());
            stopButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            row2Grid2.addView(stopButton);
            stopButton.setImageResource(R.drawable.ic_stop_black_24dp);
            stopButton.setBackgroundResource(R.drawable.rect_border2);

            // Change start button icon into pause button
            if (pauseButton != null && pauseButton.getParent()!=null) {
                ((ViewGroup) pauseButton.getParent()).removeView(pauseButton);
            }
            if (playButton != null && playButton.getParent()!=null) {
                ((ViewGroup) playButton.getParent()).removeView(playButton);
            }
            pauseButton = new ImageButton(getActivity());
            pauseButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            row2Grid1.addView(pauseButton);
            pauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
            pauseButton.setBackgroundResource(R.drawable.rect_border2);

            // Add an OnClickListener for the pause button
            pauseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    // pause the timer
                    timerRunning = false;
                    timerState = TIMER_PAUSED;
                    if (!(mediaPlayer == null) && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        mediaPlayerPaused = true;
                    }
                    timerHandler.removeCallbacks(riseTimer);
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            setPausedView();
                        }
                    },RUNNING_CHECK_INTERVAL);
                }
            });


            // Create dialog fragment
            stopFragment = StopTimerDialogFragment.newInstance();
            stopFragment.setTargetFragment(this, REQUEST_CODE);

            // Make "Save Preset" unclickable while we're running
            savePresetButton.setEnabled(false);
            savePresetButton.setClickable(false);
            savePresetButton.setBackgroundResource(R.drawable.rect_border5);
            savePresetButton.setTypeface(null, Typeface.BOLD_ITALIC);

            // Add an OnClickListener for the stop button
            stopButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    // Stop the timer
                    timerRunning = false;
                    timerState = TIMER_PAUSED;
                    timerHandler.removeCallbacks(riseTimer);

                    // pause view
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            setPausedView();
                        }
                    },RUNNING_CHECK_INTERVAL);

                    ft = getActivity().getSupportFragmentManager().beginTransaction();

                    // Show stopFragment
                    stopFragment.show(ft, STOP_DIALOG);

                }
            });

        }

        // If we're coming from paused mode, we just need to change the play logo back to pause
        if (timerState.equals(TIMER_PAUSED)){

            // Disable saving the preset since we're playing
            savePresetButton.setEnabled(false);
            savePresetButton.setClickable(false);
            savePresetButton.setBackgroundResource(R.drawable.rect_border5);
            savePresetButton.setTypeface(null, Typeface.BOLD_ITALIC);

            // Change play logo to pause
            if (pauseButton != null && pauseButton.getParent()!=null) {
                ((ViewGroup) pauseButton.getParent()).removeView(pauseButton);
            }
            if (playButton != null && playButton.getParent()!=null) {
                ((ViewGroup) playButton.getParent()).removeView(playButton);
            }
            row2Grid1.addView(pauseButton);
            pauseButton.setBackgroundResource(R.drawable.rect_border2);

            pauseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    // pause the timer
                    timerRunning = false;
                    timerState = TIMER_PAUSED;
                    if (!(mediaPlayer == null) && mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        mediaPlayerPaused = true;
                    }
                    timerHandler.removeCallbacks(riseTimer);
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            setPausedView();
                        }
                    },RUNNING_CHECK_INTERVAL);
                }
            });


        }
    }

    // Create layout for paused mode
    // Basically, the pause button becomes a play button and we can now save presets again
    // We still can't load until we stop the workout
    private void setPausedView(){
        // Enable saving the preset again since we're paused
        savePresetButton.setEnabled(true);
        savePresetButton.setClickable(true);
        savePresetButton.setBackgroundResource(R.drawable.rect_border2);
        savePresetButton.setTypeface(null, Typeface.NORMAL);

        // Change pause logo to play
        if (pauseButton != null && pauseButton.getParent()!=null) {
            ((ViewGroup) pauseButton.getParent()).removeView(pauseButton);
        }
        if (playButton != null && playButton.getParent()!=null) {
            ((ViewGroup) playButton.getParent()).removeView(playButton);
        }
        row2Grid1.addView(playButton);
        playButton.setBackgroundResource(R.drawable.rect_border2);
        // Start timer when we click play
        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // Set timer state to running
                startTimer();
            }
        });
    }


    // Create buttons and set initial/inactive view
    private void setInitialView(){
        // Get the parent layout
        LinearLayout parent = (LinearLayout)layout.findViewById(R.id.timer_layout_full);

        // All rows
        LinearLayout row1 = (LinearLayout)parent.findViewById(R.id.row1);
        LinearLayout row2 = (LinearLayout)parent.findViewById(R.id.row2);
        LinearLayout row3 = (LinearLayout)parent.findViewById(R.id.row3);
        LinearLayout row4 = (LinearLayout)parent.findViewById(R.id.row4);

        // All grid entries
        row1Grid1 = (LinearLayout)row1.findViewById(R.id.row1_grid1);
        row1Grid2 = (LinearLayout)row1.findViewById(R.id.row1_grid2);
        row1Grid3 = (LinearLayout)row1.findViewById(R.id.row1_grid3);

        row2Grid1 = (LinearLayout)row2.findViewById(R.id.row2_grid1);
        row2Grid2 = (LinearLayout)row2.findViewById(R.id.row2_grid2);
        row2Grid3 = (LinearLayout)row2.findViewById(R.id.row2_grid3);

        row3Grid1 = (LinearLayout)row3.findViewById(R.id.row3_grid1);
        row3Grid2 = (LinearLayout)row3.findViewById(R.id.row3_grid2);
        row3Grid3 = (LinearLayout)row3.findViewById(R.id.row3_grid3);

        row4Grid1 = (LinearLayout)row4.findViewById(R.id.row4_grid1);
        row4Grid2 = (LinearLayout)row4.findViewById(R.id.row4_grid2);
        row4Grid3 = (LinearLayout)row4.findViewById(R.id.row4_grid3);

        timeLeftInSegView = (TextView) row1Grid1.getChildAt(0);
        timeElapsedView = (TextView) row1Grid2.getChildAt(0);
        timerSegView = (TextView) row1Grid3.getChildAt(0);

        // Specific vals
        editSets = (EditText)row3Grid1.getChildAt(0);
        editReps = (EditText)row3Grid2.getChildAt(0);

        editSets.addTextChangedListener(new SetsWatcher());
        editReps.addTextChangedListener(new RepsWatcher());

        workTimeBox = (LinearLayout) row3Grid3.getChildAt(0);
        restTimeBox = (LinearLayout) row4Grid1.getChildAt(0);
        prepTimeBox = (LinearLayout) row4Grid2.getChildAt(0);
        breakTimeBox = (LinearLayout) row4Grid3.getChildAt(0);

        // We can get our EditText for work, rest, prep, and break times
        workTimeMins = (EditText) workTimeBox.getChildAt(0);
        workTimeSecsTens = (EditText) workTimeBox.getChildAt(2);
        workTimeSecsOnes = (EditText) workTimeBox.getChildAt(3);
        updateWork();
        workTimeMins.addTextChangedListener(new WorkWatcher());
        workTimeSecsTens.addTextChangedListener(new WorkWatcher());
        workTimeSecsOnes.addTextChangedListener(new WorkWatcher());

        restTimeMins = (EditText) restTimeBox.getChildAt(0);
        restTimeSecsTens = (EditText) restTimeBox.getChildAt(2);
        restTimeSecsOnes = (EditText) restTimeBox.getChildAt(3);
        updateRest();
        restTimeMins.addTextChangedListener(new RestWatcher());
        restTimeSecsTens.addTextChangedListener(new RestWatcher());
        restTimeSecsOnes.addTextChangedListener(new RestWatcher());

        prepTimeMins = (EditText) prepTimeBox.getChildAt(0);
        prepTimeSecsTens = (EditText) prepTimeBox.getChildAt(2);
        prepTimeSecsOnes = (EditText) prepTimeBox.getChildAt(3);
        updatePrep();
        prepTimeMins.addTextChangedListener(new PrepWatcher());
        prepTimeSecsTens.addTextChangedListener(new PrepWatcher());
        prepTimeSecsOnes.addTextChangedListener(new PrepWatcher());

        breakTimeMins = (EditText) breakTimeBox.getChildAt(0);
        breakTimeSecsTens = (EditText) breakTimeBox.getChildAt(2);
        breakTimeSecsOnes = (EditText) breakTimeBox.getChildAt(3);
        updateBreak();
        breakTimeMins.addTextChangedListener(new BreakWatcher());
        breakTimeSecsTens.addTextChangedListener(new BreakWatcher());
        breakTimeSecsOnes.addTextChangedListener(new BreakWatcher());

        // update number of sets and number of reps
        updateNumSets();
        updateNumReps();

        // Finally set workout length based on above information
        updateWorkoutLength();

        // we'll use these later
        setsTextView = new TextView(getActivity());
        repsTextView = new TextView(getActivity());

        // set properties
        setsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,SETS_TEXT_SIZE);
        repsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,REPS_TEXT_SIZE);
        setsTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        repsTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // Set initial time in segment as prepTime
        timeInSegment = prepTime;

        // Set segLowerBound initially
        segLowerBound = workoutLength - prepTime;

        // set firstRun to true
        firstRun = true;

        // Go through and assign appropriate listeners
        playButton = (ImageButton) row2Grid1.getChildAt(0);
        loadPresetButton = (Button) row2Grid2.getChildAt(0);
        savePresetButton = (Button) row2Grid3.getChildAt(0);

        // Start timer when we click play
        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startTimer();
            }
        });

        // save and load fragments

        loadPresetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                updateLoadFragment();

                if (presets.getNumPresets()>0) {

                    // We generates ListView from AllTimerPresets
                    ft = getActivity().getSupportFragmentManager().beginTransaction();

                    // Show stopFragment
                    loadPresetsFragment.show(ft, LOAD_DIALOG);
                }
                else{
                    Toast toast = Toast.makeText(getContext(), R.string.no_presets, Toast.LENGTH_SHORT);
                    toast.show();

                }


            }});

        savePresetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                updateSaveFragment();

                // Generate a dialog with a field to put in name
                // Once we have the name, we can store, because we already have all the other
                //
                // saveCurrentPreset(String name)

                // We generates ListView from AllTimerPresets
                ft = getActivity().getSupportFragmentManager().beginTransaction();

                // Show stopFragment
                savePresetsFragment.show(ft, SAVE_DIALOG);


            }});

        // Let's set view from preset if we have one
        if (presets.getNumPresets()>0) {
            (new Handler(Looper.myLooper())).postDelayed(new Runnable() {
                @Override
                public void run() {
                    insertPreset(presets.getPreset(0).getName());
                }
            }, RUNNING_CHECK_INTERVAL);
        }

    }

    @Override
    public void updateSaveFragment(){
        // Create save fragment
        if (!(presets == null)) {
            savePresetsFragment = SavePresetsDialogFragment.newInstance(presets.getNames());
            savePresetsFragment.setTargetFragment(this, REQUEST_CODE);
        }
    }

    @Override
    public void updateLoadFragment(){
        // Create load fragment
        if (!(presets == null) && presets.getNumPresets()>0) {
            loadPresetsFragment = LoadPresetsDialogFragment.newInstance(presets.getNames());
            loadPresetsFragment.setTargetFragment(this, REQUEST_CODE);
        }
    }


}