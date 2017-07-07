package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import android.text.TextWatcher;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

/**
 * SavePresetsDialogFragment: Allows user to save a custom preset
 */

public class SavePresetsDialogFragment extends DialogFragment {

    private String chosenName = "";

    // Associate custom adapter with our list view
    LoadPresetsAdapter adapter;
    ListView lv;

    private EditText entryView;
    private TextView duplicateWarning;
    Button saveButton;

    // Empty constructor
    public SavePresetsDialogFragment(){}

    // Create a new instance
    static SavePresetsDialogFragment newInstance(ArrayList<String> presetNames, String workoutType, String presetType, String typedName) {
        SavePresetsDialogFragment f = new SavePresetsDialogFragment();

        // Supply presets
        Bundle args = new Bundle();
        args.putStringArrayList(WorkoutStatic.PRESETS,presetNames);
        args.putString(WorkoutStatic.WORKOUT_TYPE,workoutType);
        args.putString(WorkoutStatic.PRESET_TYPE,presetType);
        args.putString(WorkoutStatic.TYPED_NAME,typedName);
        f.setArguments(args);

        return f;
    }

    // Update save and load dialogs with new preset information
    interface UpdatePresets{
        void updateSaveFragment(String presetType, String typedName);
        void updateLoadFragment(String presetType, int index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Keep around after screen rotation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_save_presets, container, false);

        // Display a simple dialog box
        // Send name we chose back to fragment

        entryView = (EditText) v.findViewById(R.id.preset_name);

        // force all writing to upper-case
        entryView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // Set the text as the current exercise, if we're saving an exercise
        if (getArguments().getString(WorkoutStatic.PRESET_TYPE).equals(WorkoutStatic.EXERCISE)){
            entryView.setText(getArguments().getString(WorkoutStatic.TYPED_NAME));
            chosenName = entryView.getText().toString();
        }

        // Are we trying to enter a preset with the same name as one that is already stored?
        duplicateWarning = (TextView) v.findViewById(R.id.same_name_warning);

        // Set a textchanged listener for entryview
        entryView.addTextChangedListener(new TextWatcher(){

            public void afterTextChanged(Editable s) {
                // set chosenName equal to lowercase version of whatever is entered
                chosenName = entryView.getText().toString();

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                // If we got a warning about visibility, get rid of it when changing name
                if (duplicateWarning.getVisibility() == VISIBLE){
                    duplicateWarning.setVisibility(INVISIBLE);
                }

            }
        });

        saveButton = (Button) v.findViewById(R.id.save_preset_button);

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check name first
                if (getArguments().getStringArrayList(WorkoutStatic.PRESETS).contains(chosenName)){
                    duplicateWarning.setVisibility(VISIBLE);
                }

                // If our name is not a duplicate, submit
                else {

                    Intent intent = new Intent();
                    intent.putExtra(WorkoutStatic.CHOSEN_NAME, chosenName);
                    intent.putExtra(WorkoutStatic.PRESET_TYPE,getArguments().getString(WorkoutStatic.PRESET_TYPE));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), WorkoutStatic.SAVE_CODE, intent);

                    // Updates presets with new entry
                    switch (getArguments().getString(WorkoutStatic.WORKOUT_TYPE)){
                        case WorkoutStatic.TIMER:
                            ((RiseTimerFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),getArguments().getString(WorkoutStatic.TYPED_NAME));
                            ((RiseTimerFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),-1);
                            break;
                        case WorkoutStatic.DICE:
                            ((DiceFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),getArguments().getString(WorkoutStatic.TYPED_NAME));
                            ((DiceFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),-1);
                            break;
                        case WorkoutStatic.CARDS:
                            ((CardsFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),getArguments().getString(WorkoutStatic.TYPED_NAME));
                            ((CardsFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(WorkoutStatic.PRESET_TYPE),-1);
                            break;
                    }


                    // Close fragment
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fm.findFragmentByTag(WorkoutStatic.SAVE_DIALOG));
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                }
            }
        });

        // We display all the saved fragments so we can choose a name without overwriting


        // Set up listview
        lv = (ListView) v.findViewById(R.id.presets_list_view);

        // Load stored presets so user knows which names are already taken
        adapter = new LoadPresetsAdapter(getActivity(), getTargetFragment(),
                getTargetRequestCode(), WorkoutStatic.SAVE_CODE, getArguments().getStringArrayList(WorkoutStatic.PRESETS),
                getArguments().getString(WorkoutStatic.PRESET_TYPE), -1);
        lv.setAdapter(adapter);

        return v;
    }

}

