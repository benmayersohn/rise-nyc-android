package com.therise.nyc.therisenyc;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Both cards and dice involve "numbered presets", because they're essentially the same thing
// Exercises are assigned to a suit or number, there are "n" total in different positions

class NumberedPreset extends Preset {

    @SerializedName("exercises")
    private List<String> exercises;

    NumberedPreset(String presetName, List<String> exercises){
        this.presetName = presetName;
        this.exercises = exercises;
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

    String getExercise(int index){
        return exercises.get(index);
    }


}
