package com.therise.nyc.therisenyc;

import com.google.gson.annotations.SerializedName;

/**
 * Preset class. All other presets (timer, cards, dice) derive from this
 */

// A preset at minimum has a getName() method
abstract class Preset implements Comparable<Preset>{

    @SerializedName("preset_name")
    String presetName;

    String getName(){return presetName;}

    @Override
    public int compareTo(Preset p2){
        return this.getName().compareTo(p2.getName());
    }
}
