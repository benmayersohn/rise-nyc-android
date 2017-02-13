package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/21/17.
 */

import com.google.gson.annotations.SerializedName;

// Preset class for timer, to be converted from JSON file
public class TimerPreset {

    @SerializedName("preset_name")
    public String presetName;

    @SerializedName("num_reps")
    public int numReps;

    @SerializedName("num_sets")
    public int numSets;

    @SerializedName("num_work_mins")
    public int numWorkMins;

    @SerializedName("num_work_secs_tens")
    public int numWorkSecsTens;

    @SerializedName("num_work_secs_ones")
    public int numWorkSecsOnes;

    @SerializedName("num_rest_mins")
    public int numRestMins;

    @SerializedName("num_rest_secs_tens")
    public int numRestSecsTens;

    @SerializedName("num_rest_secs_ones")
    public int numRestSecsOnes;

    @SerializedName("num_prep_mins")
    public int numPrepMins;

    @SerializedName("num_prep_secs_tens")
    public int numPrepSecsTens;

    @SerializedName("num_prep_secs_ones")
    public int numPrepSecsOnes;

    @SerializedName("num_break_mins")
    public int numBreakMins;

    @SerializedName("num_break_secs_tens")
    public int numBreakSecsTens;

    @SerializedName("num_break_secs_ones")
    public int numBreakSecsOnes;

    // Construct preset with all properties
    public TimerPreset(String name, int numSets, int numReps,
                       int numWorkMins, int numWorkSecsTens, int numWorkSecsOnes,
                       int numRestMins, int numRestSecsTens, int numRestSecsOnes,
                       int numPrepMins, int numPrepSecsTens, int numPrepSecsOnes,
                       int numBreakMins, int numBreakSecsTens, int numBreakSecsOnes){
        presetName = name;
        this.numSets = numSets;
        this.numReps = numReps;
        this.numWorkMins = numWorkMins;
        this.numWorkSecsOnes = numWorkSecsOnes;
        this.numWorkSecsTens = numWorkSecsTens;
        this.numRestMins = numRestMins;
        this.numRestSecsOnes = numRestSecsOnes;
        this.numRestSecsTens = numRestSecsTens;
        this.numPrepMins = numPrepMins;
        this.numPrepSecsOnes = numPrepSecsOnes;
        this.numPrepSecsTens = numPrepSecsTens;
        this.numBreakMins = numBreakMins;
        this.numBreakSecsOnes = numBreakSecsOnes;
        this.numBreakSecsTens = numBreakSecsTens;
    }

    // Construct preset with just name
    // make everything 0 by default
    public TimerPreset(String name){
        presetName = name;
        this.numSets = 2;
        this.numReps = 1;
        this.numWorkMins = 0;
        this.numWorkSecsOnes = 5;
        this.numWorkSecsTens = 0;
        this.numRestMins = 0;
        this.numRestSecsOnes = 5;
        this.numRestSecsTens = 0;
        this.numPrepMins = 0;
        this.numPrepSecsOnes = 5;
        this.numPrepSecsTens = 0;
        this.numBreakMins = 0;
        this.numBreakSecsOnes = 5;
        this.numBreakSecsTens = 0;

    }

    // Over-ride equals method
    // If they have the same name, they are equal, bc names should be unique

    @Override
    public boolean equals(Object preset) {
        if (preset == this){
            return true;
        }
        if (!(preset instanceof TimerPreset)){
            return false;
        }
        TimerPreset preset2 = (TimerPreset) preset;

        // Two presets are equal if they share a name
        // We can have duplicate presets with different names if people want
        return (preset2.getName().equals(this.getName()));
    }

    // Getters and setters
    public void setName(String name){
        presetName = name;
    }

    public void setNumReps(int numReps){
        this.numReps = numReps;
    }

    public void setNumSets(int numSets){
        this.numSets = numSets;
    }

    public void setNumWorkMins(int numWorkMins){
        this.numWorkMins = numWorkMins;
    }

    public void setNumWorkSecsTens(int numWorkSecsTens){
        this.numWorkSecsTens = numWorkSecsTens;
    }

    public void setNumWorkSecsOnes(int numWorkSecsOnes){
        this.numWorkSecsOnes = numWorkSecsOnes;
    }

    public void setNumRestMins(int numRestMins){
        this.numRestMins = numRestMins;
    }

    public void setNumRestSecsTens(int numRestSecsTens){
        this.numRestSecsTens = numRestSecsTens;
    }

    public void setNumRestSecsOnes(int numRestSecsOnes){
        this.numRestSecsOnes = numRestSecsOnes;
    }

    public void setNumPrepMins(int numPrepMins){
        this.numPrepMins = numPrepMins;
    }

    public void setNumPrepSecsTens(int numPrepSecsTens){
        this.numPrepSecsTens = numPrepSecsTens;
    }

    public void setNumPrepSecsOnes(int numPrepSecsOnes){
        this.numPrepSecsOnes = numPrepSecsOnes;
    }

    public void setNumBreakMins(int numBreakMins){
        this.numBreakMins = numBreakMins;
    }

    public void setNumBreakSecsTens(int numBreakSecsTens){
        this.numBreakSecsTens = numBreakSecsTens;
    }

    public void setNumBreakSecsOnes(int numBreakSecsOnes){
        this.numBreakSecsOnes = numBreakSecsOnes;
    }


    public String getName(){
        return presetName;
    }

    public int getNumReps(){
        return numReps;
    }

    public int getNumSets(){
        return numSets;
    }

    public int getNumWorkMins(){
        return numWorkMins;
    }

    public int getNumWorkSecsTens(){
        return numWorkSecsTens;
    }

    public int getNumWorkSecsOnes(){
        return numWorkSecsOnes;
    }

    public int getNumRestMins(){
        return numRestMins;
    }

    public int getNumRestSecsTens(){
        return numRestSecsTens;
    }

    public int getNumRestSecsOnes(){
        return numRestSecsOnes;
    }

    public int getNumPrepMins(){
        return numPrepMins;
    }

    public int getNumPrepSecsTens(){
        return numPrepSecsTens;
    }

    public int getNumPrepSecsOnes(){
        return numPrepSecsOnes;
    }

    public int getNumBreakMins(){
        return numBreakMins;
    }

    public int getNumBreakSecsTens(){
        return numBreakSecsTens;
    }

    public int getNumBreakSecsOnes(){
        return numBreakSecsOnes;
    }


}
