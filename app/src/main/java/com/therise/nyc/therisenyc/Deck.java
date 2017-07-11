package com.therise.nyc.therisenyc;

// Deck: Stores cards, enables retrieval/addition of cards

import java.util.List;
import java.util.ArrayList;

class Deck {
    private List<Card> cards;

    // Empty deck, default constructor
    // We can use this first the pile of cards we create as we deal
    Deck(){
        cards = new ArrayList<>();
    }

    // We can initialize full deck if we insert a boolean
    // True for a deck with two jokers, false without
    Deck(boolean hasJokers){

        cards = new ArrayList<>();

        // Construct deck without jokers first
        for (int number : CardsStatic.CARD_NUMBERS){
            for (String suit : CardsStatic.SUITS){
                cards.add(new Card(number,suit));
            }
        }

        // Add jokers if we got 'em
        if (hasJokers){
            for (String color: CardsStatic.JOKERS){
                cards.add(new Card(color));
            }

        }
    }

    // Get card at position
    Card getCardAt(int position){
        return cards.get(position);
    }

    // Remove card specified
    void removeCard(Card card){
        cards.remove(card);
    }

    int getNumCards(){
        return cards.size();
    }

    // Add card to end of deck

    void addCard(Card card){
        cards.add(card);
    }

}
