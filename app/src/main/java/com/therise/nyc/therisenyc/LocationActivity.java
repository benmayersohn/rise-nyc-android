package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by mayerzine on 1/14/17.
 *
 * This gives us an activity with a ViewPager for cycling through different workout locations
 * We want to make sure to hold onto preferences (i.e. remember what tab we're on and
 * how far scrolled down we are, even when we leave the activity)
 */

public class LocationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;
    private String currView;
    private int numViews;

    LocationPagerAdapter pagerAdapter;

    // We create the database here and let the fragments access them via

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start out with locations by day
        setContentView(R.layout.locations);

        currView = LocationStatic.DAY_VIEW;
        numViews = LocationStatic.NUM_VIEWS_DAY;

        // Create tab layout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        pager = (ViewPager) findViewById(R.id.pager);

        // Store pages off screen so we don't need to reload them
        pager.setOffscreenPageLimit(LocationStatic.NUM_VIEWS_DAY-1);

        // Associate adapter with pager
        pagerAdapter = new LocationPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // Add tabs. Initially view by day
        for (int i = 0; i <LocationStatic.DAYS.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(LocationStatic.DAYS[i]),i);
        }

        // Set up tab layout listener with pager
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Set up page change listener with tabLayout
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);

                if (tab != null){
                    tab.select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private class LocationPagerAdapter extends FragmentStatePagerAdapter{
        LocationPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            if (currView.equals(LocationStatic.DAY_VIEW)) {
                return LocationFragment.newInstance(LocationStatic.DAYS[pos], currView);
            }

            // Otherwise it's borough view
            return LocationFragment.newInstance(LocationStatic.BOROUGHS[pos], currView);

        }

        // notifyDataSetChanged() calls this. This helps us clear fragments on new views
        @Override
        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }


        @Override
        public int getCount() {
            return numViews;
        }

    }

    // Create our options menu, primarily for choosing view (by day or borough), also settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.locations_menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }

    // When options selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.organize:
                return true;

            // Switch to sorting by borough
            case R.id.by_borough:
                if (currView.equals(LocationStatic.DAY_VIEW)) {

                    // Set current tab to 0
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null){
                        tab.select();
                    }

                    // Change view
                    currView = LocationStatic.BOROUGH_VIEW;
                    numViews = LocationStatic.NUM_VIEWS_BOROUGH;

                    // Scrollable tab mode for boroughs because there's a lot of text
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

                    // Remove all tabs up to first one
                    for (int i = LocationStatic.DAYS.length - 1; i >= 0; i--) {
                        pagerAdapter.notifyDataSetChanged();
                        tabLayout.removeTabAt(i);
                    }

                    // Add new tabs
                    for (int i = 0; i < LocationStatic.BOROUGHS.length; i++) {
                        pagerAdapter.notifyDataSetChanged();
                        tabLayout.addTab(tabLayout.newTab().setText(LocationStatic.BOROUGHS[i]), i);
                    }
                }

                return true;

            // Switch to sorting by day
            case R.id.by_dayofweek:
                if (currView.equals(LocationStatic.BOROUGH_VIEW)) {

                    // Set current tab to 0
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    if (tab != null){
                        tab.select();
                    }

                    // Fixed tab mode for days so it fits evenly on screen
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);

                    // Change view
                    currView = LocationStatic.DAY_VIEW;
                    numViews = LocationStatic.NUM_VIEWS_DAY;

                    // Remove all tabs up to first one
                    for (int i = LocationStatic.BOROUGHS.length-1; i >= 0; i--){
                        pagerAdapter.notifyDataSetChanged();
                        tabLayout.removeTabAt(i);
                    }

                    // Add new tabs and pages together
                    for (int i = 0; i <LocationStatic.DAYS.length; i++) {
                        pagerAdapter.notifyDataSetChanged();
                        tabLayout.addTab(tabLayout.newTab().setText(LocationStatic.DAYS[i]),i);
                    }

                }

                return true;

            case R.id.settings:
                // Open up preferences
                startActivity(new Intent(this, Preferences.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}
