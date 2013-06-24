package com.seeedstudio.beacon.data;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.seeedstudio.beacon.utility.BeaconApp;

public class BeaconProvider extends ContentProvider  {
//implements PipeDataWriter<Cursor> {

    // debugging
    private final static String TAG = "BeaconProvider";

    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sBeaconProjectionMap;

    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sLiveFolderProjectionMap;

    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] READ_BEACON_PROJECTION = new String[] {
            BeaconApp.Beacon._ID, // Projection position 0, the note's id
            BeaconApp.Beacon.COLUMN_NAME_DESC, // Projection position 1, the
                                               // beacon's describe
            BeaconApp.Beacon.COLUMN_NAME_TITLE, // Projection position 2, the
                                                // note's title
    };
    private static final int READ_BEACON_DESC_INDEX = 1;
    private static final int READ_BEACON_TITLE_INDEX = 2;

    /*
     * Constants used by the Uri matcher to choose an action based on the
     * pattern of the incoming URI
     */
    // The incoming URI matches the Beacon URI pattern
    private static final int BEACONS = 1;
    // The incoming URI matches the Note ID URI pattern
    private static final int BEACON_ID = 2;

    // sensor
    private static final int SENSORS = 3;
    private static final int SENSOR_ID = 4;

    // actuator
    private static final int ACTUATORS = 5;
    private static final int ACTUATOR_ID = 6;

    // some Instance class
    private static final UriMatcher sUriMatcher;
    private BeaconDBHelper mOpenHelper;

    /**
     * A block that instantiates and sets static objects
     */
    static {

        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a pattern that routes URIs terminated with "notes" to a NOTES
        // operation
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "beacon", BEACONS);

        // Add a pattern that routes URIs terminated with "notes" plus an
        // integer
        // to a note ID operation
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "beacon/#", BEACON_ID);

        // sensor
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "sensor", BEACONS);
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "sensor/#", BEACON_ID);

        // actuator
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "actuator", BEACONS);
        sUriMatcher.addURI(BeaconApp.AUTHORITY, "actuator/#", BEACON_ID);

        /*
         * Creates and initializes a projection map that returns all columns
         */

        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sBeaconProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sBeaconProjectionMap.put(BeaconApp.Beacon._ID, BeaconApp.Beacon._ID);

        // Maps "title" to "title"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_TITLE,
                BeaconApp.Beacon.COLUMN_NAME_TITLE);

        // Maps "desc" to "desc"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_DESC,
                BeaconApp.Beacon.COLUMN_NAME_DESC);

        // Maps "deviceID" to "deviceID"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID,
                BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID);

        // ///////////////////////////
        // Sensor part
        // ///////////////////////////
        // Maps "sensor" to "sensor"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE,
                BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE);

        // Maps "Freq" to "Freq"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ,
                BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ);

        // Maps "unit" to "unit"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT,
                BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT);

        // ///////////////////////////
        // Actuator part
        // ///////////////////////////
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE,
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE);

        sBeaconProjectionMap.put(
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE,
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE);

        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION,
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION);

        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE,
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE);

        sBeaconProjectionMap.put(
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE,
                BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE);

        // Maps "created" to "created"
        sBeaconProjectionMap.put(BeaconApp.Beacon.COLUMN_NAME_CREATE_DATE,
                BeaconApp.Beacon.COLUMN_NAME_CREATE_DATE);

        // Maps "modified" to "modified"
        sBeaconProjectionMap.put(
                BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE,
                BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE);
    }

