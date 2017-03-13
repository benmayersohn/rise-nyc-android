package com.therise.nyc.therisenyc;

import java.util.ArrayList;

/**
 * Created by mayerzine on 3/10/17.
 */

public class CardsPreset extends NumberedPreset {

    // four suits
    private static final int numExercises = 4;

    // Construct preset with all properties
    public CardsPreset(String presetName, ArrayList<String> exercises){
        super(presetName,exercises);
    }

    // Default empty test preset
    public CardsPreset(){
        super(numExercises);
    }


}
