package com.therise.nyc.therisenyc;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.view.WindowManager;

/**
 * Created by mayerzine on 1/14/17.
 *
 * This gives us an activity with a ViewPager for cycling through workouts
 */

public class WorkoutActivity extends AppCompatActivity
        implements StopTimerDialogFragment.OnTimerStopped,
        EndTimerDialogFragment.OnTimerEnded
{

    private TabLayout tabLayout;
    private ViewPager pager;

    private static final int NUM_VIEWS = 3;
    private static final String CURRENT_VIEW = "CURRENT_VIEW";

    private static final int TIMER_VIEW = 0;
    private static final int DICE_VIEW = 1;
    private static final int CARDS_VIEW = 2;

    WorkoutPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.workouts);

        // Let volume settings control MEDIA volume, not ringer
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Keep screen on for this activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Create tab layout
        tabLayout = (TabLayout) findViewById(R.id.tab_layout2);
        pager = (ViewPager) findViewById(R.id.pager2);

        pagerAdapter = new WorkoutPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // set off screen pager limit to 2 (i.e. we can hold all three pages in memory)
        pager.setOffscreenPageLimit(NUM_VIEWS-1);

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
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onTimerStopped()
    {
        // Refresh the pager
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void onTimerEnded(){
        // Refresh the pager
        pager.setAdapter(pagerAdapter);
    }

    // We're either on timer page, dice page, or cards page
    // Instantiate appropriate fragment
    private class WorkoutPagerAdapter extends FragmentStatePagerAdapter{
        public WorkoutPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            if (pos == TIMER_VIEW) {
                return TimerFragment.newInstance();
            }

            else if(pos == DICE_VIEW) {
                return DiceFragment.newInstance();
            }
            // Otherwise it's card view
            return CardsFragment.newInstance();

        }

        // notifyDataSetChanged calls this. This helps us clear fragments on new views
        @Override
        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }


        @Override
        public int getCount() {
            return NUM_VIEWS;
        }

    }

    // Create our options menu (namely for sorting locations, also settings again)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.workouts_menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }

    // When options selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.settings:

                // Open up preferences
                startActivity(new Intent(this, Preferences.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
