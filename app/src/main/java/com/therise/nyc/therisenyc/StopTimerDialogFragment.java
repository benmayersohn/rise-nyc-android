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
import android.widget.ImageButton;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * IntroDialogFragment: Explains how to use the program when first opening
 * Includes a checkmark for users to indicate they are familiar with this dialog and would
 * not like to see it again.
 *
 * This dialog should only show if we have it set in our preferences to show
 */

public class StopTimerDialogFragment extends DialogFragment {

    private ImageButton pauseTimer;  // Pause instead of stopping
    private ImageButton stopTimer;  // truly stop

    private static final String STOP_DIALOG = "STOP_DIALOG";

    private static final int PAUSE_CODE = 0;
    private static final int STOP_CODE = 1;

    // This allows our activity to refresh the timer fragment
    public interface OnTimerStopped{
        public void onTimerStopped();
    }

    // Empty constructor
    public StopTimerDialogFragment(){}

    // Create a new instance
    static StopTimerDialogFragment newInstance() {
        StopTimerDialogFragment f = new StopTimerDialogFragment();

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
        View v = inflater.inflate(R.layout.fragment_timer_stop, container, false);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Button and Dialog CheckBox from view
        pauseTimer = (ImageButton) view.findViewById(R.id.pause_button);
        stopTimer = (ImageButton) view.findViewById(R.id.stop_button);

        // Set onClick listener on button
        pauseTimer.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v){

                // Tell fragment to pause timer
                getTargetFragment().onActivityResult(getTargetRequestCode(),PAUSE_CODE, new Intent());

                // Close fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fm.findFragmentByTag(STOP_DIALOG));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

            }
        });

        // Set onClick listener on button
        stopTimer.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v){

                // Tell fragment to stop timer
                getTargetFragment().onActivityResult(getTargetRequestCode(),STOP_CODE, new Intent());

                // Close fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fm.findFragmentByTag(STOP_DIALOG));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

                // Refresh view
                ((WorkoutActivity)getActivity()).onTimerStopped();

            }
        });


    }

}