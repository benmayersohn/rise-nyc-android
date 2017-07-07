package com.therise.nyc.therisenyc;

import android.app.Dialog;
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

// RiseTimerIssueDialogFragment - show if we've entered any incorrect parameters to the Rise Timer

public class RiseTimerIssueDialogFragment extends DialogFragment {

    // Empty constructor
    public RiseTimerIssueDialogFragment(){}

    // Create a new instance
    static RiseTimerIssueDialogFragment newInstance() {
        RiseTimerIssueDialogFragment f = new RiseTimerIssueDialogFragment();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer_issue, container, false);
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

                // Close fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fm.findFragmentByTag(RiseTimerStatic.RISE_TIMER_ISSUE_DIALOG));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.commit();

            }
        });


    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            if (dialog.getWindow() != null){
                dialog.getWindow().setLayout(width, height);
            }

        }
    }

}
