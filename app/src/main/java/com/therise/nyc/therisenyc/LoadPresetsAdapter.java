package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/22/17.
 */

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
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

// Construct custom view for each fragment
public class LoadPresetsAdapter extends BaseAdapter{

    private ArrayList<String> presetNames;
    private static LayoutInflater inflater=null;
    private FragmentActivity act;
    private Fragment targetFragment;
    private int requestCode;
    private int resultCode;

    private static final String LOAD_DIALOG = "LOAD_DIALOG";
    private static final String SELECTED_ENTRY = "SELECTED_ENTRY";
    private static final String DELETE_ENTRY = "DELETE_ENTRY";
    private static final int LOAD_CODE = 2;

    // Construct view
    public LoadPresetsAdapter(FragmentActivity act, Fragment targetFragment, int requestCode, int resultCode, ArrayList<String> presetNames) {
        this.presetNames = presetNames;

        this.act=act;
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

    public void chooseEntry(int position, boolean deleteEntry){

        // What entry did we choose?
        Intent intent = new Intent();
        intent.putExtra(SELECTED_ENTRY, position);

        // Are we deleting it?
        intent.putExtra(DELETE_ENTRY,deleteEntry);

        targetFragment.onActivityResult(requestCode, LOAD_CODE, intent);

        // Close fragment
        FragmentManager fm = targetFragment.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.remove(fm.findFragmentByTag(LOAD_DIALOG));
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

                    // We post a message that we deleted a preset
                    Toast toast = Toast.makeText(act, R.string.preset_deleted, duration);
                    toast.show();

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
