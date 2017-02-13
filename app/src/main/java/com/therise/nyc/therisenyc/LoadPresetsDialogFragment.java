package com.therise.nyc.therisenyc;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * LoadPresetsDialogFragment:
 */

public class LoadPresetsDialogFragment extends DialogFragment {

    // Associate custom adapter with our list view
    LoadPresetsAdapter adapter;
    ListView lv;

    private static final String PRESETS = "presets";
    private static final int LOAD_CODE = 2;

    // Empty constructor
    public LoadPresetsDialogFragment(){}

    // Create a new instance
    static LoadPresetsDialogFragment newInstance(ArrayList<String> presetNames) {
        LoadPresetsDialogFragment f = new LoadPresetsDialogFragment();

        // Supply presets
        Bundle args = new Bundle();
        args.putStringArrayList(PRESETS,presetNames);
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

        View v = inflater.inflate(R.layout.fragment_load_presets, container, false);

        // Set up listview

        lv = (ListView) v.findViewById(R.id.presets_list_view);

        adapter = new LoadPresetsAdapter( getActivity(), getTargetFragment(), getTargetRequestCode(), LOAD_CODE, getArguments().getStringArrayList(PRESETS));

        // set adapter
        lv.setAdapter(adapter);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}

