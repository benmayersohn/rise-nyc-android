package com.therise.nyc.therisenyc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import java.util.HashMap;

import java.util.concurrent.atomic.AtomicReference;

/** We will build our workout fragment depending on what tab we're on
* We create a new instance of the location fragment from the activity by passing the tab name
* The tab name creates an SQL query which will pop up the relevant pages
* Print the content onto a dynamically generated fragment and push to the activity
**/

public class LocationFragment extends Fragment {
    final private AtomicReference<SharedPreferences> prefs = new AtomicReference<>();

    // Keys from SQL database
    private static final String KEY_TITLE = "title";
    private static final String KEY_DAY = "day";
    private static final String KEY_PLACE = "place";
    private static final String KEY_DESC = "desc";
    private static final String KEY_IMG = "img";

    private static final String TAB_NAME = "tabName";
    private static final String VIEW_TYPE = "viewType";

    private static final String DAY_VIEW = "DAY_VIEW";

    public static final int IMAGE_HEIGHT = 220;
    public static final int IMAGE_WIDTH = 300;

    // Create a map between shortened weekdays and values
    private static final HashMap<String,String> days = new HashMap(){
        {
            put("M","Monday");
            put("T","Tuesday");
            put("W","Wednesday");
            put("R","Thursday");
            put("F","Friday");
        }
    };

    // We'll assign these on creation
    private String tabName=null;
    private String viewType=null;

    // DB stuff
    private LocationDatabaseHelper repo;
    private SQLiteDatabase db;

    // Constructor
    public LocationFragment() {};

    // Calculate proper bitmap size for required width/height
    // don't call directly
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Use THIS method to load bitmap efficiently
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    // We get the name of the tab and the view from the activity
    public static LocationFragment newInstance(String tabName, String viewType) {
        LocationFragment fragment = new LocationFragment();

        // Bundle of arguments
        Bundle args = new Bundle();

        // Add arguments to fragment
        args.putString(TAB_NAME, tabName);
        args.putString(VIEW_TYPE, viewType);

        fragment.setArguments(args);
        return fragment;
    }

    // Get name of tab we're on
    public String getTabName() {
        return getArguments().getString(TAB_NAME,null);
    }

    // Get view type, either DAY_VIEW or BOROUGH_VIEW
    public String getViewType() {
        return getArguments().getString(VIEW_TYPE,null);
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
        tabName = getTabName();
        viewType = getViewType();

        // Query by day or borough
        Cursor cursor = null;
        String[] args = {tabName};

        // We'll load our database first
        try
        {
            repo = LocationDatabaseHelper.getInstance(getActivity().getApplicationContext());
            db = repo.getReadableDatabase();
        }

        catch(Exception e){}

        // Query
        if (viewType.equals(DAY_VIEW))
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
        Bitmap imgBitmap;

        // set height and width of bitmap (in dp)
        int newHeight = IMAGE_HEIGHT;
        int newWidth = IMAGE_WIDTH;

        String desc = null;

        // Associate custom adapter with our list view
        LocFragmentAdapter adapter;
        ListView lv;

        // Get info for each workout that matched query
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            day = days.get(cursor.getString(cursor.getColumnIndex(KEY_DAY)));
            place = cursor.getString(cursor.getColumnIndex(KEY_PLACE));
            img = getResources().getIdentifier(cursor.getString(cursor.getColumnIndex(KEY_IMG)), "drawable", getActivity().getPackageName());

            desc_location = cursor.getString(cursor.getColumnIndex(KEY_DESC));

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
            catch(Exception e){}

            // imgBitmap = decodeSampledBitmapFromResource(getResources(), img,IMAGE_WIDTH,IMAGE_HEIGHT);

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
