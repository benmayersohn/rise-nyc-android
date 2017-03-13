package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/22/17.
 */

// Essentially a list of presets
// Defined for any class that implements the Preset interface
// Implements generics so we can define preset holders for any type of preset

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PresetHolder<T>  {

    private List<T> presets;

    public PresetHolder(){
        presets = new ArrayList<>();
    }

    @Override
    public boolean equals(Object allPresets){
        if (allPresets == this){
            return true;
        }

        if (!(allPresets instanceof PresetHolder)){
            return false;
        }

        PresetHolder allPresets2 = (PresetHolder) allPresets;

        // Make sure number of presets equal
        if (allPresets2.getNumPresets() != this.getNumPresets()){
            return false;
        }

        // Compare as we go
        for (int i =0; i < this.getNumPresets(); i++){
            if (!(allPresets2.containsPreset((Preset)getPreset(i)))){
                return false;
            }
        }

        return true;
    }


    public T getPreset(int position){
        return presets.get(position);
    }


    public T getPreset(String name){
        return getPreset(indexOf(name));
    }

    public void setPreset(int position, T preset){
        presets.set(position, preset);
    }

    public int getNumPresets(){
        return presets.size();
    }

    public ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < getNumPresets(); i++){
            names.add(((Preset)presets.get(i)).getName());
        }

        return names;
    }

    public void addPreset(int position, T preset) {

        presets.add(position, preset);

    }

    public void addPreset(T preset) {

        presets.add(preset);

    }

    // Assume we're dealing with numbered presets and sort
    public void sort(){
        Collections.sort((List<Preset>)presets);
    }

    // remove preset by position

    public int indexOf(String presetName){
        return getNames().indexOf(presetName);
    }

    // remove preset by object reference
    public void removePreset(Preset preset) {
        presets.remove(preset);
    }

    // uses equals as its basis
    public boolean containsPreset(Preset preset){
        return presets.contains(preset);
    }

}
