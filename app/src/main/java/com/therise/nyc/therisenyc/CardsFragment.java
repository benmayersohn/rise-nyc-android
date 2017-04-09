package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CardsFragment extends Fragment
    implements SavePresetsDialogFragment.UpdatePresets{

    // REFRESH FILES (discards saved exercises/presets for defaults)
    private static final boolean refreshFiles = false;

    private static final String PRESETS_FILE = "cards_presets.json";
    private static final String EXERCISES_FILE = "exercises.json";

    private Deck cardsRemaining; // cards remaining in deck (starts with 52 or 54 cards)
    private Deck cardsDealt; // cards that have been dealt (starts empty)

    private int numCards; // total number of cards

    private DialogFragment loadPresetsFragment;
    private DialogFragment savePresetsFragment;
    private DialogFragment stopFragment;
    private DialogFragment endFragment;

    private static final String BLANK = "";

    // RESULT_CODES
    private static final int PAUSE_CODE = 0;
    private static final int STOP_CODE = 1;
    private static final int LOAD_CODE = 2;
    private static final int SAVE_CODE = 3;

    private static final int MAX_LENGTH = 17;

    private static final String LOAD_DIALOG = "LOAD_DIALOG";
    private static final String SAVE_DIALOG = "SAVE_DIALOG";
    private static final String STOP_DIALOG = "STOP_DIALOG";
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
    private static final String JOKER = "JOKER";

    private String timerState = TIMER_INACTIVE;

    // delimiters
    private static final String SPACE_DELIM = " ";
    private static final String SLASH = "/";

    // Time running
    private TextView timeElapsedView;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private SimpleTimer simpleTimer = new SimpleTimer();

    // FragmentTransaction for loading dialogs
    private FragmentTransaction ft;

    // Index of current suit
    private int suitIndex;

    private ImageView leftArrow;
    private ImageView rightArrow;

    // Preset name to save
    private String presetName;

    // Exercise name to save
    private String exerciseName;

    private LinearLayout buttonRow;

    private volatile boolean timerRunning;
    private volatile boolean runningViewSet;
    private volatile boolean firstRun = true;

    // We start on first card
    private volatile boolean firstCard = true;
    private volatile boolean lastCard = false;

    private static final int ONE_SECOND = 1000; // one second in milliseconds
    private static final int RUNNING_CHECK_INTERVAL = 50; // MUST be factor of 1000

    private long elapsedTime;

    private static final String WORKOUT_TYPE = "CARDS";

    // request code for dialog in reference to to this fragment
    private static final int REQUEST_CODE = 3;

    // Buttons
    private CheckBox jokerBox; // do we want jokers enabled?

    private TextView cardExercise; // current exercise being shown
    private TextView cardNumber; // current exercise being shown

    private boolean hasJokers;

    private ImageButton playButton;
    private ImageButton playButtonDuring;
    private ImageButton pauseButton;
    private ImageButton stopButton;
    private Button savePresetButton;
    private Button loadPresetButton;

    // Where are we in the deck?
    private int currentPosition;

    // Views

    private View layoutInitial;
    private View layoutRunning;

    // Buttons and such on initial view
    private static final int numFields = 4;
    private static final int[] fieldIds = new int[]{R.id.hearts_exercise, R.id.clubs_exercise, R.id.diamonds_exercise, R.id.spades_exercise};


    private ImageView[] saveButtons;

    // Store fields so we can modify later
    private EditText[] fields;

    // Save buttons on initial view
    private static final int[] saveIds = new int[]{R.id.save_hearts,R.id.save_clubs,R.id.save_diamonds,R.id.save_spades};

    // For presets (load while setting up UI)
    private Gson gson;
    private NumberedPreset numberedPreset;
    private PresetHolder<NumberedPreset> presets;
    private JsonReader jsonReader;
    private FileWriter fileWriter;
    private File jsonFile;

    private static final int HEARTS_INDEX = 0;
    private static final int CLUBS_INDEX = 1;
    private static final int DIAMONDS_INDEX = 2;
    private static final int SPADES_INDEX = 3;

    // Exercises
    private Exercise exercise;
    private PresetHolder<Exercise> exercises;
    private String[] chosenExercises; // exercises chosen

    public CardsFragment() {};

    private String cardToExercise(Card c){
        if (c.isJoker()){
            return JOKER;
        }

        String suit = c.getSuit();
        String chosenExercise = "";
        int number = c.getNumber();

        String outputString;

        switch (suit){
            case Card.HEARTS:
                chosenExercise = chosenExercises[HEARTS_INDEX];
                break;
            case Card.CLUBS:
                chosenExercise = chosenExercises[CLUBS_INDEX];
                break;
            case Card.SPADES:
                chosenExercise = chosenExercises[SPADES_INDEX];
                break;
            case Card.DIAMONDS:
                chosenExercise = chosenExercises[DIAMONDS_INDEX];
                break;
        }

        outputString = String.valueOf(number + SPACE_DELIM + chosenExercise);

        return outputString;
    }

    private String cardNumberString(){
        return String.valueOf(currentPosition+1) + SLASH + String.valueOf(numCards);
    }

    // We get the name of the tab and the view from the activity
    public static CardsFragment newInstance() {
        CardsFragment fragment = new CardsFragment();
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

        }
    }

    private void setPausedView(){
        // Enable saving the preset again since we're paused

        // Disable left/right click
        leftArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_grey_24dp));
        rightArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_grey_24dp));

        leftArrow.setEnabled(false);
        rightArrow.setEnabled(false);

        // New play button on bottom row
        buttonRow  = (LinearLayout) layoutRunning.findViewById(R.id.row3);

        // Change pause logo to play
        if (pauseButton != null && pauseButton.getParent()!=null) {
            ((ViewGroup) pauseButton.getParent()).removeView(pauseButton);
        }
        if (playButtonDuring != null && playButtonDuring.getParent()!=null) {
            ((ViewGroup) playButtonDuring.getParent()).removeView(playButtonDuring);
        }
        playButtonDuring = new ImageButton(getActivity());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams stopParams = (LinearLayout.LayoutParams) stopButton.getLayoutParams();

        params.gravity = Gravity.CENTER_VERTICAL;
        params.rightMargin = stopParams.rightMargin;
        params.leftMargin = stopParams.leftMargin;
        playButtonDuring.setPadding(stopButton.getPaddingLeft(),
                stopButton.getPaddingTop(),stopButton.getPaddingRight(),stopButton.getPaddingBottom());

        playButtonDuring.setLayoutParams(params);
        playButtonDuring.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        playButtonDuring.setBackgroundResource(R.drawable.rect_border4);


        buttonRow.addView(playButtonDuring,0);

        // Start timer when we click play
        playButtonDuring.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                leftArrow.setEnabled(true);
                rightArrow.setEnabled(true);

                // Make buttons right color again
                // Change color of left/right arrows if necessary
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        leftArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_blue_24dp));
                        rightArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_blue_24dp));

                        if (currentPosition == 0){
                            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_grey_24dp);
                            leftArrow.setImageDrawable(d);
                            firstCard = true;
                        }

                        else if (currentPosition == numCards-1){
                            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_gold_24dp);
                            rightArrow.setImageDrawable(d);
                            lastCard = true;
                        }

                        else{
                            // If we're coming from first or last card, color arrows appropriately
                            if (firstCard){
                                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_blue_24dp);
                                leftArrow.setImageDrawable(d);

                                firstCard = false;
                            }

                            if (lastCard){
                                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_blue_24dp);
                                rightArrow.setImageDrawable(d);

                                lastCard = false;
                            }
                        }

                    }
                }, RUNNING_CHECK_INTERVAL);


                // Change play button back to pause button
                // Change pause logo to play
                if (playButtonDuring != null && playButtonDuring.getParent()!=null) {
                    ((ViewGroup) playButtonDuring.getParent()).removeView(playButtonDuring);
                }

                buttonRow.addView(pauseButton,0);

                // Set timer state to running
                startTimer();
            }
        });
    }

    private class SimpleTimerViewUpdate implements Runnable{
        @Override
        public void run(){

            if (!runningViewSet && timerRunning){

                layoutRunning.setBackgroundResource(cardsDealt.getCardAt(0).getId());

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
        if (resultCode == PAUSE_CODE){
            timerState = TIMER_PAUSED;

            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPausedView();
                }
            }, RUNNING_CHECK_INTERVAL);

        }
        else if (resultCode == STOP_CODE){
            timerState = TIMER_INACTIVE;

            // delete layoutRunning
            layoutRunning = null;

            // set content view to initial
            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                @Override
                public void run() {

                    layoutInitial = getActivity().getLayoutInflater().inflate(R.layout.fragment_cards_initial, null, false);

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

                    suitIndex = data.getIntExtra(INDEX,0);

                    // refresh view
                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Check our position, update the corresponding EditText
                            fields[suitIndex].setText(exerciseName);
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

                // UNCOMMENT ONLY IF YOU WANT TO REFRESH EXTERNAL JSON FILE
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
        layoutInitial = inflater.inflate(R.layout.fragment_cards_initial, container, false);

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

                    // Show stopFragment
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

            // Make sure exercises up to date
            setupJson();

            updateSaveFragment(EXERCISE,String.valueOf(fields[(Integer)view.getTag()].getText()));

            // Generate a dialog with a field to put in name
            // Once we have the name, we can store, because we already have all the other

            // saveCurrentPreset(String name)

            ft = getActivity().getSupportFragmentManager().beginTransaction();

            // Show stopFragment
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

                // Reload JSON
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

                // Reload JSON
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

        // Set on click listener for checkbox
        jokerBox = (CheckBox)layoutInitial.findViewById(R.id.has_jokers);
        hasJokers = jokerBox.isChecked();

        jokerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                hasJokers = isChecked;
            }

        });

        // Set on click listener on play button

        playButton = (ImageButton) layoutInitial.findViewById(R.id.play_button_image);

        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                // Running layout; store the view for when we run
                layoutRunning = getActivity().getLayoutInflater().inflate(R.layout.fragment_cards_during, null, false);

                chosenExercises = new String[numFields];

                // Now we solidify our chosen exercises
                for (int i = 0; i < numFields; i++){
                    chosenExercises[i] = String.valueOf(fields[i].getText());
                }

                // Create deck of remaining cards
                cardsRemaining = new Deck(hasJokers);

                // create deck of cards drawn
                cardsDealt = new Deck(); // empty deck

                // set number of cards in stone
                numCards = cardsRemaining.getNumCards();

                // Draw a card
                drawCard();

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
    private Card drawCard(){
        // Pick a random number between 0 and numCardsRemaining-1
        int numCardsRemaining = cardsRemaining.getNumCards();
        Random r = new Random();
        int nextInd = r.nextInt(numCardsRemaining);
        Card drawnCard = cardsRemaining.getCardAt(nextInd);

        // Add drawn card to drawn deck
        cardsDealt.addCard(drawnCard);

        // Remove from other deck
        cardsRemaining.removeCard(drawnCard);

        return drawnCard;
    }

    // Runnable class for updating the UI, run on the main thread
    private class UpdateScreen implements Runnable{
        // Update screen for new card

        Card c;

        UpdateScreen(Card c){
            this.c = c;
        }

        @Override
        public void run(){
            layoutRunning.setBackgroundResource(c.getId());

            // Set exercise display
            cardExercise.setText(cardToExercise(c));

            // Set card number
            cardNumber.setText(cardNumberString());

            // Change color of left/right arrows if necessary
            if (currentPosition == 0){
                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_grey_24dp);
                leftArrow.setImageDrawable(d);
                firstCard = true;
            }

            else if (currentPosition == numCards-1){
                Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_gold_24dp);
                rightArrow.setImageDrawable(d);
                lastCard = true;
            }

            else{
                // If we're coming from first or last card, color arrows appropriately
                if (firstCard){
                    Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_previous_blue_24dp);
                    leftArrow.setImageDrawable(d);

                    firstCard = false;
                }

                if (lastCard){
                    Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_skip_next_blue_24dp);
                    rightArrow.setImageDrawable(d);

                    lastCard = false;
                }
            }
        }

    }

    // Create layout for running mode, upon pressing the play button
    private void setRunningView(){

        // Lastly, show the view
        getActivity().setContentView(layoutRunning);

        // Remove references to layoutInitial
        layoutInitial = null;

        // Now set up the buttons, on-click listeners, etc.
        timeElapsedView = (TextView) layoutRunning.findViewById(R.id.card_time_elapsed);
        cardExercise = (TextView) layoutRunning.findViewById(R.id.card_exercise);
        cardNumber = (TextView) layoutRunning.findViewById(R.id.current_card);

        leftArrow = (ImageView) layoutRunning.findViewById(R.id.left_arrow);
        rightArrow = (ImageView) layoutRunning.findViewById(R.id.right_arrow);

        // pause button
        pauseButton = (ImageButton) layoutRunning.findViewById(R.id.pause_button);

        // Set onclick listener for pause button
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // pause the timer
                timerRunning = false;
                timerState = TIMER_PAUSED;

                timerHandler.removeCallbacks(simpleTimer);
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        setPausedView();
                    }
                },RUNNING_CHECK_INTERVAL);
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


        // Set screen to current card
        (new Handler(Looper.getMainLooper())).postDelayed(new UpdateScreen(cardsDealt.getCardAt(0)),RUNNING_CHECK_INTERVAL);

        // Set on click listeners for left and right arrows

        leftArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // Cycle to previous card
                if (currentPosition != 0){
                    currentPosition--;
                    (new Handler(Looper.getMainLooper()))
                            .postDelayed(new UpdateScreen(cardsDealt.getCardAt(currentPosition)),RUNNING_CHECK_INTERVAL);
                }

            }

        });

        rightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // We want to draw another card, UNLESS our current position is somewhere in the
                // middle of the cards we have drawn (i.e. we've went back), or we're done

                // We're done
                if (currentPosition == numCards-1){
                    // Display EndTimerDialogFragment

                    timerRunning = false;
                    timerState = TIMER_INACTIVE;

                    ft = getActivity().getSupportFragmentManager().beginTransaction();

                    endFragment.show(ft, END_DIALOG);
                }

                // New card
                else if (currentPosition == cardsDealt.getNumCards()-1){
                    currentPosition++;
                    (new Handler(Looper.getMainLooper())).postDelayed(new UpdateScreen(drawCard()),RUNNING_CHECK_INTERVAL);
                }

                // Otherwise, cycle to next card (which has already been drawn)
                else{
                    currentPosition++;
                    (new Handler(Looper.getMainLooper()))
                            .postDelayed(new UpdateScreen(cardsDealt.getCardAt(currentPosition)),RUNNING_CHECK_INTERVAL);
                }

            }

        });

        // Set stop dialog fragment on stop button
        // Create dialog fragment
        stopFragment = StopDialogFragment.newInstance(WORKOUT_TYPE);
        stopFragment.setTargetFragment(this, REQUEST_CODE);

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
