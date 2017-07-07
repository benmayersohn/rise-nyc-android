package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import java.io.BufferedReader;
import android.widget.ListView;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicReference;

/** We will build our workout fragment depending on what tab we're on
* We create a new instance of the location fragment from the activity by passing the tab name
* The tab name creates an SQL query which will pop up the relevant pages
* Print the content onto a dynamically generated fragment and push to the activity
**/

public class LocationFragment extends Fragment {
    final private AtomicReference<SharedPreferences> prefs = new AtomicReference<>();

    // DB
    private SQLiteDatabase db;

    // Constructor
    public LocationFragment() {}

    // We get the name of the tab and the view from the activity
    public static LocationFragment newInstance(String tabName, String viewType) {
        LocationFragment fragment = new LocationFragment();

        // Bundle of arguments
        Bundle args = new Bundle();

        // Add arguments to fragment
        args.putString(LocationStatic.TAB_NAME, tabName);
        args.putString(LocationStatic.VIEW_TYPE, viewType);

        fragment.setArguments(args);
        return fragment;
    }

    // Get name of tab we're on
    public String getTabName() {
        return getArguments().getString(LocationStatic.TAB_NAME,null);
    }

    // Get view type, either DAY_VIEW or BOROUGH_VIEW
    public String getViewType() {
        return getArguments().getString(LocationStatic.VIEW_TYPE,null);
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
        View layout = inflater.inflate(R.layout.fragment_location, container, false);

        // By this step we should have our database
        String tabName = getTabName();
        String viewType = getViewType();

        // Query by day or borough
        Cursor cursor;
        String[] args = {tabName};

        // We'll load our database first
        try
        {
            LocationDatabaseHelper repo = LocationDatabaseHelper.getInstance(getActivity());
            db = repo.getReadableDatabase();
        }

        catch(Exception e){e.printStackTrace();}

        // Query
        if (viewType.equals(LocationStatic.DAY_VIEW))
        {
            cursor = db.rawQuery("SELECT * FROM locations WHERE day = ? ", args);
        }
        else{
            cursor = db.rawQuery("SELECT * FROM locations WHERE borough = ? ", args);
        }

        // Clear current view

        // Get description for each workout. We'll use a BufferedReader and an InputStreamReader
        BufferedReader br;

        // These will be passed to our custom adapter to create our ListView
        // LocPage is a custom class that combines all information needed to generate a
        // page for each workout
        List<LocPage> pages = new ArrayList<>();

        // Location of description. Load into descs
        String desc_location;
        String line;
        StringBuilder sb;

        // Other fields, which will be used to create a new LocPage
        String title;
        String place;
        String day;
        int img;

        String desc = null;

        // Associate custom adapter with our list view
        LocFragmentAdapter adapter;
        ListView lv;

        // Get info for each workout that matched query
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(LocationStatic.KEY_TITLE));
            day = LocationStatic.DAY_MAP.get(cursor.getString(cursor.getColumnIndex(LocationStatic.KEY_DAY)));
            place = cursor.getString(cursor.getColumnIndex(LocationStatic.KEY_PLACE));
            img = getResources().getIdentifier(cursor.getString(cursor.getColumnIndex(LocationStatic.KEY_IMG)), "drawable", getActivity().getPackageName());

            desc_location = cursor.getString(cursor.getColumnIndex(LocationStatic.KEY_DESC));

            // Read from text file to get description
            try {
                br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(desc_location)));
                sb = new StringBuilder();

                // Load summary into single string
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                desc = sb.toString();
                br.close();

            }
            catch(Exception e){e.printStackTrace();}

            // Add all this to list of LocPage
            pages.add(new LocPage(title, place, day, img, desc));

        }

        cursor.close();

        // Set up listview
        adapter = new LocFragmentAdapter(getActivity(), pages);
        lv = (ListView) layout.findViewById(R.id.loc_listview);

        lv.setAdapter(adapter);

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
