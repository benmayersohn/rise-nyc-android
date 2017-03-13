package com.therise.nyc.therisenyc;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by mayerzine on 3/11/17.
 */

// Both cards and dice involve "numbered presets", because they're essentially the same thing
// Exercises are assigned to a suit or number, there are "n" total in different positions

public class NumberedPreset implements Preset {

    @SerializedName("preset_name")
    private String presetName;

    @SerializedName("exercises")
    private ArrayList<String> exercises;

    private int numExercises;

    public static final String BLANK = "";
    public static final String BLANK_NAME = "EMPTY";

    public NumberedPreset(String presetName, ArrayList<String> exercises){
        this.presetName = presetName;
        this.exercises = exercises;
        numExercises = exercises.size();
    }

    // Default empty test preset
    public NumberedPreset(int numExercises){
        exercises = new ArrayList<>();
        this.numExercises = numExercises;

        // Add empty entries
        for (int i =0; i < numExercises; i++) {
            exercises.add(BLANK);
        }

        presetName = BLANK_NAME;
    }

    public String getName(){
        return presetName;
    }

    // Over-ride equals method
    // If they have the same name, they are equal, bc names should be unique

    @Override
    public boolean equals(Object preset) {
        if (preset == this){
            return true;
        }
        if (!(preset instanceof NumberedPreset)){
            return false;
        }
        NumberedPreset preset2 = (NumberedPreset) preset;

        // Two presets are equal if they share a name
        // We can have duplicate presets with different names if people want
        return (preset2.getName().equals(this.getName()));
    }

    // Getters and setters
    public void setName(String name){
        presetName = name;
    }

    public void setExercise(int index, String exercise){
        exercises.set(index,exercise);
    }

    public String getExercise(int index){
        return exercises.get(index);
    }

    public int getNumExercises(){
        return numExercises;
    }


}
