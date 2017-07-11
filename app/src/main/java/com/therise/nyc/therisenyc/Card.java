package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/25/17.
 *
 * Card from a deck of cards
 */

class Card {


    private int id; // ID of drawable

    private int number; // number on card
    private String suit; // suit of card
    private String name; // full name of card (for display)
    private boolean isJoker;

    // For all cards except jokers
    Card(int number, String suit){
        this.number = number;
        this.suit = suit;

        // Do we have a picture card?
        if (number == CardsStatic.JACK_VALUE){
            name = firstUpperCase(CardsStatic.JACK) + CardsStatic.OF_DISPLAY + firstUpperCase(suit);
        }
        else if (number == CardsStatic.QUEEN_VALUE){
            name = firstUpperCase(CardsStatic.QUEEN) + CardsStatic.OF_DISPLAY + firstUpperCase(suit);
        }
        else if (number == CardsStatic.KING_VALUE){
            name = firstUpperCase(CardsStatic.KING) + CardsStatic.OF_DISPLAY + firstUpperCase(suit);
        }
        else if (number == CardsStatic.ACE_VALUE_ONE || number == CardsStatic.ACE_VALUE_TWO){
            name = firstUpperCase(CardsStatic.ACE) + CardsStatic.OF_DISPLAY + firstUpperCase(suit);
        }
        else{
            name = number + CardsStatic.OF_DISPLAY + firstUpperCase(suit);
        }

        // Get ID from resourceMap
        id = CardsStatic.RESOURCE_MAP.get(CardsStatic.NUMBER_MAP.get(number) + CardsStatic.OF_ID + suit);

        // Not a joker if we're using this constructor
        isJoker = false;

    }

    // Cards are equal if they have the same name
    @Override
    public boolean equals(Object c) {
        if (c == this){
            return true;
        }
        if (!(c instanceof Card)){
            return false;
        }
        Card c2 = (Card) c;

        return (c2.getName().equals(this.getName()));
    }

    // For jokers
    Card(String jokerColor){
        id = CardsStatic.RESOURCE_MAP.get(jokerColor + CardsStatic.JOKER_ID_STRING);
        name = firstUpperCase(jokerColor) + CardsStatic.JOKER_NAME_STRING;
        number = CardsStatic.JOKER_VALUE; // The type is a joker
        suit = jokerColor; // Call the suit the color of the joker
        isJoker = true; // this is a joker
    }

    // Take lowercase word and make first letter upper case
    private static String firstUpperCase(String lowercase){
        return lowercase.substring(0,1).toUpperCase() + lowercase.substring(1);
    }

    // Getters and setters
    public int getNumber(){
        return number;
    }

    public String getName(){
        return name;
    }

    String getSuit(){
        return suit;
    }

    boolean isJoker(){
        return isJoker;
    }

    public int getId(){
        return id;
    }

}
