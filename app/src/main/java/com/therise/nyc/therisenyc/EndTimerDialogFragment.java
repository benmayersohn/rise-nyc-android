package com.therise.nyc.therisenyc;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * IntroDialogFragment: Explains how to use the program when first opening
 * Includes a checkmark for users to indicate they are familiar with this dialog and would
 * not like to see it again.
 *
 * This dialog should only show if we have it set in our preferences to show
 */

public class EndTimerDialogFragment extends DialogFragment {

    private Button okay;  // end workout

    private static final String END_DIALOG = "END_DIALOG";

    // This allows our activity to refresh the timer fragment
    public interface OnTimerEnded{
        void onTimerEnded();
    }

    // Empty constructor
    public EndTimerDialogFragment(){}

    // Create a new instance
    static EndTimerDialogFragment newInstance() {
        EndTimerDialogFragment f = new EndTimerDialogFragment();

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
        View v = inflater.inflate(R.layout.fragment_dialog_ended, container, false);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Button and Dialog CheckBox from view
        okay = (Button) view.findViewById(R.id.okay_button);

        // Set onClick listener on button
        okay.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v){
                // Close fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fm.findFragmentByTag(END_DIALOG));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

                // Refresh view
                ((WorkoutActivity)getActivity()).onTimerEnded();

            }
        });


    }

}
