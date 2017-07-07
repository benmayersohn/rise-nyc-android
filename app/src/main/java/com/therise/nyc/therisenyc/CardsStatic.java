package com.therise.nyc.therisenyc;

import java.util.HashMap;

/**
 * Created by mayerzine on 6/24/17.
 */


// Static variables for cards
public class CardsStatic {

    // four suits
    public static final int NUM_EXERCISES = 4;

    public static final String PRESET_FILE = "cards_presets.json";

    public static final int REQUEST_CODE = 3;

    public static final String WORKOUT_TYPE = "CARDS";

    // names
    public static final String JACK = "jack";
    public static final String QUEEN = "queen";
    public static final String KING = "king";
    public static final String ACE = "ace";
    public static final String TWO = "two";
    public static final String THREE = "three";
    public static final String FOUR = "four";
    public static final String FIVE = "five";
    public static final String SIX = "six";
    public static final String SEVEN = "seven";
    public static final String EIGHT = "eight";
    public static final String NINE = "nine";
    public static final String TEN = "ten";

    public static final int[] CARD_NUMBERS = {2,3,4,5,6,7,8,9,10,11,12,13,14};

    public static final String HAS_JOKERS = "HAS_JOKERS";

    public static final int HEARTS_INDEX = 0;
    public static final int CLUBS_INDEX = 1;
    public static final int DIAMONDS_INDEX = 2;
    public static final int SPADES_INDEX = 3;

    public static final int JACK_VALUE = 11;
    public static final int QUEEN_VALUE = 12;
    public static final int KING_VALUE = 13;
    public static final int ACE_VALUE_ONE = 14;
    public static final int ACE_VALUE_TWO = 1;
    public static final int JOKER_VALUE = 0;

    public static final int TWO_VALUE = 2;
    public static final int THREE_VALUE = 3;
    public static final int FOUR_VALUE = 4;
    public static final int FIVE_VALUE = 5;
    public static final int SIX_VALUE = 6;
    public static final int SEVEN_VALUE = 7;
    public static final int EIGHT_VALUE = 8;
    public static final int NINE_VALUE = 9;
    public static final int TEN_VALUE = 10;

    public static final String CLUBS = "clubs";
    public static final String DIAMONDS = "diamonds";
    public static final String SPADES = "spades";
    public static final String HEARTS = "hearts";

    public static final String RED = "red";
    public static final String BLACK = "black";

    public static final String JOKER_ID_STRING = "_joker";
    public static final String JOKER_NAME_STRING = " Joker";
    public static final String JOKER = "JOKER";

    public static final String OF_DISPLAY = " Of ";
    public static final String OF_ID = "_of_";

    public static final String[] SUITS = {SPADES, HEARTS, DIAMONDS, CLUBS};
    public static final String[] JOKERS = {RED, BLACK};

    // Create static map from strings to resource ID
    public static final HashMap<String, Integer> RESOURCE_MAP = createResourceMap();
    public static final HashMap<Integer, String> NUMBER_MAP = createNumberMap();

    public static HashMap<Integer,String> createNumberMap (){
        HashMap<Integer,String> map = new HashMap<>();

        map.put(TWO_VALUE,TWO);
        map.put(THREE_VALUE,THREE);
        map.put(FOUR_VALUE,FOUR);
        map.put(FIVE_VALUE,FIVE);
        map.put(SIX_VALUE,SIX);
        map.put(SEVEN_VALUE,SEVEN);
        map.put(EIGHT_VALUE,EIGHT);
        map.put(NINE_VALUE,NINE);
        map.put(TEN_VALUE,TEN);
        map.put(JACK_VALUE,JACK);
        map.put(QUEEN_VALUE,QUEEN);
        map.put(KING_VALUE,KING);
        map.put(ACE_VALUE_ONE,ACE);
        map.put(ACE_VALUE_TWO,ACE);

        return map;
    }

