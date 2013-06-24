package com.seeedstudio.beacon.data;

import com.seeedstudio.beacon.utility.BeaconApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Sqlite database class for paired Beacon
 * 
 * @author xiaobo
 * 
 */
public class BeaconDBHelper extends SQLiteOpenHelper {

    // debugging
    private final static String TAG = "BeaconDbHelper";

    // database name and version
    private static final String DATABASE_NAME = "beacons.db";
    private static final int DATABASE_VERSION = 1;

    public BeaconDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CreateBeaconTable = "CREATE TABLE "
            + BeaconApp.Beacon.TABLE_NAME + " ( " + BeaconApp.Beacon._ID
            + " INTEGER PRIMARY KEY," + BeaconApp.Beacon.COLUMN_NAME_TITLE
            + " TEXT," + BeaconApp.Beacon.COLUMN_NAME_DESC + " TEXT,"
            + BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID + " INTEGER,"
            + BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE + " INTEGER,"
            + BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ + " INTEGER,"
            + BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT + " INTEGER,"
            + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE + " INTEGER,"
            + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE
            + " INTEGER," + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION
            + " INTEGER," + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE
            + " INTEGER," + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE
            + " INTEGER," + BeaconApp.Beacon.COLUMN_NAME_CREATE_DATE
            + " INTEGER," + BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE
            + " INTEGER);";
    public static final String CreateSensorTable = "CREATE TABLE "
            + BeaconApp.Sensor.TABLE_SENSOR + " ( " + BeaconApp.Sensor._ID
            + " INTEGER PRIMARY KEY,"
            + BeaconApp.Sensor.COLUMN_NAME_SENSOR_TITLE + " TEXT,"
            + BeaconApp.Sensor.COLUMN_NAME_SENSOR_DESC + " TEXT,"
            + BeaconApp.Sensor.COLUMN_NAME_SENSOR_ID + " INTEGER,"
            + BeaconApp.Sensor.COLUMN_NAME_SENSOR_FREQ + " INTEGER,"
            + BeaconApp.Sensor.COLUMN_NAME_SENSOR_UNIT + " INTEGER);";

    public static final String CreateActuatorTable = "CREATE TABLE "
            + BeaconApp.Actuator.TABLE_ACTUATOR + " ( "
            + BeaconApp.Actuator._ID + " INTEGER PRIMARY KEY,"
            + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_TITLE + " TEXT,"
            + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_DESC + " TEXT,"
            + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_ID + " INTEGER,"
            + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE
            + " INTEGER," + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_ACTION
            + " INTEGER," + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_COMPARE
            + " INTEGER,"
            + BeaconApp.Actuator.COLUMN_NAME_ACTUATOR_COMPARE_VALUE
            + " INTEGER);";

    private void createTable(SQLiteDatabase db) {
        db.execSQL(CreateBeaconTable);
        db.execSQL(CreateSensorTable);
        db.execSQL(CreateActuatorTable);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + BeaconApp.Beacon.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BeaconApp.Sensor.TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + BeaconApp.Actuator.TABLE_ACTUATOR);

        // Recreates the database with a new version
        onCreate(db);
    }

}