//    @Override
//    public void writeDataToPipe(ParcelFileDescriptor output, Uri uri,
//            String mimeType, Bundle opts, Cursor c) {
//        // We currently only support conversion-to-text from a single note
//        // entry,
//        // so no need for cursor data type checking here.
//        FileOutputStream fout = new FileOutputStream(output.getFileDescriptor());
//        PrintWriter pw = null;
//        try {
//            pw = new PrintWriter(new OutputStreamWriter(fout, "UTF-8"));
//            pw.println(c.getString(READ_BEACON_TITLE_INDEX));
//            pw.println("");
//            pw.println(c.getString(READ_BEACON_DESC_INDEX));
//        } catch (UnsupportedEncodingException e) {
//            Log.w(TAG, "Ooops", e);
//        } finally {
//            c.close();
//            if (pw != null) {
//                pw.flush();
//            }
//            try {
//                fout.close();
//            } catch (IOException e) {
//            }
//        }
//    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;
        int count;

        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {
        case BEACONS:
            count = db.delete(BeaconApp.Beacon.TABLE_NAME, selection,
                    selectionArgs);
            break;
        case BEACON_ID:

            /*
             * Starts creating the final WHERE clause by restricting it to the
             * incoming note ID.
             */
            finalWhere = BeaconApp.Beacon._ID + // The ID column name
                    " = " + // test for equality
                    uri.getPathSegments(). // the incoming note ID
                            get(BeaconApp.Beacon.BEACON_ID_PATH_POSITION);

            // If there were additional selection criteria, append them to the
            // final WHERE
            // clause
            if (selection != null) {
                finalWhere = finalWhere + " AND " + selection;
            }

            // Does the update and returns the number of rows updated.
            count = db.delete(BeaconApp.Beacon.TABLE_NAME, // The database table
                                                           // name.
                    finalWhere, // The final WHERE clause to use
                                // placeholders for whereArgs
                    selectionArgs // The where clause column values to select
                                  // on, or
                    // null if the values are in the where argument.
                    );

            break;

        // If the incoming pattern is invalid, throws an exception.
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*
         * Gets a handle to the content resolver object for the current context,
         * and notifies it that the incoming URI changed. The object passes this
         * along to the resolver framework, and observers that have registered
         * themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    @Override
    public String getType(Uri uri) {
        /**
         * Chooses the MIME type based on the incoming URI pattern
         */
        switch (sUriMatcher.match(uri)) {

        // If the pattern is for notes or live folders, returns the general
        // content type.
        case BEACONS:
            // case LIVE_FOLDER_NOTES:
            // return BeaconApp.Beacon.CONTENT_TYPE;

            // If the pattern is for note IDs, returns the note ID content type.
        case BEACON_ID:
            return BeaconApp.Beacon.CONTENT_ITEM_TYPE;

            // If the URI pattern doesn't match any permitted patterns, throws
            // an exception.
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        String insertTableName = null;
        Uri insertIdBase = null;

        switch (sUriMatcher.match(uri)) {
        case BEACONS:
            insertTableName = BeaconApp.Beacon.TABLE_NAME;
            insertIdBase = BeaconApp.Beacon.CONTENT_ID_URI_BASE;
            break;
        case SENSORS:
            insertTableName = BeaconApp.Sensor.TABLE_SENSOR;
            insertIdBase = BeaconApp.Sensor.CONTENT_SENSOR_URI_BASE;
            break;
        case ACTUATORS:
            insertTableName = BeaconApp.Actuator.TABLE_ACTUATOR;
            insertIdBase = BeaconApp.Actuator.CONTENT_ACTUATOR_URI_BASE;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // // Validates the incoming URI.
        // if (sUriMatcher.match(uri) != BEACONS) {
        // throw new IllegalArgumentException("Unknown URI " + uri);
        // }

        // A map to hold the new setup's values
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Gets the current system time in milliseconds
        Long now = Long.valueOf(System.currentTimeMillis());

        // If the values map doesn't contain the creation date, sets the
        // value
        // to the current time.
        if (sUriMatcher.match(uri) == BEACONS) {

            if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_CREATE_DATE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_CREATE_DATE, now);
            }

            // If the values map doesn't contain the modification date, sets the
            // value to the current
            // time.
            if (values
                    .containsKey(BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE, now);
            }
        }

        /**
         * input atom data to content value
         */
        // device name and describe
        if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_TITLE) == false) {
            values.put(BeaconApp.Beacon.COLUMN_NAME_TITLE, "beacon");
        }

        if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_DESC) == false) {
            values.put(BeaconApp.Beacon.COLUMN_NAME_DESC, "describe");
        }

        if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID) == false) {
            values.put(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID, "deviceID");
        }

        // sensor part
        if (sUriMatcher.match(uri) == BEACONS
                || sUriMatcher.match(uri) == SENSORS) {
            if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE, "sensor");
            }
            if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ,
                        "frequency");
            }
            if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT, "unit");
            }
        }

        // actuator part
        if (sUriMatcher.match(uri) == BEACONS
                || sUriMatcher.match(uri) == ACTUATORS) {
            if (values.containsKey(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE,
                        "actuator");
            }
            if (values
                    .containsKey(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE) == false) {
                values.put(
                        BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE,
                        "trigger");
            }
            if (values
                    .containsKey(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION,
                        "action");
            }
            if (values
                    .containsKey(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE,
                        "compare");
            }
            if (values
                    .containsKey(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE) == false) {
                values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE,
                        "value");
            }
        }

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new note.
        long rowId = db.insert(insertTableName,
                BeaconApp.Beacon.COLUMN_NAME_DESC, values);

        if (rowId > 0) {
            Uri beaconUri = ContentUris.withAppendedId(insertIdBase, rowId);
            // Notifies observers registered against this provider that the data
            // changed.
            getContext().getContentResolver().notifyChange(beaconUri, null);
            return beaconUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an
        // exception.
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BeaconDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        /**
         * Choose the projection and adjust the "where" clause based on URI
         * pattern-matching.
         */
        switch (sUriMatcher.match(uri)) {
        // If the incoming URI is for notes, chooses the Notes projection
        case BEACONS:
            qb.setTables(BeaconApp.Beacon.TABLE_NAME);
            qb.setProjectionMap(sBeaconProjectionMap);
            break;

        /*
         * If the incoming URI is for a single note identified by its ID,
         * chooses the note ID projection, and appends "_ID = <noteID>" to the
         * where clause, so that it selects that single note
         */
        case BEACON_ID:
            qb.setTables(BeaconApp.Beacon.TABLE_NAME);
            qb.setProjectionMap(sBeaconProjectionMap);
            qb.appendWhere(BeaconApp.Beacon._ID + // the name of the ID column
                    "=" +
                    // the position of the note ID itself in the incoming URI
                    uri.getPathSegments().get(
                            BeaconApp.Beacon.BEACON_ID_PATH_POSITION));
            break;
        case SENSORS:
            qb.setTables(BeaconApp.Sensor.TABLE_SENSOR);
            qb.setProjectionMap(sBeaconProjectionMap);
            break;
        case SENSOR_ID:
            qb.setTables(BeaconApp.Sensor.TABLE_SENSOR);
            qb.setProjectionMap(sBeaconProjectionMap);
            qb.appendWhere(BeaconApp.Sensor._ID + // the name of the ID column
                    "=" +
                    // the position of the note ID itself in the incoming URI
                    uri.getPathSegments().get(
                            BeaconApp.Sensor.SENSOR_ID_PATH_POSITION));
            break;
        case ACTUATORS:
            qb.setTables(BeaconApp.Actuator.TABLE_ACTUATOR);
            qb.setProjectionMap(sBeaconProjectionMap);
            break;
        case ACTUATOR_ID:
            qb.setTables(BeaconApp.Actuator.TABLE_ACTUATOR);
            qb.setProjectionMap(sBeaconProjectionMap);
            qb.appendWhere(BeaconApp.Actuator._ID + // the name of the ID column
                    "=" +
                    // the position of the note ID itself in the incoming URI
                    uri.getPathSegments().get(
                            BeaconApp.Actuator.ACTUATOR_ID_PATH_POSITION));
            break;
        default:
            // If the URI doesn't match any of the known patterns, throw an
            // exception.
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        // If no sort order is specified, uses the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = BeaconApp.Beacon.DEFAULT_SORT_ORDER;
        } else {
            // otherwise, uses the incoming sort order
            orderBy = sortOrder;
        }

        // Opens the database object in "read" mode, since no writes need to be
        // done.
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        /*
         * Performs the query. If no problems occur trying to read the database,
         * then a Cursor object is returned; otherwise, the cursor variable
         * contains null. If no records were selected, then the Cursor object is
         * empty, and Cursor.getCount() returns 0.
         */
        Cursor c = qb.query(db, // The database to query
                projection, // The columns to return from the query
                selection, // The columns for the where clause
                selectionArgs, // The values for the where clause
                null, // don't group the rows
                null, // don't filter by row groups
                orderBy // The sort order
                );

        // Tells the Cursor what URI to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {
        case BEACONS:
            count = db.update(BeaconApp.Beacon.TABLE_NAME, values, selection,
                    selectionArgs);
            break;
        case BEACON_ID:
            // From the incoming URI, get the note ID
            String beaconId = uri.getPathSegments().get(
                    BeaconApp.Beacon.BEACON_ID_PATH_POSITION);

            /*
             * Starts creating the final WHERE clause by restricting it to the
             * incoming note ID.
             */
            finalWhere = BeaconApp.Beacon._ID + // The ID column name
                    " = " + // test for equality
                    uri.getPathSegments(). // the incoming note ID
                            get(BeaconApp.Beacon.BEACON_ID_PATH_POSITION);

            // If there were additional selection criteria, append them to the
            // final WHERE
            // clause
            if (selection != null) {
                finalWhere = finalWhere + " AND " + selection;
            }

            // Does the update and returns the number of rows updated.
            count = db.update(BeaconApp.Beacon.TABLE_NAME, // The database table
                                                           // name.
                    values, // A map of column names and new values to use.
                    finalWhere, // The final WHERE clause to use
                                // placeholders for whereArgs
                    selectionArgs // The where clause column values to select
                                  // on, or
                    // null if the values are in the where argument.
                    );

            break;
        // If the incoming pattern is invalid, throws an exception.
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*
         * Gets a handle to the content resolver object for the current context,
         * and notifies it that the incoming URI changed. The object passes this
         * along to the resolver framework, and observers that have registered
         * themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;

    }

    BeaconDBHelper getOpenHelperForTest() {
        return mOpenHelper;
    }

}
