package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FrontScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_screen);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean skipDialog = preferences.getBoolean(GeneralStatic.SKIP_DIALOG_PREF,false);

        if (!(skipDialog)){
            // Show options menu
            showDialog();
        }

        // Social Media buttons
        Button fbButton = findViewById(R.id.fb_button);
        Button instaButton = findViewById(R.id.insta_button);
        Button twitterButton = findViewById(R.id.twitter_button);
        Button meetupButton = findViewById(R.id.meetup_button);
        LinearLayout urlButton = findViewById(R.id.rise_url_button);

        // FACEBOOK
        fbButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = SocialMediaTools.getFacebookPageURL(getApplicationContext());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });

        // INSTA
        instaButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent instaIntent = new Intent(Intent.ACTION_VIEW);
                String instaUrl = SocialMediaTools.getInstaURL(getApplicationContext());
                instaIntent.setData(Uri.parse(instaUrl));
                startActivity(instaIntent);
            }
        });

        // TWITTER
        twitterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW);
                String twitterUrl = SocialMediaTools.getTwitterURL(getApplicationContext());
                twitterIntent.setData(Uri.parse(twitterUrl));
                startActivity(twitterIntent);
            }
        });

        // MEETUP
        meetupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = GeneralStatic.MEETUP_ADDRESS;
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // WEBSITE
        urlButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = GeneralStatic.WEB_ADDRESS;
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

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
        newFragment.show(ft, GeneralStatic.FRAGMENT_TIP_TAG);
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
