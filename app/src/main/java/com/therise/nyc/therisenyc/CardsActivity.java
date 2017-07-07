package com.therise.nyc.therisenyc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Deck of Cards activity.
 * Choose random cards from a deck of 54 (including jokers) or 52 (excluding).
 * Each card has an exercise on it.
 */

public class CardsActivity extends NumberedActivity {

    // Stores the results of our DoC
    private CardGame cardGame;

    private TextView cardExercise;
    private TextView currentCard;

    protected ImageView leftArrow;
    protected ImageView rightArrow;

    View layout;

    // Create view updater class
    private class CardsActivityViewUpdater extends NumberedActivityViewUpdater{

        // Initially, can't go back, but can go forward
        Drawable leftArrowDrawable;
        Drawable rightArrowDrawable;
        LinearLayout background;

        CardsActivityViewUpdater(Context context,
                                 ImageButton pauseButton,
                                 ImageButton playButton,
                                 ImageButton stopButton,
                                 LinearLayout buttonRow,
                                 TextView timeElapsedView,
                                 LinearLayout background){
            super(context, pauseButton, playButton, stopButton, buttonRow, timeElapsedView);
            this.background = background;
        }

        @Override
        public void setPausedView(){
            super.setPausedView();

            // Disable left/right click
            leftArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_skip_previous_grey_24dp));
            rightArrow.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_skip_next_grey_24dp));

            leftArrow.setEnabled(false);
            rightArrow.setEnabled(false);
        }

        @Override
        public void setRunningView(){
            super.setRunningView();

            // Enable left/right click
            leftArrow.setImageDrawable(leftArrowDrawable);
            rightArrow.setImageDrawable(rightArrowDrawable);

            leftArrow.setEnabled(true);
            rightArrow.setEnabled(true);
        }

        void setNewCardView(){
            cardExercise.setText(cardGame.currentCardToExercise());
            currentCard.setText(cardGame.currentCardNumberString());
            background.setBackground(ContextCompat.getDrawable(context, cardGame.getCurrentCard().getId()));

            if (cardGame.isFirstCard()){
                leftArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_previous_grey_24dp);
                rightArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_next_blue_24dp);
            }
            else if (cardGame.isLastCard()){
                leftArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_previous_blue_24dp);
                rightArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_next_gold_24dp);
            }
            else {
                leftArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_previous_blue_24dp);
                rightArrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_skip_next_blue_24dp);
            }

            leftArrow.setImageDrawable(leftArrowDrawable);
            rightArrow.setImageDrawable(rightArrowDrawable);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        layout = View.inflate(this,R.layout.activity_cards_during,null);
        setContentView(R.layout.activity_cards_during);

        // Bottom row, where the play/stop/pause buttons are
        LinearLayout buttonRow  = (LinearLayout) findViewById(R.id.row3);
        ImageButton stopButton = (ImageButton) findViewById(R.id.stop_button);
        ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        ImageButton playButton = new ImageButton(this);
        TextView timeElapsedView = (TextView) findViewById(R.id.time_elapsed);
        LinearLayout background = (LinearLayout) findViewById(R.id.cards_layout_during);

        viewHandler = new Handler(Looper.getMainLooper());
        audioController = new AudioController(this);

        viewUpdater = new CardsActivityViewUpdater(this,pauseButton, playButton, stopButton, buttonRow, timeElapsedView, background);
        timerRepeater = new SimpleTimerRepeater();

        // Create relevant dialog fragments
        stopWorkoutFragment = StopWorkoutDialogFragment.newInstance(CardsStatic.WORKOUT_TYPE);
        finishWorkoutFragment = FinishWorkoutDialogFragment.newInstance(CardsStatic.WORKOUT_TYPE);

        cardGame = new CardGame(getIntent().getStringArrayListExtra(WorkoutStatic.CHOSEN_EXERCISES),
                getIntent().getBooleanExtra(CardsStatic.HAS_JOKERS,true));

        cardExercise = (TextView) findViewById(R.id.card_exercise);

        leftArrow = (ImageView) findViewById(R.id.left_arrow);
        rightArrow = (ImageView) findViewById(R.id.right_arrow);

        currentCard = (TextView) findViewById(R.id.current_card);

        // Draw initial card and set view
        cardGame.goToNextCard();
        viewHandler.post(new Runnable(){
            @Override
            public void run(){
                ((CardsActivityViewUpdater)viewUpdater).setNewCardView();
            }
        });

        // Set on click listeners now

        leftArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!cardGame.isFirstCard()) {
                    // draw card
                    cardGame.goToPreviousCard();

                    viewHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            ((CardsActivityViewUpdater)viewUpdater).setNewCardView();
                        }
                    });
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            if (!cardGame.isLastCard()) {
                // draw card
                cardGame.goToNextCard();

                viewHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        ((CardsActivityViewUpdater)viewUpdater).setNewCardView();
                    }
                });
            }

            // We're done if we click right on the last card!
            // Push a FinishWorkoutDialogFragment
            else {

                // stop timer
                stopTimer();

                // Show finishing fragment
                ft = getSupportFragmentManager().beginTransaction();

                // Create a FinishDialogFragment
                finishWorkoutFragment = FinishWorkoutDialogFragment.newInstance(CardsStatic.WORKOUT_TYPE);

                // Show the fragment
                finishWorkoutFragment.show(ft, WorkoutStatic.FINISH_DIALOG);
            }
            }
        });

        // start timer
        startTimer();
    }
}
