package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/22/17.
 */

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.ArrayList;

public class AllTimerPresets {

    @SerializedName("timer_presets")
    ArrayList<TimerPreset> timerPresets;

    public AllTimerPresets(){
        timerPresets = new ArrayList<>();
    }

    @Override
    public boolean equals(Object allPresets){
        if (allPresets == this){
            return true;
        }

        if (!(allPresets instanceof AllTimerPresets)){
            return false;
        }

        AllTimerPresets allPresets2 = (AllTimerPresets) allPresets;

        // Make sure number of presets equal
        if (allPresets2.getNumPresets() != this.getNumPresets()){
            return false;
        }

        // Compare as we go
        for (int i =0; i < this.getNumPresets(); i++){
            if (!(allPresets2.containsPreset(this.getPreset(i)))){
                return false;
            }
        }

        return true;
    }

    public TimerPreset getPreset(int position){
        return timerPresets.get(position);
    }

    public TimerPreset getPreset(String name){
        return getPreset(indexOf(name));
    }

    public void setPreset(int position, TimerPreset preset){
        timerPresets.set(position, preset);
    }

    public int getNumPresets(){
        return timerPresets.size();
    }

    public ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < getNumPresets(); i++){
            names.add(timerPresets.get(i).getName());
        }

        return names;
    }

    public void addPreset(int position, TimerPreset preset) {
        timerPresets.add(position, preset);
    }

    public void addPreset(TimerPreset preset) {
        timerPresets.add(preset);
    }

    // remove preset by position
    public void removePreset(int position) {
        timerPresets.remove(position);
    }

    public void removePreset(String name) {
        timerPresets.remove(getPreset(name));
    }

    public int indexOf(TimerPreset preset){
        return timerPresets.indexOf(preset);
    }

    public int indexOf(String presetName){
        return getNames().indexOf(presetName);
    }

    // remove preset by object reference
    public void removePreset(TimerPreset preset) {
        timerPresets.remove(preset);
    }

    // uses equals as its basis
    public boolean containsPreset(TimerPreset preset){
        return timerPresets.contains(preset);
    }

}
