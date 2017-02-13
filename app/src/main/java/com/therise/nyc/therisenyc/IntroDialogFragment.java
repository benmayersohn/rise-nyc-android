package com.therise.nyc.therisenyc;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * IntroDialogFragment: Explains how to use the program when first opening
 * Includes a checkmark for users to indicate they are familiar with this dialog and would
 * not like to see it again.
 *
 * This dialog should only show if we have it set in our preferences to show
 */

public class IntroDialogFragment extends DialogFragment {


    private Button okayButton;  // Say OK to close the dialog
    private CheckBox doNotShowAgain;  // Check if we don't want to show the dialog again

    public final static String SKIP_DIALOG_PREF = "dontShowDialog";

    private static final String DIALOG="fragment_tip";

    // Empty constructor
    public IntroDialogFragment(){}

    // Create a new instance
    static IntroDialogFragment newInstance() {
        IntroDialogFragment f = new IntroDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
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
        View v = inflater.inflate(R.layout.fragment_tip, container, false);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Button and Dialog CheckBox from view
        okayButton = (Button) view.findViewById(R.id.button);
        doNotShowAgain = (CheckBox) view.findViewById(R.id.checkBox);

        // Set onClick listener on button
        okayButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v){
                // Close fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fm.findFragmentByTag(DIALOG));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

            }
        });

        // Set checked listener on checkbox
        doNotShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                Editor editor = preferences.edit();
                editor.putBoolean(SKIP_DIALOG_PREF, doNotShowAgain.isChecked());
                editor.commit();
            }
        });
    }

}
