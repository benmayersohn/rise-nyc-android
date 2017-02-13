package com.therise.nyc.therisenyc;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mayerzine on 1/14/17.
 */

public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(android.R.id.content)==null){
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content,new Display()).commit();
        }
    }

    public static class Display extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_display);
        }

    }
}
