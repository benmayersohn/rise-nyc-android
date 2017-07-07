package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/15/17.
 *
 * Location, used by SQL to store information from each workout location
 */

public class Location {
    private String name;
    private String borough;
    private String day;
    private String place;
    private String img;
    private String desc;

    public Location(){ }

    public Location(String name, String borough, String day, String place, String img, String desc) {
        this.name = name;
        this.borough = borough;
        this.day = day;
        this.place = place;
        this.img = img;
        this.desc = desc;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getBorough(){
        return this.borough;
    }

    public void setBorough(String borough){
        this.borough = borough;
    }

    public String getDay(){
        return this.day;
    }

    public void setDay(String day){
        this.day = day;
    }

    public String getPlace(){
        return this.place;
    }

    public void setPlace(String place){
        this.place = place;
    }

    public String getImg(){
        return this.img;
    }

    public void setImg(String img){
        this.img = img;
    }

    public String getDesc(){
        return this.desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

}
