package com.therise.nyc.therisenyc;

// Presets for the Rise Timer

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Preset class for rise timer, to be converted from JSON file

class RiseTimerPreset extends Preset{

    @SerializedName("num_reps")
    private Integer numReps;

    @SerializedName("num_sets")
    private Integer numSets;

    @SerializedName("time_mins")
    private List<Integer> timeMins;

    @SerializedName("time_secs_tens")
    private List<Integer> timeSecsTens;

    @SerializedName("time_secs_ones")
    private List<Integer> timeSecsOnes;

    // For JSON
    // Construct preset with all properties

    RiseTimerPreset(String name, int numSets, int numReps,
                    List<Integer> timeMins, List<Integer> timeSecsTens, List<Integer> timeSecsOnes){
        this.presetName = name;
        this.numSets = numSets;
        this.numReps = numReps;
        this.timeMins = timeMins;
        this.timeSecsTens = timeSecsTens;
        this.timeSecsOnes = timeSecsOnes;
    }

    // Over-ride equals method
    // If they have the same name, they are equal, bc names should be unique

    @Override
    public boolean equals(Object preset) {
        if (preset == this){
            return true;
        }
        if (!(preset instanceof RiseTimerPreset)){
            return false;
        }
        RiseTimerPreset preset2 = (RiseTimerPreset) preset;

        // Two presets are equal if they share a name
        // We can have duplicate presets with different names if people want
        return (preset2.getName().equals(this.getName()));
    }

    // Getters (no need to modify a preset, just create a new one... so no setters)

    int getNumReps(){
        return numReps;
    }

    int getNumSets(){
        return numSets;
    }

    int getTimeMins(int index){
        return timeMins.get(index);
    }

    int getTimeSecsTens(int index){
        return timeSecsTens.get(index);
    }

    int getTimeSecsOnes(int index){
        return timeSecsOnes.get(index);
    }

}
