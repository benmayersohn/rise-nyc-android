package com.therise.nyc.therisenyc;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

/**
 * Dice roll game
 * Roll a die, get roll number and the exercise associated with it
 */

public class DiceActivity extends NumberedActivity {

    // Stores the results of our Dice Game
    private DiceGame diceGame;
    private TextView diceExercise;
    private TextView rollNumber;
    private ImageView dieImage;

    private ImageView rightArrow;

    // Create view updater class
    private class DiceActivityViewUpdater extends NumberedActivityViewUpdater{

        DiceActivityViewUpdater(Context context,
                                ImageButton pauseButton,
                                ImageButton playButton,
                                ImageButton stopButton,
                                LinearLayout buttonRow,
                                TextView timeElapsedView)
        {
            super(context, pauseButton, playButton, stopButton, buttonRow, timeElapsedView);
        }

        @Override
        public void setPausedView(){
            super.setPausedView();

            // Disable left/right click
            rightArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_skip_next_grey_24dp));
            rightArrow.setEnabled(false);
        }

        @Override
        public void setRunningView(){
            super.setRunningView();

            // Enable right click
            rightArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_skip_next_blue_24dp));
            rightArrow.setEnabled(true);
        }

        void showRollAnimation(){
            // Create animator
            dieImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dice_animation));
            dieImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dice_animation));

            // Set to animation, set a delay, then show the die
            AnimationDrawable frameAnimation = (AnimationDrawable) dieImage.getDrawable();

            // Add images cycling through die
            for (int i = 0; i < DiceStatic.NUM_ANIMATION_CYCLES; i++){
                frameAnimation.addFrame(ContextCompat.getDrawable(context, DiceStatic.DIE_IMAGES[new Random().nextInt(DiceStatic.NUM_EXERCISES)]),
                        DiceStatic.DICE_ANIMATE_INTERVAL);
            }

            frameAnimation.start();
        }

        void setNewRollView(){
            dieImage.setImageDrawable(ContextCompat.getDrawable(context, DiceStatic.DIE_IMAGES[diceGame.getPosition()]));
            diceExercise.setText(diceGame.rollToExercise());
            rollNumber.setText(diceGame.rollNumberString());
        }
    }

    // our rolling method! does it all
    void roll(){
        diceGame.rollDie();

        // play audio
        audioController.playSample(R.raw.dice_roll);

        // Display animation and display new result
        viewHandler.post(new Runnable(){
            @Override
            public void run(){
                ((DiceActivityViewUpdater)viewUpdater).showRollAnimation(); // first animate roll, then show result
                ((DiceActivityViewUpdater)viewUpdater).setNewRollView();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_during);

        LinearLayout buttonRow  = (LinearLayout) findViewById(R.id.row3);
        TextView timeElapsedView = (TextView) findViewById(R.id.time_elapsed);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stop_button);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        ImageButton playButton = new ImageButton(this);

        viewHandler = new Handler(Looper.getMainLooper());
        audioController = new AudioController(this);

        viewUpdater = new DiceActivityViewUpdater(this, pauseButton, playButton, stopButton, buttonRow, timeElapsedView);
        timerRepeater = new SimpleTimerRepeater();

        // Create relevant dialog fragments
        stopWorkoutFragment = StopWorkoutDialogFragment.newInstance(DiceStatic.WORKOUT_TYPE);

        dieImage = (ImageView) findViewById(R.id.die_image);

        // Now set up the buttons, on-click listeners, etc.
        diceExercise = (TextView) findViewById(R.id.dice_exercise);
        rollNumber = (TextView) findViewById(R.id.roll_number);

        rightArrow = (ImageView) findViewById(R.id.right_arrow);

        diceGame = new DiceGame(getIntent().getStringArrayListExtra(WorkoutStatic.CHOSEN_EXERCISES));

        // Initialize views
        diceExercise = (TextView) findViewById(R.id.dice_exercise);
        rightArrow = (ImageView) findViewById(R.id.right_arrow);

        // Draw initial card and update view
        roll();

        // Set on click listener for roll button (this will include animation)
        rightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            roll();
            }
        });

        // start timer
        startTimer();

    }
}
