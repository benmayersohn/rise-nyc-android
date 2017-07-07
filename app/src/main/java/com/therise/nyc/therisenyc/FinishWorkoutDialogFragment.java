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
import android.widget.Button;

public class FinishWorkoutDialogFragment extends DialogFragment {

    // This allows our activity to refresh the timer fragment
    interface OnWorkoutFinished{
        void onWorkoutFinished();
    }

    // Empty constructor
    public FinishWorkoutDialogFragment(){}

    // Create a new instance
    static FinishWorkoutDialogFragment newInstance(String workoutType) {
        FinishWorkoutDialogFragment f = new FinishWorkoutDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(WorkoutStatic.WORKOUT_TYPE,workoutType);
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
        return inflater.inflate(R.layout.fragment_dialog_ended, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Button and Dialog CheckBox from view
        Button okay = (Button) view.findViewById(R.id.okay_button);

        // Set onClick listener on button
        okay.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick (View v){
            String workoutType = getArguments().getString(WorkoutStatic.WORKOUT_TYPE);

            if (workoutType != null){
                if (workoutType.equals(RiseTimerStatic.WORKOUT_TYPE)){

                    // Close fragment
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fm.findFragmentByTag(WorkoutStatic.FINISH_DIALOG));
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();

                    // Refresh view
                    ((WorkoutActivity)getActivity()).onWorkoutFinished();
                }

                // Otherwise, just kill launching activity to go back to corresponding fragment
                else{
                    ((NumberedActivity)getActivity()).onWorkoutFinished();
                }

            }
            }
        });

    }

}