    private static HashMap<String,Integer> createResourceMap (){
        HashMap<String,Integer> map = new HashMap<>();

        // Add all values
        map.put(ACE + OF_ID + CLUBS, R.drawable.ace_of_clubs); map.put(ACE + OF_ID + HEARTS, R.drawable.ace_of_hearts);
        map.put(ACE + OF_ID + DIAMONDS, R.drawable.ace_of_diamonds); map.put(ACE + OF_ID + SPADES, R.drawable.ace_of_spades);

        map.put(TWO + OF_ID + CLUBS, R.drawable.two_of_clubs); map.put(TWO + OF_ID + HEARTS, R.drawable.two_of_hearts);
        map.put(TWO + OF_ID + DIAMONDS, R.drawable.two_of_diamonds); map.put(TWO + OF_ID + SPADES, R.drawable.two_of_spades);

        map.put(THREE + OF_ID + CLUBS, R.drawable.three_of_clubs); map.put(THREE + OF_ID + HEARTS, R.drawable.three_of_hearts);
        map.put(THREE + OF_ID + DIAMONDS, R.drawable.three_of_diamonds); map.put(THREE + OF_ID + SPADES, R.drawable.three_of_spades);

        map.put(FOUR + OF_ID + CLUBS, R.drawable.four_of_clubs); map.put(FOUR + OF_ID + HEARTS, R.drawable.four_of_hearts);
        map.put(FOUR + OF_ID + DIAMONDS, R.drawable.four_of_diamonds); map.put(FOUR + OF_ID + SPADES, R.drawable.four_of_spades);

        map.put(FIVE + OF_ID + CLUBS, R.drawable.five_of_clubs); map.put(FIVE + OF_ID + HEARTS, R.drawable.five_of_hearts);
        map.put(FIVE + OF_ID + DIAMONDS, R.drawable.five_of_diamonds); map.put(FIVE + OF_ID + SPADES, R.drawable.five_of_spades);

        map.put(SIX + OF_ID + CLUBS, R.drawable.six_of_clubs); map.put(SIX + OF_ID + HEARTS, R.drawable.six_of_hearts);
        map.put(SIX + OF_ID + DIAMONDS, R.drawable.six_of_diamonds); map.put(SIX + OF_ID + SPADES, R.drawable.six_of_spades);

        map.put(SEVEN + OF_ID + CLUBS, R.drawable.seven_of_clubs); map.put(SEVEN + OF_ID + HEARTS, R.drawable.seven_of_hearts);
        map.put(SEVEN + OF_ID + DIAMONDS, R.drawable.seven_of_diamonds); map.put(SEVEN + OF_ID + SPADES, R.drawable.seven_of_spades);

        map.put(EIGHT + OF_ID + CLUBS, R.drawable.eight_of_clubs); map.put(EIGHT + OF_ID + HEARTS, R.drawable.eight_of_hearts);
        map.put(EIGHT + OF_ID + DIAMONDS, R.drawable.eight_of_diamonds); map.put(EIGHT + OF_ID + SPADES, R.drawable.eight_of_spades);

        map.put(NINE + OF_ID + CLUBS, R.drawable.nine_of_clubs); map.put(NINE + OF_ID + HEARTS, R.drawable.nine_of_hearts);
        map.put(NINE + OF_ID + DIAMONDS, R.drawable.nine_of_diamonds); map.put(NINE + OF_ID + SPADES, R.drawable.nine_of_spades);

        map.put(TEN + OF_ID + CLUBS, R.drawable.ten_of_clubs); map.put(TEN + OF_ID + HEARTS, R.drawable.ten_of_hearts);
        map.put(TEN + OF_ID + DIAMONDS, R.drawable.ten_of_diamonds); map.put(TEN + OF_ID + SPADES, R.drawable.ten_of_spades);

        map.put(JACK + OF_ID + CLUBS, R.drawable.jack_of_clubs); map.put(JACK + OF_ID + HEARTS, R.drawable.jack_of_hearts);
        map.put(JACK + OF_ID + DIAMONDS, R.drawable.jack_of_diamonds); map.put(JACK + OF_ID + SPADES, R.drawable.jack_of_spades);

        map.put(QUEEN + OF_ID + CLUBS, R.drawable.queen_of_clubs); map.put(QUEEN + OF_ID + HEARTS, R.drawable.queen_of_hearts);
        map.put(QUEEN + OF_ID + DIAMONDS, R.drawable.queen_of_diamonds); map.put(QUEEN + OF_ID + SPADES, R.drawable.queen_of_spades);

        map.put(KING + OF_ID + CLUBS, R.drawable.king_of_clubs); map.put(KING + OF_ID + HEARTS, R.drawable.king_of_hearts);
        map.put(KING + OF_ID + DIAMONDS, R.drawable.king_of_diamonds); map.put(KING + OF_ID + SPADES, R.drawable.king_of_spades);

        map.put(RED + JOKER_ID_STRING, R.drawable.red_joker);
        map.put(BLACK + JOKER_ID_STRING, R.drawable.black_joker);

        return map;
    }
}
