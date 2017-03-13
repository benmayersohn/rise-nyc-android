package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 3/10/17.
 */

import com.google.gson.annotations.SerializedName;

public class Exercise implements Preset{

    // Not doing much with this class so far

    @SerializedName("name")
    String name; // name of exercise; the only thing we're using so far

    private static final String BURPEES = "BURPEES";

    public Exercise(String name){
        this.name = name;
    }

    // Include Burpees if nothing there :)
    public Exercise(){
        name = BURPEES;
    }

    // Getters and setters
    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return name;
    }



}
