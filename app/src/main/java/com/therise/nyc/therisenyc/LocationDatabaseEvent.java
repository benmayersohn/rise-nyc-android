package com.therise.nyc.therisenyc;

/**
 * LocationDatabaseEvent: Lets subscribers know when a new LocationDatabaseHelper needs to be instantiated
 */

public class LocationDatabaseEvent {
    LocationDatabaseHelper helper;

    public LocationDatabaseEvent(LocationDatabaseHelper helper) {
        this.helper = helper;
    }

    LocationDatabaseHelper getHelper(){
        return helper;
    }
}
