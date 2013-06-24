package com.seeedstudio.beacon.utility;

import android.net.Uri;
import android.provider.BaseColumns;

public class BeaconApp {
    public static final String AUTHORITY = "com.seeedstudio.provider.Beacon";

    public BeaconApp() {
    }

    /**
     * Beacon Table contract
     * 
     * @author xiaobo
     * 
     */
    public static final class Beacon implements BaseColumns {

        // This class cannot be instantiated
        public Beacon() {
        }

        // The table name offered by this provider
        public static final String TABLE_NAME = "beacon";

        /*
         * URI definitions
         */
        private static final String SCHEME = "content://";

        // path part for beacon URI
        private static final String PATH_BEACON = "/beacon";

        // path part for beacon ID URI
        private static final String PATH_BEACON_ID = "/beacon/";

        // 0-relative position of a note ID segment in the path part of a note
        // ID URI
        public static final int BEACON_ID_PATH_POSITION = 1;

        // The content:// style URL for this table
        // >> content://com.seeedstudio.provider.Beacon/beacon
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
                + PATH_BEACON);

        /**
         * The content URI base for a single note. Callers must append a numeric
         * note id to this Uri to retrieve a note
         */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME
                + AUTHORITY + PATH_BEACON_ID);

        /**
         * The content URI match pattern for a single note, specified by its ID.
         * Use this to match incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME
                + AUTHORITY + PATH_BEACON_ID + "/#");

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.seeedstudio.beacon";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.seeedstudio.beacon";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /*
         * Column definitions, 8 parts. device name, describe; sensor name,
         * frequency, unit; actuator name, action, compare, compare value;
         */

        /**
         * Column name for the title of the beacon
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String COLUMN_NAME_TITLE = "beacon";

        /**
         * Column name of the beacon describe
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String COLUMN_NAME_DESC = "describe";

        /**
         * Column id of the beacon device
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_DEVICE_ID = "deviceId";

        // sensor part
        /**
         * Column name of the beacon sensor
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_TITLE = "sensor";

        /**
         * Column name of the beacon sensor frequency
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_FREQ = "frequency";

        /**
         * Column name of the beacon sensor unit
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_UNIT = "unit";

        // actuator part
        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_TITLE = "actuator";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE = "trigger";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_ACTION = "action";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_COMPARE = "compare";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_COMPARE_VALUE = "value";

        // time stamp
        /**
         * Column name for the creation timestamp
         * <P>
         * Type: INTEGER (long from System.curentTimeMillis())
         * </P>
         */
        public static final String COLUMN_NAME_CREATE_DATE = "created";

        /**
         * Column name for the modification timestamp
         * <P>
         * Type: INTEGER (long from System.curentTimeMillis())
         * </P>
         */
        public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";
        
        public static final String[] PROJECTION = new String[] {
            _ID, COLUMN_NAME_TITLE,
            COLUMN_NAME_DESC,
            COLUMN_NAME_DEVICE_ID,
            COLUMN_NAME_SENSOR_TITLE,
            COLUMN_NAME_SENSOR_FREQ,
            COLUMN_NAME_SENSOR_UNIT,
            COLUMN_NAME_ACTUATOR_TITLE,
            COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE,
            COLUMN_NAME_ACTUATOR_ACTION,
            COLUMN_NAME_ACTUATOR_COMPARE,
            COLUMN_NAME_ACTUATOR_COMPARE_VALUE };
    }

    public static final class Sensor implements BaseColumns {

        public Sensor() {
        }

        // table name
        public static final String TABLE_SENSOR = "sensor";

        /**
         * URI definitions
         */
        private static final String SCHEME = "content://";

        // path part for sensor table
        private static final String PATH_SENSOR = "/sensor";
        private static final String PATH_SENSOR_ID = "/sensor/";

        // 0-relative position of a note ID segment in the path part of a note
        // ID URI
        public static final int SENSOR_ID_PATH_POSITION = 1;

        // The content:// style URL for this table
        // >> content://com.seeedstudio.provider.Beacon/sensor

        // sensor
        public static final Uri CONTENT_SENSOR_URI = Uri.parse(SCHEME
                + AUTHORITY + PATH_SENSOR);
        public static final Uri CONTENT_SENSOR_URI_BASE = Uri.parse(SCHEME
                + AUTHORITY + PATH_SENSOR_ID);
        public static final Uri CONTENT_SENSOR_URI_PATTERN = Uri.parse(SCHEME
                + AUTHORITY + PATH_SENSOR_ID + "/#");

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.seeedstudio.sensor";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.seeedstudio.sensor";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * Column name of the beacon sensor
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_TITLE = "sensor";

        /**
         * Column name of the beacon describe
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_DESC = "describe";

        /**
         * Column id of the beacon device
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_ID = "deviceId";

        /**
         * Column name of the beacon sensor frequency
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_FREQ = "frequency";

        /**
         * Column name of the beacon sensor unit
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_SENSOR_UNIT = "unit";

    }

    public static final class Actuator implements BaseColumns {

        public Actuator() {
        }

        // table name
        public static final String TABLE_ACTUATOR = "actuator";

        /*
         * URI definitions
         */
        private static final String SCHEME = "content://";

        // path part for actuator table
        private static final String PATH_ACTUATOR = "/actuator";
        private static final String PATH_ACTUATOR_ID = "/actuator/";

        // 0-relative position of a note ID segment in the path part of a note
        // ID URI
        public static final int ACTUATOR_ID_PATH_POSITION = 1;

        // The content:// style URL for this table
        // >> content://com.seeedstudio.provider.Beacon/beacon

        // actuator
        public static final Uri CONTENT_ACTUATOR_URI = Uri.parse(SCHEME
                + AUTHORITY + PATH_ACTUATOR);
        public static final Uri CONTENT_ACTUATOR_URI_BASE = Uri.parse(SCHEME
                + AUTHORITY + PATH_ACTUATOR_ID);
        public static final Uri CONTENT_ACTUATOR_URI_PATTERN = Uri.parse(SCHEME
                + AUTHORITY + PATH_ACTUATOR_ID + "/#");

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.seeedstudio.actuator";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.seeedstudio.actuator";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /*
         * Column definitions, 8 parts. device name, describe; sensor name,
         * frequency, unit; actuator name, action, compare, compare value;
         */

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_TITLE = "actuator";
        /**
         * Column name of the beacon describe
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_DESC = "describe";

        /**
         * Column id of the beacon device
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_ID = "deviceId";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE = "trigger";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_ACTION = "action";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_COMPARE = "compare";

        /**
         * Column name of the beacon actuator
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String COLUMN_NAME_ACTUATOR_COMPARE_VALUE = "value";
    }
}
