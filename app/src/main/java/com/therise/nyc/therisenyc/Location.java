package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/15/17.
 *
 * Location, used by SQL to store information from each workout location
 */

public class Location {
    String _name;
    String _borough;
    String _day;
    String _place;
    String _img;
    String _desc;

    public Location(){   }

    public Location(String _name, String _borough, String _day, String _place, String _img, String _desc) {
        this._name = _name;
        this._borough = _borough;
        this._day = _day;
        this._place = _place;
        this._img = _img;
        this._desc = _desc;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getBorough(){
        return this._borough;
    }

    public void setBorough(String borough){
        this._borough = borough;
    }

    public String getDay(){
        return this._day;
    }

    public void setDay(String day){
        this._day = day;
    }

    public String getPlace(){
        return this._place;
    }

    public void setPlace(String place){
        this._place = place;
    }

    public String getImg(){
        return this._img;
    }

    public void setImg(String img){
        this._img = img;
    }

    public String getDesc(){
        return this._desc;
    }

    public void setDesc(String desc){
        this._desc = desc;
    }

}
