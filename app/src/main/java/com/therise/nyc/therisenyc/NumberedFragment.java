package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Set-up cards for workout segments with a fixed number of exercises (e.g. Dice and Cards)
 */

// This is used for loading the setup screen for cards and dice workouts
// CardsFragment and DiceFragment extend this

public class NumberedFragment extends Fragment implements SavePresetsDialogFragment.UpdatePresets
{
    // Save/load fragments
    protected SavePresetsDialogFragment savePresetsFragment;
    protected LoadPresetsDialogFragment loadPresetsFragment;

    protected List<String> chosenExercises;

    protected Handler viewHandler; // Handle UI events

    // FragmentManager for loading dialogs
    protected FragmentManager fm;

    // All the presets and exercises
    protected PresetManager<NumberedPreset> presets;
    protected PresetManager<Exercise> exercises;

    protected EditText[] fields; // where we write names of exercises
    protected ImageView[] saveButtons;
    protected Button loadPresetButton;
    protected Button savePresetButton;
    protected ImageButton playButton;

    // Updates view from a RiseTimerPreset. Called from UI thread.
    class NumberedPresetViewUpdater implements Runnable{

        int presetPosition; // position in list of presets
        int fieldPosition; // only relevant for exercises
        String presetType; // exercise or preset of exercises?

        // for presets
        NumberedPresetViewUpdater(int presetPosition){
            this.presetPosition = presetPosition;
            this.presetType = WorkoutStatic.PRESET;
        }

        // for exercises
        NumberedPresetViewUpdater(int presetPosition, int fieldPosition){
            presetType = WorkoutStatic.EXERCISE;
            this.presetPosition = presetPosition;
            this.fieldPosition = fieldPosition;
        }

        @Override
        public void run(){
            // get Preset
            if (presetType.equals(WorkoutStatic.PRESET)){
                NumberedPreset p = presets.getPreset(presetPosition);

                // Fill fields
                for (int i = 0; i < fields.length; i++){
                    fields[i].setText(p.getExercise(i));
                }
            }
            else{
                fields[fieldPosition].setText(exercises.getPreset(presetPosition).getName());
            }
        }
    }

    // Initialize views. Generic for all NumberedFragments
    protected void initViews(View layout, int numExercises){
        fields = new EditText[numExercises];
        saveButtons = new ImageView[numExercises];

        exercises = new PresetManager<>(WorkoutStatic.EXERCISE_FILE, getContext(), new TypeToken<PresetHolder<Exercise>>(){}.getType());
        exercises.loadPresets();

        viewHandler = new Handler(Looper.getMainLooper());

        loadPresetButton = (Button) layout.findViewById(R.id.load_presets_text);
        savePresetButton = (Button) layout.findViewById(R.id.save_presets_text);

        for (int i = 0; i < numExercises; i++){
            fields[i] = (EditText) layout.findViewById(WorkoutStatic.FIELD_IDS[i]);
            saveButtons[i] = (ImageView) layout.findViewById(WorkoutStatic.SAVE_BUTTONS[i]);

            // Set a tag corresponding to the indices
            fields[i].setTag(i);

            // Set long click listener on text field
            fields[i].setOnLongClickListener(new ExerciseListener());

            // Set on click listener for save buttons
            saveButtons[i].setTag(i);
            saveButtons[i].setOnClickListener(new SaveExerciseListener());

            // Force all caps when entering, and max length of characters
            fields[i].setFilters(new InputFilter[] {new InputFilter.AllCaps(),new InputFilter.LengthFilter(WorkoutStatic.EXERCISE_MAX_LENGTH)});
        }

        // Set on click listener on play button

        playButton = (ImageButton) layout.findViewById(R.id.play_button_image);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // We'll either get load or save codes...no pause or stop codes in setup

        String presetName;
        String presetType;

        switch(resultCode){
            case WorkoutStatic.LOAD_CODE:

                int position = data.getIntExtra(WorkoutStatic.SELECTED_ENTRY, 0);
                boolean deleteEntry = data.getBooleanExtra(WorkoutStatic.DELETE_ENTRY,false);
                presetType = data.getStringExtra(WorkoutStatic.PRESET_TYPE);

                // If we're loading a preset
                if (presetType.equals(WorkoutStatic.PRESET)) {

                    presetName = presets.getPreset(position).getName();

                    if (deleteEntry){
                        presets.deletePreset(presetName);
                    }
                    else {
                        // refresh view
                        viewHandler.post(new NumberedPresetViewUpdater(position));
                    }
                }

                // If we're loading an exercise
                if (presetType.equals(WorkoutStatic.EXERCISE)){

                    presetName = exercises.getPreset(position).getName();
                    deleteEntry = data.getBooleanExtra(WorkoutStatic.DELETE_ENTRY, false);

                    if (deleteEntry){
                        exercises.deletePreset(presetName);
                    }
                    else {
                        // refresh view
                        int fieldIndex = data.getIntExtra(WorkoutStatic.INDEX,0);
                        viewHandler.post(new NumberedPresetViewUpdater(position, fieldIndex));
                    }
                }
                break;

            case WorkoutStatic.SAVE_CODE:

                presetType = data.getStringExtra(WorkoutStatic.PRESET_TYPE);
                presetName = data.getStringExtra(WorkoutStatic.CHOSEN_NAME);

                if (presetType.equals(WorkoutStatic.PRESET)) {
                    presets.addPreset(generateCurrentPreset(presetName));
                }

                else {
                    exercises.addPreset(generateCurrentExercise(presetName));
                }
                break;
        }
    }

    public NumberedPreset generateCurrentPreset(String name){
        List<String> exerciseNames = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            exerciseNames.add(fields[i].getText().toString());
        }

        return new NumberedPreset(name,exerciseNames);
    }

    public Exercise generateCurrentExercise(String name){
        return new Exercise(name);
    }

    // Use this to keep activity in place after rotation etc.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set up FragmentManager
        fm = getActivity().getSupportFragmentManager();

        // Keep around after screen rotation
        setRetainInstance(true);
    }

    protected void setPresetListeners(){
        // save and load fragments
        loadPresetButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                updateLoadFragment(WorkoutStatic.PRESET,-1);

                if (presets.getNumPresets()>0) {
                    // Show stopWorkoutFragment
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

                // Show stopWorkoutFragment
                savePresetsFragment.show(fm, WorkoutStatic.SAVE_DIALOG);
            }});
    }

    private class ExerciseListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View view){

            if (presets.getNumPresets()>0) {

                // We want to load the list of exercises
                // We also want to pass the index of the exercise
                updateLoadFragment(WorkoutStatic.EXERCISE, (Integer)view.getTag());

                // Show stopFragment
                loadPresetsFragment.show(fm, WorkoutStatic.LOAD_DIALOG);
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

            updateSaveFragment(WorkoutStatic.EXERCISE,String.valueOf(fields[(Integer)view.getTag()].getText()));

            // Show stopFragment
            savePresetsFragment.show(fm, WorkoutStatic.SAVE_DIALOG);
        }
    }

    // Implementation of save/load dialogs depends on fragment

    @Override
    public void updateSaveFragment(String presetType, String typedName){}

    @Override
    public void updateLoadFragment(String presetType, int index){}

}

