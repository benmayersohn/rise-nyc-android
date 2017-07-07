package com.therise.nyc.therisenyc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// CardGame: stores variables relevant to the deck of cards game
// Cards dealt, cards left, the current position, and total number of cards

class CardGame {

    // Create two decks: dealt and future
    private Deck dealtCards;
    private Deck futureCards;
    private int numCards;
    private int position; // our position in the deck; initially -1, then from 0 through numCards-1

    private List<String> chosenExercises;

    // Random number generator
    Random r;

    CardGame(List<String> chosenExercises, boolean hasJokers){

        this.chosenExercises = new ArrayList<>();
        for (int i = 0; i < chosenExercises.size(); i++){
            this.chosenExercises.add(chosenExercises.get(i));
        }

        futureCards = new Deck(hasJokers); // full deck
        numCards = futureCards.getNumCards();
        dealtCards = new Deck(); // empty deck
        position = -1; // we haven't drawn a card yet
        r = new Random();
    }

    String currentCardToExercise(){
        Card c = getCurrentCard();
        if (c.isJoker()){
            return CardsStatic.JOKER;
        }

        String suit = c.getSuit();
        String chosenExercise = WorkoutStatic.BLANK;
        int number = c.getNumber();

        String outputString;

        switch (suit){
            case CardsStatic.HEARTS:
                chosenExercise = chosenExercises.get(CardsStatic.HEARTS_INDEX);
                break;
            case CardsStatic.CLUBS:
                chosenExercise = chosenExercises.get(CardsStatic.CLUBS_INDEX);
                break;
            case CardsStatic.SPADES:
                chosenExercise = chosenExercises.get(CardsStatic.SPADES_INDEX);
                break;
            case CardsStatic.DIAMONDS:
                chosenExercise = chosenExercises.get(CardsStatic.DIAMONDS_INDEX);
                break;
        }

        outputString = String.valueOf(number + WorkoutStatic.SPACE_DELIM + chosenExercise);

        return outputString;
    }

    // Draw card from the deck
    private void drawCard(){
        // Pick a random number between 0 and numCardsRemaining-1
        int numCardsRemaining = futureCards.getNumCards();

        int nextInd = r.nextInt(numCardsRemaining);
        Card drawnCard = futureCards.getCardAt(nextInd);

        // Add drawn card to drawn deck
        dealtCards.addCard(drawnCard);

        // Remove from other deck
        futureCards.removeCard(drawnCard);

        position++;
    }

    private int getCurrentCardNumber(){
        return position+1;
    }

    String currentCardNumberString(){
        return getCurrentCardNumber() + WorkoutStatic.SLASH_DELIM + getNumCards();
    }

    void goToPreviousCard(){
        position--;
    }

    void goToNextCard(){
        if (position == dealtCards.getNumCards()-1){
            drawCard();
        }
        else{
            position++;
        }
    }

    Card getCurrentCard(){
        return dealtCards.getCardAt(position);
    }

    boolean isLastCard(){
        return position == numCards-1;
    }

    boolean isFirstCard(){
        return position == 0;
    }

    private int getNumCards(){
        return numCards;
    }
}
