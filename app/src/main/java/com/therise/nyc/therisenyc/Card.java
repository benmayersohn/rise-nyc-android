package com.therise.nyc.therisenyc;

import java.util.HashMap;

/**
 * Created by mayerzine on 1/25/17.
 *
 * Card from a deck of cards
 */

public class Card {


    private int id; // ID of drawable

    private String number; // number on card (or type for face card)
    private String suit; // suit of card
    private String name; // full name of card (for display)

    private boolean _isPictureCard;
    private boolean _isJoker;

    private static final Class DRAWABLE_CLASS = R.drawable.class;

    public static final String JACK = "jack";
    public static final String QUEEN = "queen";
    public static final String KING = "king";
    public static final String ACE = "ace";
    public static final String JOKER = "joker";
    public static final String TWO = "two";
    public static final String THREE = "three";
    public static final String FOUR = "four";
    public static final String FIVE = "five";
    public static final String SIX = "six";
    public static final String SEVEN = "seven";
    public static final String EIGHT = "eight";
    public static final String NINE = "nine";
    public static final String TEN = "ten";

    public static final String CLUBS = "clubs";
    public static final String DIAMONDS = "diamonds";
    public static final String SPADES = "spades";
    public static final String HEARTS = "hearts";

    public static final String RED = "red";
    public static final String BLACK = "black";

    private static final String JOKER_ID_STRING = "_joker";
    private static final String JOKER_NAME_STRING = " Joker";

    private static final String OF_DISPLAY = " Of ";
    private static final String OF_ID = "_of_";

    // Create static map from strings to resource ID
    private static final HashMap<String, Integer> resourceMap = createMap();
    public static HashMap<String,Integer> createMap (){
        HashMap<String,Integer> map = new HashMap<>();

        // Add all values
        map.put(ACE + OF_ID + CLUBS, R.drawable.ace_of_clubs); map.put(ACE + OF_ID + HEARTS, R.drawable.ace_of_hearts);
        map.put(ACE + OF_ID + DIAMONDS, R.drawable.ace_of_diamonds); map.put(ACE + OF_ID + SPADES, R.drawable.ace_of_spades);

        map.put(TWO + OF_ID + CLUBS, R.drawable.two_of_clubs); map.put(TWO + OF_ID + HEARTS, R.drawable.ace_of_hearts);
        map.put(TWO + OF_ID + DIAMONDS, R.drawable.ace_of_diamonds); map.put(TWO + OF_ID + SPADES, R.drawable.ace_of_spades);

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


    // For all cards except jokers
    public Card(String number, String suit){
        this.number = number;
        this.suit = suit;

        // Do we have a picture card?
        if (number.equals(JACK) || number.equals(QUEEN) || number.equals(KING) || number.equals(ACE)){
            _isPictureCard = true;
            name = firstUpperCase(number) + OF_DISPLAY + firstUpperCase(suit);
        }
        else{
            _isPictureCard = false;
            name = number + OF_DISPLAY + firstUpperCase(suit);
        }

        // Get ID from resourceMap
        id = resourceMap.get(name + OF_ID + suit);

        // Not a joker if we're using this constructor
        _isJoker = false;

    }

    // For jokers
    public Card(String jokerColor){
        id = resourceMap.get(jokerColor + JOKER_ID_STRING);
        name = firstUpperCase(jokerColor) + JOKER_NAME_STRING;
        number = JOKER; // The type is a joker
        suit = jokerColor; // Call the suit the color of the joker
        _isJoker = true; // this is a joker
    }

    // Take lowercase word and make first letter upper case
    public static String firstUpperCase(String lowercase){
        return lowercase.substring(0,1).toUpperCase() + lowercase.substring(1);
    }

    // Getters and setters
    public String getNumber(){
        return number;
    }

    public String getName(){
        return name;
    }

    public String getSuit(){
        return suit;
    }

    public boolean isPictureCard(){
        return _isPictureCard;
    }

    public boolean isJoker(){
        return _isJoker;
    }

    public int getId(){
        return id;
    }

}
