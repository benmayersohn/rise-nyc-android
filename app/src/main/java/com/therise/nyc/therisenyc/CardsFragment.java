package com.therise.nyc.therisenyc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Scroller;

/**
 * Created by mayerzine on 1/14/17.
 *
 * Deck of Cards fragment.
 * Choose random cards from a deck of 54 (including jokers) or 52 (excluding).
 * We draw a random index, sort cards in an ArrayList (or deck, getCardAt(int position))
 * Option of assigning exercises to each suit.
 *
 * We can create a "drawn" pile and a "future" pile
 * Upon beginning, put first card into drawn pile and remove from future pile. We are at position 0.
 * Flipping forward puts next card in drawn pile. We are at position one.
 * When flipping backwards, go to corresponding position within drawn pile and use getCardAt()
 * Use a ViewFlipper to transition between card displays
 */

public class CardsFragment extends Fragment{

    private Deck cardsRemaining; // cards remaining in deck (starts with 52 or 54 cards)
    private Deck cardsDealt; // cards that have been dealt (starts empty)

    private static final String SPADES_EXERCISE = "SPADES_EXERCISE";
    private static final String HEARTS_EXERCISE = "HEARTS_EXERCISE";
    private static final String DIAMONDS_EXERCISE = "DIAMONDS_EXERCISE";
    private static final String CLUBS_EXERCISE = "CLUBS_EXERCISE";

    // Buttons
    private CheckBox jokerBox; // do we want jokers enabled?
    private Scroller exerciseList; // list of stored exercises


    public CardsFragment() {};

    // We get the name of the tab and the view from the activity
    public static CardsFragment newInstance() {
        CardsFragment fragment = new CardsFragment();
        return fragment;
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
        View layout = inflater.inflate(R.layout.fragment_cards_initial, container, false);

        // This is the initial view, before we press start


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
