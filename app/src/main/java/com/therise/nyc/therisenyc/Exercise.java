package com.therise.nyc.therisenyc;

/**
 * Exercise = a preset with just one exercise.
 * It's basically just a concrete extension of the Preset class.
 */

class Exercise extends Preset{

    Exercise(String name){
        presetName = name;
    }

    // Getters and setters
    public void setName(String name){
        this.presetName = name;
    }

}
