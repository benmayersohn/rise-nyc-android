package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class FrontScreenActivity extends AppCompatActivity {

    private static final String DIALOG="fragment_tip";
    private static final String SKIP_DIALOG_PREF = "dontShowDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_screen);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean skipDialog = preferences.getBoolean(SKIP_DIALOG_PREF,false);

        if (!(skipDialog)){
            // Show options menu
            showDialog();
        }

    }

    // Create our options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.front_menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }

    public void showDialog() {

        // We will show the dialog according to our preferences

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Create dialog
        DialogFragment newFragment = IntroDialogFragment.newInstance();

        // We will only show it if we want to
        newFragment.show(ft, DIALOG);
    }

    // When options selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.locations: {

                startActivity(new Intent(this, LocationActivity.class));

                return true;
            }

            case R.id.workouts: {

                startActivity(new Intent(this, WorkoutActivity.class));

                return true;
            }

            case R.id.settings: {

                startActivity(new Intent(this, Preferences.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
