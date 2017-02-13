package com.therise.nyc.therisenyc;

import android.graphics.Bitmap;

/**
 * Created by mayerzine on 1/16/17.
 *
 * Location Page class
 * All information needed to generate a workout location page
 */

public class LocPage {

    // This info will be obtained from SQL database
    private String title;
    private String place;
    private int img;
    private String desc;
    private String day;


    public LocPage(String title, String place, String day, int img, String desc){
        this.title = title;
        this.place = place;
        this.img = img;
        this.day = day;
        this.desc = desc;
    }

    // Getters
    // We don't need setters, since they're set on construction

    public String getTitle(){
        return title;
    }

    public String getPlace(){
        return place;
    }

    public int getImg(){
        return img;
    }

    public String getDesc(){
        return desc;
    }

    public String getDay(){
        return day;
    }

}
