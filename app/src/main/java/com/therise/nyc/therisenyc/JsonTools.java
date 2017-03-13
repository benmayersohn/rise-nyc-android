package com.therise.nyc.therisenyc;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by mayerzine on 3/11/17.
 */

public class JsonTools {

    public static <T> void writeToJson(PresetHolder<T> presets, T p, File jsonFile, Gson gson){
        presets.addPreset(p);
        FileWriter fileWriter;

        // save
        try{
            // Set up output writer
            fileWriter = new FileWriter(jsonFile);

            // write
            gson.toJson(presets,fileWriter);

            // close writer
            fileWriter.close();
        }
        catch(Exception e){}
    }

    public static <T> void removeFromJson(PresetHolder<T> presets, Preset p, File jsonFile, Gson gson){
        presets.removePreset(p);
        FileWriter fileWriter;

        // save
        try{
            // Set up output writer
            fileWriter = new FileWriter(jsonFile);

            // write
            gson.toJson(presets,fileWriter);

            // close writer
            fileWriter.close();
        }
        catch(Exception e){}
    }
}
