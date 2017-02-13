package com.therise.nyc.therisenyc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mayerzine on 1/14/17.
 *
 * Dice fragment
 * Choose a random side of a 6-sided die.
 */

public class DiceFragment extends Fragment{

    public DiceFragment() {};

    // We get the name of the tab and the view from the activity
    public static DiceFragment newInstance() {
        DiceFragment fragment = new DiceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    // Use this to keep activity in place after rotation etc.
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Keep around after screen rotation
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_dice_initial, container, false);

        return layout;

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


}

