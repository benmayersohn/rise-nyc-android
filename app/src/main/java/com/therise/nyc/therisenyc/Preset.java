package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 3/10/17.
 */

// A preset at minimum has a getName() method
public abstract class Preset implements Comparable<Preset>{

    public abstract String getName();

    @Override
    public int compareTo(Preset p2){
        return this.getName().compareTo(p2.getName());
    }
}
