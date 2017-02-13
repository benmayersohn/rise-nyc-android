package com.therise.nyc.therisenyc;

/**
 * Created by mayerzine on 1/15/17.
 *
 * SQL database helper
 */

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

public class LocationDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "locations.db";
    private static File DB_FILE;
    private Context myContext;

    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded = false;

    /**
     * number of users of the database connection.
     * */
    private int mOpenConnections = 0;

    // Load our database of locations
    private SQLiteDatabase locdata;

    private static LocationDatabaseHelper mInstance;

    // Create instance of database helper if one not already initialized
    synchronized static public LocationDatabaseHelper getInstance(Context context) throws IOException{

        if (mInstance == null) {
            mInstance = new LocationDatabaseHelper(context.getApplicationContext());
        }

        return mInstance;
    }

    // constructor (called by getInstance)
    private LocationDatabaseHelper(Context ctxt) throws IOException{
        super(ctxt, DB_NAME, null, VERSION);
        this.myContext = ctxt;
        DB_FILE = ctxt.getDatabasePath(DB_NAME);

        locdata = null;

        // Try loading database
        try {
            locdata = getReadableDatabase();

            if (locdata != null) {
                locdata.close();
            }
            if (mInvalidDatabaseFile) {
                copyDatabase();
            }
        }

        catch (SQLiteException e) {
        }

        finally {
            if (locdata != null && locdata.isOpen()) {
                locdata.close();
            }
        }
    }

    // Copies database from .db file to app storage (so we don't need to keep reloading)
    private void copyDatabase() throws IOException{

        AssetManager assetManager = myContext.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DB_NAME);
            out = new FileOutputStream(DB_FILE);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        catch (IOException e) {
        }

        finally {
            if (in != null) {
                try {
                    in.close();
                }

                catch (IOException e) {}
            }
            if (out != null) {
                try {
                    out.close();
                }

                catch (IOException e) {}
            }
        }
        setDatabaseVersion();
        mInvalidDatabaseFile = false;

    }

    /**
     * implementation to avoid closing the database connection while it is in
     * use by others.
     */
    @Override
    public synchronized void close() {
        mOpenConnections--;
        if (mOpenConnections == 0) {
            super.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // We don't want to create a new file, bc we already have our database
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We don't want to create a new file, bc we already have our database
        mInvalidDatabaseFile = true;
        mIsUpgraded = true;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DB_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + VERSION);
        } catch (SQLiteException e ) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }



}
