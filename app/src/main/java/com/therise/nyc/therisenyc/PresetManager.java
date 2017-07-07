package com.therise.nyc.therisenyc;

// PresetManager.java: For managing presets (storing, reading, writing, etc.)

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

class PresetManager<T extends Preset>  {

    private Gson gson;
    private String directory;
    private String filename;
    private File jsonFile;
    private Context context;
    private Type presetType;

    private PresetHolder<T> presets;

    PresetManager(String filename, Context context, Type presetType){
        gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = context.getExternalFilesDir(null);
        if (dir != null){
            this.directory = dir.getPath();
        }
        this.filename = filename;
        this.context = context;
        presets = new PresetHolder<>(); // create default
        this.presetType = presetType;
    }

    void loadPresets(){

        // Create jsonReader
        JsonReader jsonReader;

        try {

            // Load presets from stored file
            // If we haven't stored presets yet, get initial file from assets
            jsonFile = new File(directory, filename);

            if (jsonFile.exists()){
                if (WorkoutStatic.REFRESH_FILES) {
                    jsonReader = new JsonReader(new InputStreamReader(context.getAssets().open(filename)));
                }
                else{
                    jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile)));
                }
            }

            else{
                jsonReader = new JsonReader(new InputStreamReader(context.getAssets().open(filename)));
            }

            // Load presets
            PresetHolder<T> tempPresets = gson.fromJson(jsonReader, presetType);

            if (tempPresets != null && tempPresets.getNumPresets()>0){
                for (int i = 0; i < tempPresets.getNumPresets();i++){
                    presets.addPreset(tempPresets.getPreset(i));
                }
            }

            jsonReader.close();

        }
        catch(Exception e){e.printStackTrace();}

    }

    // write changes in presets
    private void updateFile(){
        try{

            // Set up output writer
            FileWriter fileWriter = new FileWriter(jsonFile);

            // write
            gson.toJson(presets,fileWriter);

            // close writer
            fileWriter.close();
        }

        catch(Exception e){e.printStackTrace();}
    }

    T getPreset(int position){
        return presets.getPreset(position);
    }

    int getNumPresets(){
        return presets.getNumPresets();
    }

    ArrayList<String> getNames(){
        return presets.getNames();
    }

    void addPreset(T preset) {
        presets.addPreset(preset);
        presets.sort();
        updateFile();
    }

    void deletePreset(String presetName){
        presets.deletePreset(getNames().indexOf(presetName));
        updateFile();
    }

}
