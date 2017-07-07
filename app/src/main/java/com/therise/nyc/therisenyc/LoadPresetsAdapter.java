package com.therise.nyc.therisenyc;


// LoadPresetsAdapter: Set up ListView of presets/exercises to load

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

class LoadPresetsAdapter extends BaseAdapter{

    private List<String> presetNames;
    private String presetType;

    private static LayoutInflater inflater=null;

    private FragmentActivity act;
    private Fragment targetFragment;
    private int requestCode;
    private int resultCode;
    private int index;

    private static final int LOAD_CODE = 2;

    // Construct view
    LoadPresetsAdapter(FragmentActivity act, Fragment targetFragment,
                              int requestCode, int resultCode, ArrayList<String> presetNames,
                              String presetType, int index) {
        this.presetNames = presetNames;
        this.presetType = presetType;
        this.index = index;

        this.act = act;
        this.targetFragment = targetFragment;
        this.requestCode = requestCode;
        this.resultCode = resultCode;

        inflater = ( LayoutInflater )this.act.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {

        return presetNames.size();
    }

    // Don't need this
    @Override
    public Object getItem(int position) {
        return presetNames.get(position);
    }

    // The ID is just the position
    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class PresetHolder
    {
        TextView presetTitle;
    }

    private void chooseEntry(int position, boolean deleteEntry){

        // What entry did we choose?
        Intent intent = new Intent();
        intent.putExtra(WorkoutStatic.SELECTED_ENTRY, position);

        // Are we deleting it?
        intent.putExtra(WorkoutStatic.DELETE_ENTRY, deleteEntry);

        intent.putExtra(WorkoutStatic.INDEX, index);

        // Are we displaying it? (we are if it's an exercise)
        intent.putExtra(WorkoutStatic.PRESET_TYPE,presetType);

        targetFragment.onActivityResult(requestCode, LOAD_CODE, intent);

        // Close fragment
        FragmentManager fm = targetFragment.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.remove(fm.findFragmentByTag(WorkoutStatic.LOAD_DIALOG));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PresetHolder holder=new PresetHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.presets_list, null);
        }

        String currName = (String) getItem(position);

        holder.presetTitle = (TextView) convertView.findViewById(R.id.preset_choice);
        holder.presetTitle.setText(currName);

        // Set on click listener if we're loading
        // not if we're saving

        if (resultCode == LOAD_CODE) {
            // on-click listener for selection
            holder.presetTitle.setOnClickListener(new OnClickListener() {

                // Set onclick from here
                @Override
                public void onClick(View v) {
                    chooseEntry(position, false); // send for loading, NOT deleting
                }
            });

            // on long touch listener for deletion
            holder.presetTitle.setOnLongClickListener(new View.OnLongClickListener() {

                // Set onclick from here
                @Override
                public boolean onLongClick(View arg0) {

                    int duration = Toast.LENGTH_SHORT;

                    if (presetType.equals(WorkoutStatic.PRESET)) {
                        // We post a message that we deleted a preset
                        Toast toast = Toast.makeText(act, R.string.preset_deleted, duration);
                        toast.show();
                    }

                    if (presetType.equals(WorkoutStatic.EXERCISE)) {
                        // We post a message that we deleted a preset
                        Toast toast = Toast.makeText(act, R.string.exercise_deleted, duration);
                        toast.show();
                    }


                    // entry deleted
                    chooseEntry(position, true);

                    // Don't call onClick
                    return true;

                }
            });
        }

        return convertView;
    }

}
