package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * This is for setup. See CardsActivity for when workout starts
 */

public class CardsFragment extends NumberedFragment implements SavePresetsDialogFragment.UpdatePresets{

    // Buttons
    private CheckBox jokerBox; // do we want jokers enabled?

    // We get the name of the tab and the view from the activity
    public static CardsFragment newInstance() {
        return new CardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        // get presets/exercises
        presets = new PresetManager<>(CardsStatic.PRESET_FILE, getActivity(), new TypeToken<PresetHolder<NumberedPreset>>(){}.getType());
        presets.loadPresets();

        // Initial view; we will return this
        View layout = inflater.inflate(R.layout.fragment_cards_initial, container, false);

        // set initial view
        initViews(layout, CardsStatic.NUM_EXERCISES);

        // Display first preset, if we have one stored
        if (presets.getNumPresets()>0){
            new NumberedPresetViewUpdater(0).run();
        }

        // Set on click listener for checkbox
        jokerBox = (CheckBox)layout.findViewById(R.id.has_jokers);

        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                chosenExercises = new ArrayList<>(CardsStatic.NUM_EXERCISES);

                // Now we solidify our chosen exercises
                for (int i = 0; i < CardsStatic.NUM_EXERCISES; i++){
                    chosenExercises.add(i,String.valueOf(fields[i].getText()));
                }

                // Add exercises to intent
                Intent intent = new Intent(getContext(), CardsActivity.class);
                intent.putStringArrayListExtra(WorkoutStatic.CHOSEN_EXERCISES, (ArrayList<String>)chosenExercises);
                intent.putExtra(CardsStatic.HAS_JOKERS,jokerBox.isChecked());

                // Start cards activity
                startActivity(intent);
            }
        });

        // Set listeners for load/save preset dialogs
        setPresetListeners();

        // Return the layout
        return layout;

    }
    @Override
    public void updateSaveFragment(String presetType, String typedName){
        // Create save fragment

        if (presetType.equals(WorkoutStatic.PRESET)) {
            if (!(presets == null)) {
                savePresetsFragment = SavePresetsDialogFragment.newInstance(presets.getNames(), CardsStatic.WORKOUT_TYPE, WorkoutStatic.PRESET, typedName);
                savePresetsFragment.setTargetFragment(this, CardsStatic.REQUEST_CODE);
            }
        }

        if (presetType.equals(WorkoutStatic.EXERCISE)) {
            if (!(exercises == null)) {
                savePresetsFragment = SavePresetsDialogFragment.newInstance(exercises.getNames(), CardsStatic.WORKOUT_TYPE, WorkoutStatic.EXERCISE, typedName);
                savePresetsFragment.setTargetFragment(this, CardsStatic.REQUEST_CODE);
            }
        }
    }

    @Override
    public void updateLoadFragment(String presetType, int index){

        // Create load fragment

        if (presetType.equals(WorkoutStatic.PRESET)) {
            if (!(presets == null) && presets.getNumPresets() > 0) {
                loadPresetsFragment = LoadPresetsDialogFragment.newInstance(presets.getNames(), WorkoutStatic.PRESET, index);
                loadPresetsFragment.setTargetFragment(this, CardsStatic.REQUEST_CODE);
            }
        }

        if (presetType.equals(WorkoutStatic.EXERCISE)) {
            if (!(exercises == null) && exercises.getNumPresets() > 0) {
                loadPresetsFragment = LoadPresetsDialogFragment.newInstance(exercises.getNames(), WorkoutStatic.EXERCISE, index);
                loadPresetsFragment.setTargetFragment(this, CardsStatic.REQUEST_CODE);
            }
        }
    }

}
