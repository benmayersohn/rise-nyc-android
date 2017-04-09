package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by mayerzine on 1/14/17.
 *
 * Deck of Cards fragment.
 * Choose random cards from a deck of 54 (including jokers) or 52 (excluding).
 * We draw a random index, sort cards in an ArrayList (or deck, getCardAt(int position))
 * Option of assigning exercises to each suit.
 *
 * We can create a "drawn" pile and a "future" pile
 * Upon beginning, put first card into drawn pile and remove from future pile. We are at position 0.
 * Flipping forward puts next card in drawn pile. We are at position one.
 * When flipping backwards, go to corresponding position within drawn pile and use getCardAt()
 * Use a ViewFlipper to transition between card displays
 */

public class DiceFragment extends Fragment
        implements SavePresetsDialogFragment.UpdatePresets{

    // REFRESH FILES (discards saved exercises/presets for defaults)
    private static final boolean refreshFiles = false;

    private static final String PRESETS_FILE = "dice_presets.json";
    private static final String EXERCISES_FILE = "exercises.json";

    private DialogFragment loadPresetsFragment;
    private DialogFragment savePresetsFragment;
    private DialogFragment endFragment;

    private static final String BLANK = "";
    private MediaPlayer mediaPlayer;

    private static final int MAX_LENGTH = 17;

    // RESULT_CODES
    private static final int STOP_CODE = 1;
    private static final int LOAD_CODE = 2;
    private static final int SAVE_CODE = 3;

    private static final int NUM_ANIMATION_CYCLES = 12;
    private static final int DICE_ANIMATE_INTERVAL = 50;

    private static final String LOAD_DIALOG = "LOAD_DIALOG";
    private static final String SAVE_DIALOG = "SAVE_DIALOG";
    private static final String END_DIALOG = "END_DIALOG";
    private static final String PRESET = "PRESET";
    private static final String EXERCISE = "EXERCISE";
    private static final String PRESET_TYPE = "PRESET_TYPE";

    private static final String TIMER_INACTIVE = "TIMER_INACTIVE";
    private static final String TIMER_PAUSED = "TIMER_PAUSED";

    private static final String SELECTED_ENTRY = "SELECTED_ENTRY";
    private static final String DELETE_ENTRY = "DELETE_ENTRY";
    private static final String CHOSEN_NAME = "CHOSEN_NAME";

    private static final String INDEX = "INDEX";

    private String timerState = TIMER_INACTIVE;

    // Time running
    private TextView timeElapsedView;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private SimpleTimer simpleTimer = new SimpleTimer();

    private ImageView dieImage;

    // FragmentTransaction for loading dialogs
    private FragmentTransaction ft;

    // Index of current suit
    private int exerciseIndex;

    private ImageView rightArrow;

    // Preset name to save
    private String presetName;

    // Exercise name to save
    private String exerciseName;

    private AnimationDrawable frameAnimation;

    private volatile boolean timerRunning;
    private volatile boolean runningViewSet;
    private static final int ONE_SECOND = 1000; // one second in milliseconds
    private static final int RUNNING_CHECK_INTERVAL = 50; // MUST be factor of 1000

    private long elapsedTime;

    private static final String WORKOUT_TYPE = "DICE";

    // request code for dialog in reference to to this fragment
    private static final int REQUEST_CODE = 2;

    // Buttons

    private TextView diceExercise; // current exercise being shown
    private TextView rollNumber; // how many rolls we've thrown

    private ImageButton playButton;
    private ImageButton stopButton;
    private Button savePresetButton;
    private Button loadPresetButton;

    // Where are we in the deck?
    private int currentPosition;

    // Views

    private View layoutInitial;
    private View layoutRunning;

    // Buttons and such on initial view
    private static final int numFields = 6;
    private static final int[] fieldIds = new int[]{
            R.id.exercise1, R.id.exercise2, R.id.exercise3, R.id.exercise4, R.id.exercise5, R.id.exercise6};

    private static final int[] dieImages = new int[]{
            R.drawable.dice_one,R.drawable.dice_two,R.drawable.dice_three,R.drawable.dice_four,
            R.drawable.dice_five,R.drawable.dice_six};


    private ImageView[] saveButtons;

    // Store fields so we can modify later
    private EditText[] fields;

    // Save buttons on initial view
    private static final int[] saveIds =
            new int[]{R.id.save_1,R.id.save_2,R.id.save_3,R.id.save_4,R.id.save_5,R.id.save_6};

    // For presets (load while setting up UI)
    private Gson gson;
    private NumberedPreset numberedPreset;
    private PresetHolder<NumberedPreset> presets;
    private JsonReader jsonReader;
    private FileWriter fileWriter;
    private File jsonFile;

    // Exercises
    private Exercise exercise;
    private PresetHolder<Exercise> exercises;
    private String[] chosenExercises; // exercises chosen

    // roll of the die
    private int dieRoll;

    public DiceFragment() {};

    private String diceNumberString(){
        return "Roll: " + String.valueOf(currentPosition+1);
    }

    // We get the name of the tab and the view from the activity
    public static DiceFragment newInstance() {
        DiceFragment fragment = new DiceFragment();
        return fragment;
    }

    // We want to create a simple timer object that just posts the time elapsed
    // We should be able to re-use this code for the Dice Fragment as well
    private class SimpleTimer implements Runnable{
        @Override
        public void run(){

            if (!runningViewSet){
                (new Handler(Looper.getMainLooper())).postDelayed(new SimpleTimerViewUpdate(),RUNNING_CHECK_INTERVAL);
            }

            // Print the amount of time that has elapsed
            elapsedTime += RUNNING_CHECK_INTERVAL;

            if (elapsedTime % ONE_SECOND == 0) {
                (new Handler(Looper.getMainLooper())).postDelayed(new SimpleTimerViewUpdate(),RUNNING_CHECK_INTERVAL);
            }

            // Lastly, here's the key! Run a new handler to make this loop
            // To stop timer, we can simply call timerHandler.removeCallbacks(riseTimer)
            timerHandler.postDelayed(this,RUNNING_CHECK_INTERVAL);

            // If our media player isn't playing, set to null
            if (mediaPlayer != null && !(mediaPlayer.isPlaying())) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }
    }

    private class SimpleTimerViewUpdate implements Runnable{
        @Override
        public void run(){

            if (!runningViewSet && timerRunning){

                setRunningView();

                runningViewSet = true;
            }

            timeElapsedView.setText(TimeTools.millisToTimeString(elapsedTime));

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    // PAUSE_CODE, STOP_CODE, SAVE_CODE, LOAD_CODE
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == STOP_CODE){
            timerState = TIMER_INACTIVE;

            // delete layoutRunning
            layoutRunning = null;

            // set content view to initial
            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                @Override
                public void run() {

                    layoutInitial = getActivity().getLayoutInflater().inflate(R.layout.fragment_dice_initial, null, false);

                    setInitialView();

                    // Set content view to initial layout, set the properties of the view
                    getActivity().setContentView(layoutInitial);
                }
            }, RUNNING_CHECK_INTERVAL);


        }

        // Load presets
        else if (resultCode == LOAD_CODE) {

            int position = data.getIntExtra(SELECTED_ENTRY, 0);

            String presetType = data.getStringExtra(PRESET_TYPE);

            // If we're loading a preset
            if (presetType.equals(PRESET)) {
                presetName = presets.getPreset(position).getName();
                boolean deleteEntry = data.getBooleanExtra(DELETE_ENTRY, false);

                if (deleteEntry) {
                    jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), PRESETS_FILE);
                    JsonTools.removeFromJson(presets, presets.getPreset(position), jsonFile, gson);
                } else {

                    // refresh view
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            insertPreset(presetName);
                        }
                    }, RUNNING_CHECK_INTERVAL);
                }
            }

            // If we're loading an exercise
            if (presetType.equals(EXERCISE)){

                exerciseName = exercises.getPreset(position).getName();
                boolean deleteEntry = data.getBooleanExtra(DELETE_ENTRY, false);

                if (deleteEntry) {
                    jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), EXERCISES_FILE);
                    JsonTools.removeFromJson(exercises, exercises.getPreset(position), jsonFile, gson);
                } else {

                    exerciseIndex = data.getIntExtra(INDEX,0);

                    // refresh view
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Check our position, update the corresponding EditText
                            fields[exerciseIndex].setText(exerciseName);
                        }
                    }, RUNNING_CHECK_INTERVAL);
                }
            }
        }

        // Save presets
        else if (resultCode == SAVE_CODE){
            String presetType = data.getStringExtra(PRESET_TYPE);

            if (presetType.equals(PRESET)) {
                presetName = data.getStringExtra(CHOSEN_NAME);

                // Write preset
                jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), PRESETS_FILE);
                JsonTools.writeToJson(presets, generateCurrentPreset(presetName), jsonFile, gson);
            }

            if (presetType.equals(EXERCISE)) {
                exerciseName = data.getStringExtra(CHOSEN_NAME);

                // Write preset
                jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), EXERCISES_FILE);
                JsonTools.writeToJson(exercises, generateCurrentExercise(exerciseName), jsonFile, gson);
            }
        }


    }

    public NumberedPreset generateCurrentPreset(String name){
        List<String> exerciseNames = new ArrayList<>();

        for (int i = 0; i < numFields; i++) {
            exerciseNames.add(fields[i].getText().toString());
        }

        return new NumberedPreset(name,exerciseNames);
    }

    public Exercise generateCurrentExercise(String name){
        return new Exercise(name);
    }

    public void insertPreset(String name){
        if (presets != null) {
            numberedPreset = presets.getPreset(name);

            for (int i = 0; i < numFields; i++) {
                fields[i].setText(numberedPreset.getExercise(i));

            }
        }
    }

    @Override
    // Use this to keep activity in place after rotation etc.
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Keep around after screen rotation
        setRetainInstance(true);
    }

    private void setupJson(){
        // load presets
        gson = new GsonBuilder().setPrettyPrinting().create();
        try {

            // Load presets from stored file
            // If we havent stored presets yet, get initial file from assets
            jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), PRESETS_FILE);

            if (jsonFile.exists()){

                jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));

                if (refreshFiles) {
                    jsonFile.delete();
                    jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(PRESETS_FILE)));
                }
            }
            else{
                jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(PRESETS_FILE)));
            }

            // Load presets via GSON here
            Type collectionType = new TypeToken<PresetHolder<NumberedPreset>>(){}.getType();
            presets = gson.fromJson(jsonReader, collectionType);
            if (presets == null){
                // Generate default
                presets = new PresetHolder<>();
                presets.addPreset(new NumberedPreset(numFields));
            }
            else {
                numberedPreset = presets.getPreset(0); // Set initial view preset as the first in the list
            }

            // close reader
            jsonReader.close();
        }
        catch(Exception e){e.printStackTrace();}

        // Load exercises
        try {

            // Load presets from stored file
            // If we havent stored presets yet, get initial file from assets
            jsonFile = new File(getActivity().getExternalFilesDir(null).getPath(), EXERCISES_FILE);

            if (jsonFile.exists()){

                jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));

                if (refreshFiles) {
                    jsonFile.delete();
                    jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(EXERCISES_FILE)));
                }
            }
            else{
                jsonReader = new JsonReader(new InputStreamReader(getActivity().getAssets().open(EXERCISES_FILE)));
            }

            // Load exercises via GSON here
            Type collectionType = new TypeToken<PresetHolder<Exercise>>(){}.getType();
            exercises = gson.fromJson(jsonReader, collectionType);
            if (presets == null){
                // Generate default
                exercises = new PresetHolder<>();
                exercises.addPreset(new Exercise());
            }
            else {
                exercise = exercises.getPreset(0); // Set initial view preset as the first in the list
            }

            // close reader
            jsonReader.close();
        }
        catch(Exception e){e.printStackTrace();}

        // lastly sort
        exercises.sort();
        presets.sort();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        // Initial view; we will return this
        layoutInitial = inflater.inflate(R.layout.fragment_dice_initial, container, false);

        setupJson();

        setInitialView();

        return layoutInitial;

    }

    private class ExerciseListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View view){

            setupJson();

            // We want to load the list of exercises
            // We also want to pass the index of the exercise
            updateLoadFragment(EXERCISE, (Integer)view.getTag());

            if (presets.getNumPresets()>0) {

                // We generates ListView from PresetHolder<TimerPreset>
                ft = getActivity().getSupportFragmentManager().beginTransaction();

                loadPresetsFragment.show(ft, LOAD_DIALOG);
            }
            else{
                Toast toast = Toast.makeText(getContext(), R.string.no_exercises, Toast.LENGTH_SHORT);
                toast.show();

            }

            // Don't call on click listener
            return true;
        }
    }

    private class SaveExerciseListener implements View.OnClickListener{
        @Override
        public void onClick(View view){

            setupJson();

            updateSaveFragment(EXERCISE,String.valueOf(fields[(Integer)view.getTag()].getText()));

            // Generate a dialog with a field to put in name
            // Once we have the name, we can store, because we already have all the other

            // saveCurrentPreset(String name)

            ft = getActivity().getSupportFragmentManager().beginTransaction();

            savePresetsFragment.show(ft, SAVE_DIALOG);


        }
    }

    // This is the initial view, before we press start
    private void setInitialView(){

        // not running
        runningViewSet = false;
        elapsedTime = 0;
        timerRunning = false;
        currentPosition = 0;

        // We want to populate the EditText fields with the first preset, as a default.

        fields = new EditText[numFields];
        saveButtons = new ImageView[numFields];

        for (int i = 0; i < numFields; i++){
            fields[i] = (EditText) layoutInitial.findViewById(fieldIds[i]);
            fields[i].setText(presets.getPreset(0).getExercise(i));

            // Set a tag corresponding to the indices
            fields[i].setTag(i);

            // Set long click listener on text field
            fields[i].setOnLongClickListener(new ExerciseListener());

            // Set on click listener for save buttons
            saveButtons[i] = (ImageView) layoutInitial.findViewById(saveIds[i]);
            saveButtons[i].setTag(i);

            saveButtons[i].setOnClickListener(new SaveExerciseListener());

            // Force all caps when entering, and max length of characters
            fields[i].setFilters(new InputFilter[] {new InputFilter.AllCaps(),new InputFilter.LengthFilter(MAX_LENGTH)});
        }

        // We want to set a long click listener on the edit text fields, which should load the
        // LoadPresetsDialogFragment and display the presets

        loadPresetButton = (Button) layoutInitial.findViewById(R.id.load_presets_text);

        loadPresetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                setupJson();

                updateLoadFragment(PRESET,-1);

                if (presets.getNumPresets()>0) {

                    // We generates ListView from PresetHolder<TimerPreset>
                    ft = getActivity().getSupportFragmentManager().beginTransaction();

                    // Show stopFragment
                    loadPresetsFragment.show(ft, LOAD_DIALOG);
                }
                else{
                    Toast toast = Toast.makeText(getContext(), R.string.no_presets, Toast.LENGTH_SHORT);
                    toast.show();

                }

            }});

        savePresetButton = (Button) layoutInitial.findViewById(R.id.save_presets_text);

        savePresetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                setupJson();

                updateSaveFragment(PRESET,BLANK);

                // Generate a dialog with a field to put in name
                // Once we have the name, we can store, because we already have all the other
                //
                // saveCurrentPreset(String name)

                // We generates ListView from PresetHolder<TimerPreset>
                ft = getActivity().getSupportFragmentManager().beginTransaction();

                // Show stopFragment
                savePresetsFragment.show(ft, SAVE_DIALOG);


            }});

        // Set on click listener on play button

        playButton = (ImageButton) layoutInitial.findViewById(R.id.play_button_image);

        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                // Running layout; store the view for when we run
                layoutRunning = getActivity().getLayoutInflater().inflate(R.layout.fragment_dice_during, null, false);

                chosenExercises = new String[numFields];

                // Now we solidify our chosen exercises
                for (int i = 0; i < numFields; i++){
                    chosenExercises[i] = String.valueOf(fields[i].getText());
                }

                // Roll die
                dieRoll = rollDie();

                // Start the timer
                startTimer();
            }
        });

    }

    private void startTimer(){
        timerRunning = true;
        timerHandler.postDelayed(simpleTimer,RUNNING_CHECK_INTERVAL);
    }

    // Draw card from the deck
    private int rollDie(){
        // Pick a random number between 0 and 5
        Random r = new Random();
        return r.nextInt(numFields);
    }

    // Create layout for running mode, upon pressing the play button
    private void setRunningView(){

        // Lastly, show the view
        getActivity().setContentView(layoutRunning);

        dieImage = (ImageView) layoutRunning.findViewById(R.id.die_image);
        Drawable d = ContextCompat.getDrawable(getContext(), dieImages[dieRoll]);
        dieImage.setImageDrawable(d);

        // Remove references to layoutInitial
        layoutInitial = null;

        // Now set up the buttons, on-click listeners, etc.
        timeElapsedView = (TextView) layoutRunning.findViewById(R.id.dice_time_elapsed);
        diceExercise = (TextView) layoutRunning.findViewById(R.id.dice_exercise);
        rollNumber = (TextView) layoutRunning.findViewById(R.id.roll_number);

        diceExercise.setText(chosenExercises[dieRoll]);
        rollNumber.setText(diceNumberString());

        rightArrow = (ImageView) layoutRunning.findViewById(R.id.right_arrow);

        rightArrow.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  // roll the dice

                  currentPosition++;

                  // roll dice
                  if (mediaPlayer != null) {
                      mediaPlayer.release();
                      mediaPlayer = null;
                  }
                  mediaPlayer = MediaPlayer.create(getContext(), R.raw.dice_roll);
                  mediaPlayer.start();

                  // Set Play animation
                  (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                      @Override
                      public void run() {

                          // Create animator
                          dieImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dice_animation));

                          // Set to animation, set a delay, then show the die
                          frameAnimation = (AnimationDrawable) dieImage.getDrawable();

                          // Add images cycling through die
                          for (int i = 0; i < NUM_ANIMATION_CYCLES; i++){
                              frameAnimation.addFrame(ContextCompat.getDrawable(getContext(), dieImages[i % numFields]),DICE_ANIMATE_INTERVAL);
                          }

                          frameAnimation.start();
                      }
                  }, RUNNING_CHECK_INTERVAL);

                  // Then, update the rest
                  (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                      @Override
                      public void run() {

                          dieRoll = rollDie();

                          Drawable d = ContextCompat.getDrawable(getContext(), dieImages[dieRoll]);
                          dieImage.setImageDrawable(d);

                          diceExercise.setText(chosenExercises[dieRoll]);

                          // Set roll number
                          rollNumber.setText(diceNumberString());

                      }
                  }, DICE_ANIMATE_INTERVAL*NUM_ANIMATION_CYCLES);

              }
          });

        // stop button
        stopButton = (ImageButton) layoutRunning.findViewById(R.id.stop_button);

        // Set onclick listener for stopButton
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Stop the timer
                timerRunning = false;
                timerState = TIMER_PAUSED;

                timerHandler.removeCallbacks(simpleTimer);

                ft = getActivity().getSupportFragmentManager().beginTransaction();

                // Show stopFragment
                endFragment.show(ft, END_DIALOG);
            }
        });

        // Set end dialog fragment on stop button

        endFragment = EndTimerDialogFragment.newInstance(WORKOUT_TYPE);
        endFragment.setTargetFragment(this, REQUEST_CODE);
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

    // save and load fragments

    @Override
    public void updateSaveFragment(String presetType, String typedName){
        // Create save fragment

        if (presetType.equals(PRESET)) {
            if (!(presets == null)) {
                savePresetsFragment = SavePresetsDialogFragment.newInstance(presets.getNames(), WORKOUT_TYPE, PRESET, typedName);
                savePresetsFragment.setTargetFragment(this, REQUEST_CODE);
            }
        }

        if (presetType.equals(EXERCISE)) {
            if (!(exercises == null)) {
                savePresetsFragment = SavePresetsDialogFragment.newInstance(exercises.getNames(), WORKOUT_TYPE, EXERCISE, typedName);
                savePresetsFragment.setTargetFragment(this, REQUEST_CODE);
            }
        }
    }

    @Override
    public void updateLoadFragment(String presetType, int index){

        // Create load fragment

        if (presetType.equals(PRESET)) {
            if (!(presets == null) && presets.getNumPresets() > 0) {
                loadPresetsFragment = LoadPresetsDialogFragment.newInstance(presets.getNames(), PRESET, index);
                loadPresetsFragment.setTargetFragment(this, REQUEST_CODE);
            }
        }

        if (presetType.equals(EXERCISE)) {
            if (!(exercises == null) && exercises.getNumPresets() > 0) {
                loadPresetsFragment = LoadPresetsDialogFragment.newInstance(exercises.getNames(), EXERCISE, index);
                loadPresetsFragment.setTargetFragment(this, REQUEST_CODE);
            }
        }
    }

}
