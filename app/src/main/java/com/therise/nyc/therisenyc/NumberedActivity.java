package com.therise.nyc.therisenyc;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Abstract superclass for DiceActivity and CardsActivity classes
 * Includes common elements of both
 */

// Activity that has a simple timer, with ability to update the view

public abstract class NumberedActivity extends AppCompatActivity implements
        FinishWorkoutDialogFragment.OnWorkoutFinished{

    // Create simple timer
    protected SimpleTimer timer = new SimpleTimer(WorkoutStatic.RUNNING_CHECK_INTERVAL);

    // FragmentTransaction for loading dialogs
    protected FragmentTransaction ft;

    protected AudioController audioController;

    protected StopWorkoutDialogFragment stopWorkoutFragment;
    protected FinishWorkoutDialogFragment finishWorkoutFragment;

    // Handle UI events
    protected Handler viewHandler;
    protected Handler timerHandler;

    protected HandlerThread timerThread; // handle timer

    protected SimpleTimerRepeater timerRepeater;
    protected NumberedActivityViewUpdater viewUpdater;

    // To start timer
    protected void startTimer(){
        // Set up handler
        timerThread = new HandlerThread(WorkoutStatic.SIMPLE_TIMER_THREAD);
        timerThread.start();

        timerHandler = new Handler(timerThread.getLooper());

        // Set running view
        viewHandler.post(new Runnable() {
            @Override
            public void run() {
                viewUpdater.setRunningView();
            }
        });

        timerRepeater.reset();
        timerHandler.post(timerRepeater);
    }

    protected void resumeTimer(){
        // resume audiocontroller
        audioController.resume();

        // Set timer state to running
        startTimer();
    }

    protected void pauseTimer(){
        stopTimer();

        // Set paused view
        viewHandler.post(new Runnable() {
            @Override
            public void run() {
                viewUpdater.setPausedView();
            }
        });
    }

    protected void stopTimer(){
        if (timerRepeater != null) {

            // pause audio
            audioController.pause();

            // Stop timer
            timerRepeater.stop();
            timerThread.quitSafely();
        }
    }

    // Create a Runnable class that calls timer and updates views in a loop.
    class SimpleTimerRepeater implements Runnable {

        volatile boolean isLooping = true; // If this is true, loop.

        // Stop the loop
        public void stop() {
            isLooping = false;
        }

        void reset() { isLooping = true; }

        @Override
        public void run() {

            if (isLooping) {
                timer.step();

                // Did we step?
                if (timer.isOnSecondMark()) {

                    // Update the view
                    viewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewUpdater.updateViewFromTimer();
                        }
                    });

                }

                // lastly, loop again!
                timerHandler.postDelayed(timerRepeater, timer.getRunningCheckInterval());
            }
        }
    }

    // Create view updater class for timer
    class NumberedActivityViewUpdater{

        String viewState = WorkoutStatic.TIMER_RUNNING; // when activity created, we're running
        protected Context context;

        boolean playButtonCreated = false;

        ImageButton pauseButton;
        ImageButton playButton;
        ImageButton stopButton;
        TextView timeElapsedView;
        LinearLayout buttonRow; // where ImageButtons are

        NumberedActivityViewUpdater(Context context,
                                    ImageButton pauseButton,
                                    ImageButton playButton,
                                    ImageButton stopButton,
                                    LinearLayout buttonRow,
                                    TextView timeElapsedView){
            this.context = context;
            this.pauseButton = pauseButton;
            this.playButton = playButton;
            this.stopButton = stopButton;
            this.buttonRow = buttonRow;
            this.timeElapsedView = timeElapsedView;
        }

        public void setPausedView(){

            if (!playButtonCreated) {

                playButton.setLayoutParams(pauseButton.getLayoutParams());
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                playButton.setBackgroundResource(R.drawable.rect_border4);
                playButton.setPadding(pauseButton.getPaddingLeft(),
                        pauseButton.getPaddingTop(), pauseButton.getPaddingRight(), pauseButton.getPaddingBottom());

                playButtonCreated = true;
            }

            // Change pause logo to play
            if (buttonRow.getChildAt(0) != playButton){
                buttonRow.removeView(pauseButton);
                buttonRow.addView(playButton, 0);
            }

            viewState = WorkoutStatic.TIMER_PAUSED;
        }

        // Update timer display
        private void updateViewFromTimer(){
            timeElapsedView.setText(TimeTools.millisToTimeString(timer.getTimeElapsed()));
        }

        public void setRunningView(){
            // This will be true every time except for the first run, before we're paused for the first time
            // Change pause logo to play
            if (buttonRow.getChildAt(0) != pauseButton){
                buttonRow.removeView(playButton);
                buttonRow.addView(pauseButton, 0);
            }
            else{
                // Set onclick listeners for play/pause/stop

                pauseButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view){
                        pauseTimer();
                    }
                });

                playButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        resumeTimer();
                    }
                });

                stopButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        pauseTimer();

                        ft = getSupportFragmentManager().beginTransaction();

                        // Show stopWorkoutFragment
                        stopWorkoutFragment.show(ft, WorkoutStatic.STOP_DIALOG);
                    }
                });
            }

            viewState = WorkoutStatic.TIMER_RUNNING;
        }
    }

    @Override
    public void onWorkoutFinished(){
        // Close activity
        stopTimer();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseTimer();
    }

    @Override
    public void onStop(){
        super.onStop();
        stopTimer();
    }
}