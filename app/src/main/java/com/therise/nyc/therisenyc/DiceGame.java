package com.therise.nyc.therisenyc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// DiceGame: stores variables relevant to rolling a die
// Basically just the act of rolling, and the current roll number

class DiceGame {

    private int position; // the last side we rolled (ranges from 0 through 5)
    private int rollNumber; // our current roll number

    // Chosen exercises
    private List<String> chosenExercises;

    String rollToExercise(){
        return chosenExercises.get(position);
    }

    String rollNumberString(){
        return DiceStatic.ROLL_STRING + String.valueOf(rollNumber);
    }

    // Random number generator
    private Random r;

    DiceGame(List<String> chosenExercises){
        this.chosenExercises = new ArrayList<>();
        for (int i = 0; i < chosenExercises.size(); i++){
            this.chosenExercises.add(chosenExercises.get(i));
        }

        rollNumber = 0;

        r = new Random();
    }

    void rollDie(){
        position = r.nextInt(DiceStatic.NUM_EXERCISES);
        rollNumber++;
    }

    int getPosition(){return position;}

}
