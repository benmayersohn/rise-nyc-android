package com.therise.nyc.therisenyc;

import java.util.ArrayList;

/**
 * Created by mayerzine on 3/10/17.
 */

public class DicePreset extends NumberedPreset implements Preset {

    // six-sided die
    private static final int numExercises = 6;

    // Construct preset with all properties
    public DicePreset(String presetName, ArrayList<String> exercises){
        super(presetName,exercises);
    }

    // Default empty test preset
    public DicePreset(){
        super(numExercises);
    }


}
