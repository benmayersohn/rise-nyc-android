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
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import android.text.TextWatcher;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

/**
 * IntroDialogFragment: Explains how to use the program when first opening
 * Includes a checkmark for users to indicate they are familiar with this dialog and would
 * not like to see it again.
 *
 * This dialog should only show if we have it set in our preferences to show
 */

public class SavePresetsDialogFragment extends DialogFragment {

    private static final String PRESETS = "presets";
    private static final String EXERCISE = "EXERCISE";
    private static final String TYPED_NAME = "TYPED_NAME";
    private static final String PRESET_TYPE = "PRESET_TYPE";

    private static final String WORKOUT_TYPE = "WORKOUT_TYPE";

    private static final String BLANK = "";

    // Workout types
    private static final String CARDS = "CARDS";
    private static final String TIMER = "TIMER";
    private static final String DICE = "DICE";

    private static final String SAVE_DIALOG = "SAVE_DIALOG";
    private static final String CHOSEN_NAME = "CHOSEN_NAME";

    private static final int SAVE_CODE = 3;
    private String chosenName = "";

    // Associate custom adapter with our list view
    private LoadPresetsAdapter adapter;
    private ListView lv;

    private EditText entryView;
    private TextView duplicateWarning;
    private Button saveButton;

    // Empty constructor
    public SavePresetsDialogFragment(){}

    // Create a new instance
    static SavePresetsDialogFragment newInstance(ArrayList<String> presetNames, String workoutType, String presetType, String typedName) {
        SavePresetsDialogFragment f = new SavePresetsDialogFragment();

        // Supply presets
        Bundle args = new Bundle();
        args.putStringArrayList(PRESETS,presetNames);
        args.putString(WORKOUT_TYPE,workoutType);
        args.putString(PRESET_TYPE,presetType);
        args.putString(TYPED_NAME,typedName);
        f.setArguments(args);

        return f;
    }

    public interface UpdatePresets{
        void updateSaveFragment(String presetType, String typedName);
        void updateLoadFragment(String presetType, int index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Keep around after screen rotation
        setRetainInstance(true);
    }

    // Show the view stored in fragment_tip.xml
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
        if (getArguments().getString(PRESET_TYPE).equals(EXERCISE)){
            entryView.setText(getArguments().getString(TYPED_NAME));
        }

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
                if (getArguments().getStringArrayList(PRESETS).contains(chosenName)){
                    duplicateWarning.setVisibility(VISIBLE);
                }

                // If our name is not a duplicate, submit
                else {

                    Intent intent = new Intent();
                    intent.putExtra(CHOSEN_NAME, chosenName);
                    intent.putExtra(PRESET_TYPE,getArguments().getString(PRESET_TYPE));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), SAVE_CODE, intent);

                    // Updates presets with new entry
                    switch (getArguments().getString(WORKOUT_TYPE)){
                        case TIMER:
                            ((TimerFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(PRESET_TYPE),getArguments().getString(TYPED_NAME));
                            ((TimerFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(PRESET_TYPE),-1);
                            break;
                        case DICE:
                            ((DiceFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(PRESET_TYPE),getArguments().getString(TYPED_NAME));
                            ((DiceFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(PRESET_TYPE),-1);
                            break;
                        case CARDS:
                            ((CardsFragment)getTargetFragment()).updateSaveFragment(
                                    getArguments().getString(PRESET_TYPE),getArguments().getString(TYPED_NAME));
                            ((CardsFragment)getTargetFragment()).updateLoadFragment(
                                    getArguments().getString(PRESET_TYPE),-1);
                            break;
                    }


                    // Close fragment
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fm.findFragmentByTag(SAVE_DIALOG));
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                }
            }
        });

        // We display all the saved fragments so we can choose a name without overwriting


        // Set up listview
        lv = (ListView) v.findViewById(R.id.presets_list_view);
        adapter = new LoadPresetsAdapter(getActivity(), getTargetFragment(),
                getTargetRequestCode(), SAVE_CODE, getArguments().getStringArrayList(PRESETS),
                getArguments().getString(PRESET_TYPE), -1);
        lv.setAdapter(adapter);

        // Return to activity

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}

