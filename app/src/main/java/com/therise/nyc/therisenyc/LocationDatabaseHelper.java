package com.therise.nyc.therisenyc;


// SQL database helper - use to load up locations

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import android.content.res.AssetManager;

class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static File DB_FILE;
    private Context context;

    private boolean invalidDatabaseFile = false;
    private boolean isUpgraded = false;

    /**
     * number of users of the database connection.
     */

    private int numOpenConnections = 0;

    private static LocationDatabaseHelper helperInstance;

    // Create instance of database helper if one not already initialized
    synchronized static LocationDatabaseHelper getInstance(Context context) throws IOException{

        if (helperInstance == null) {
            helperInstance = new LocationDatabaseHelper(context.getApplicationContext());
        }

        return helperInstance;
    }

    // constructor (called by getInstance)
    private LocationDatabaseHelper(Context ctxt) throws IOException{
        super(ctxt, LocationStatic.DB_NAME, null, LocationStatic.VERSION);
        this.context = ctxt;

        DB_FILE = ctxt.getDatabasePath(LocationStatic.DB_NAME);

        SQLiteDatabase locdata = null;

        // Try loading database
        try {
            locdata = getReadableDatabase();

            if (locdata != null) {
                locdata.close();
            }
            if (invalidDatabaseFile) {
                copyDatabase();
            }
        }

        catch (SQLiteException e) {
            e.printStackTrace();
        }

        finally {
            if (locdata != null && locdata.isOpen()) {
                locdata.close();
            }
        }
    }

    // Copies database from .db file to app storage (so we don't need to keep reloading)
    private void copyDatabase() throws IOException{

        AssetManager assetManager = context.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(LocationStatic.DB_NAME);
            out = new FileOutputStream(DB_FILE);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (in != null) {
                try {
                    in.close();
                }

                catch (IOException e) {e.printStackTrace();}
            }
            if (out != null) {
                try {
                    out.close();
                }

                catch (IOException e) {e.printStackTrace();}
            }
        }
        setDatabaseVersion();
        invalidDatabaseFile = false;

    }

    /**
     * implementation to avoid closing the database connection while it is in
     * use by others.
     */
    @Override
    public synchronized void close() {
        numOpenConnections--;
        if (numOpenConnections == 0) {
            super.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // We don't want to create a new file, bc we already have our database
        invalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We don't want to create a new file, bc we already have our database
        invalidDatabaseFile = true;
        isUpgraded = true;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;

        try {
            db = SQLiteDatabase.openDatabase(DB_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL(LocationStatic.USER_VERSION_STRING + LocationStatic.VERSION);
        }

        catch (SQLiteException e ) {
            e.printStackTrace();
        }

         finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }



}
