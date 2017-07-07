package com.therise.nyc.therisenyc;

import java.util.HashMap;

/**
 * LocationStatic: static variables related to location and database of locations
 */

class LocationStatic {

    static final String DAY_VIEW = "DAY_VIEW";
    static final String BOROUGH_VIEW = "BOROUGH_VIEW";
    static final int NUM_VIEWS_DAY = 5;
    static final int NUM_VIEWS_BOROUGH = 3;

    // Arrays of days and boroughs (in order of tabs)
    static String[] DAYS = {"M","T","W","R","F"};
    static String[] BOROUGHS = {"Manhattan","Brooklyn","Queens"};

    // Create a map between shortened weekdays and values
    static final HashMap<String,String> DAY_MAP = makeDayMap();

    private static HashMap<String,String> makeDayMap(){
        HashMap<String,String> map = new HashMap<>();
        map.put("M","Monday");
        map.put("T","Tuesday");
        map.put("W","Wednesday");
        map.put("R","Thursday");
        map.put("F","Friday");
        return map;
    }

    static String USER_VERSION_STRING = "PRAGMA user_version = ";

    static final int VERSION = 1;
    static final String DB_NAME = "locations.db";

    // Keys from SQL database
    static final String KEY_TITLE = "title";
    static final String KEY_DAY = "day";
    static final String KEY_PLACE = "place";
    static final String KEY_DESC = "desc";
    static final String KEY_IMG = "img";

    static final String TAB_NAME = "tabName";
    static final String VIEW_TYPE = "viewType";

    static final int IMAGE_HEIGHT = 220;
    static final int IMAGE_WIDTH = 300;
}
