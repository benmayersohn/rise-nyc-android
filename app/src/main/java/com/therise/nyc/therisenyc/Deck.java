package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/25/17.
 */

import java.util.List;
import java.util.ArrayList;

public class Deck {
    private List<Card> cards;

    private static final String[] cardNumbers = {Card.TWO,Card.THREE, Card.FOUR, Card.FIVE, Card.SIX,
            Card.SEVEN,Card.EIGHT,Card.NINE,Card.TEN, Card.JACK, Card.QUEEN, Card.KING, Card.ACE};

    private static final String[] suits = {Card.SPADES, Card.HEARTS, Card.DIAMONDS, Card.CLUBS};

    private static final String[] jokers = {Card.RED, Card.BLACK};

    // Empty deck, default constructor
    // We can use this first the pile of cards we create as we deal
    public Deck(){
        cards = new ArrayList<>();
    }

    // We can initialize full deck if we insert a boolean
    // True for a deck with two jokers, false without
    public Deck(boolean hasJokers){

        // Construct deck without jokers first
        for (String number : cardNumbers){
            for (String suit : suits){
                cards.add(new Card(number,suit));
            }
        }

        // Add jokers if we got 'em
        if (hasJokers){
            for (String color: jokers){
                cards.add(new Card(color));
            }

        }
    }

    // Get card at position
    public Card getCardAt(int position){
        return cards.get(position);
    }

    // Remove card at position
    public void removeCardAt(int position){
        cards.remove(position);
    }

    // Remove card specified
    public void removeCard(Card card){
        cards.remove(card);
    }

    // Add card to end of deck

    public void addCard(Card card){
        cards.add(card);
    }

}
