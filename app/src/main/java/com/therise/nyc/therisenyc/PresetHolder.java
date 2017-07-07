package com.therise.nyc.therisenyc;

// PresetHolder.java: Stores all the presets of a given type
// Basically just a collection of presets. See PresetManager.java for writing/reading to/from JSON files

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PresetHolder<T extends Preset> {

    @SerializedName("presets")
    private List<T> presets;

    PresetHolder(){
        presets = new ArrayList<>(); // create default
    }

    T getPreset(int position){
        return presets.get(position);
    }

    int getNumPresets(){
        return presets.size();
    }

    ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < getNumPresets(); i++){
            names.add(presets.get(i).getName());
        }

        return names;
    }

    void addPreset(T preset) {
        presets.add(preset);
    }

    void deletePreset(int index){
        presets.remove(index);
    }

    // sort by name
    void sort(){
        Collections.sort(presets);
    }

}
