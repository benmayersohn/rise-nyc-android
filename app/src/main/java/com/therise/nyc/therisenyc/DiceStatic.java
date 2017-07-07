package com.therise.nyc.therisenyc;

/**
 * Static variables for dice
 */

class DiceStatic {

    // six-sided die
    static final int NUM_EXERCISES = 6;

    static final String PRESET_FILE = "dice_presets.json";

    static final int[] DIE_IMAGES = new int[]{
            R.drawable.dice_one,R.drawable.dice_two,R.drawable.dice_three,R.drawable.dice_four,
            R.drawable.dice_five,R.drawable.dice_six};

    static final int REQUEST_CODE = 2;

    static final int NUM_ANIMATION_CYCLES = 12;
    static final int DICE_ANIMATE_INTERVAL = 50;

    public static final String WORKOUT_TYPE = "DICE";

    static final String ROLL_STRING = "Roll: ";
}
