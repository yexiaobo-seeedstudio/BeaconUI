package com.seeedstudio.beacon.ui;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.seeedstudio.beacon.ui.widget.CornerListView;
import com.seeedstudio.beacon.utility.BeaconApp;
import com.seeedstudio.library.Atom;
import com.seeedstudio.library.Utility;

public class MainActivity extends SherlockActivity {

    // debugging
    private static final boolean D = Utility.DEBUG;
    private static final String TAG = "MainActivity";

    // for activity request code
    public static final int CONFIG_REQUEST_CODE = 0;
    public static final int SETUP_REQUEST_CODE = 1;

    // Parcel object key for intent data transform
    public static final String SELECTED_BEACON = "com.seeedstudio.beacon.ui.atom";

    private CornerListView cornerListView = null;
    private List<Map<String, String>> listData = null;
    private SimpleAdapter adapter = null;
    private SimpleCursorAdapter mAdapter = null;
    private Cursor mCursor;

    // private String SELECT = "SELECT * FROM " + BeaconApp.Beacon.TABLE_NAME
    // + " WHERE " + BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE
    // + " <> " + "0";

    private String SELECT = BeaconApp.Beacon.COLUMN_NAME_TITLE + " <> ?"
            + " AND " + BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID + " <> ?";
    private String[] SELECTARGS = new String[] { "NULL", "0" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Sherlock_Light);
        setTitle(R.string.paired_beacon);
        setContentView(R.layout.main);

        // Gets the intent that started this Activity.
        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the
        // default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(BeaconApp.Beacon.CONTENT_URI);
        }

        // atom = new Atom("New Beacon", "Beacon", 0);

        ininUI();

    }

    private void ininUI() {

        // set action bar background
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(
                        R.drawable.action_bar_gradient_background));

        // set corner listview
        cornerListView = (CornerListView) findViewById(R.id.corner_list);
        cornerListView.setOnItemClickListener(new ListClickEvent());

        // cursor adapter
        // The names of the cursor columns to display in the view, initialized
        // to the title column
        String[] dataColumns = { BeaconApp.Beacon.COLUMN_NAME_TITLE,
                BeaconApp.Beacon.COLUMN_NAME_DESC };
        int[] viewIDs = { R.id.corner_listview_title,
                R.id.corner_listview_subtitle };
        // get the cursor for manager database
        mCursor = getContentResolver().query(getIntent().getData(),
                BeaconApp.Beacon.PROJECTION, SELECT, SELECTARGS,
                BeaconApp.Beacon.DEFAULT_SORT_ORDER);

        mAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.corner_listview, mCursor, dataColumns, viewIDs, 0);
        cornerListView.setAdapter(mAdapter);

        //
        // if (!isBeaconEmpty()) {
        // cornerListView.setVisibility(View.INVISIBLE);
        // } else {
        // cornerListView.setVisibility(View.GONE);
        // }

    }

    @Override
    protected void onPause() {
        if (D)
            Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (D)
            Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (D)
            Log.d(TAG, "onResume()");
        super.onResume();

        // if (mCursor != null) {
        // mCursor = getContentResolver().query(getIntent().getData(),
        // BeaconApp.Beacon.PROJECTION, SELECT, SELECTARGS,
        // BeaconApp.Beacon.DEFAULT_SORT_ORDER);
        // }
        mCursor.requery();
        mAdapter.notifyDataSetChanged();
    }

    private boolean isBeaconEmpty() {
        if (mAdapter.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    // ////////////////////////////////////////////////////////
    // listveiw data and click event
    // ////////////////////////////////////////////////////////

    private class ListClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position,
                long id) {
            Log.d(TAG, "Clikc item View: " + v + ", position: " + position
                    + ", id: " + id);

            Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult");

        switch (requestCode) {
        case CONFIG_REQUEST_CODE:

            if (D)
                Log.d(TAG, "get configure result");
            if (resultCode == Activity.RESULT_FIRST_USER && data != null) {
                Atom atom = (Atom) data.getSerializableExtra(SELECTED_BEACON);
                if (D)
                    Log.d(TAG, "get configure atom");

                if (atom != null) {
                    // startNormalSetup(atom);
                }
                // Toast.makeText(getApplicationContext(),
                // atom.getName() + " is Added", Toast.LENGTH_LONG).show();

                // TODO custom ListView that can be add beacon automatically
                // addListData(atom);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Configure data is Null", Toast.LENGTH_LONG).show();
                return;
            }
            break;
        // case SETUP_REQUEST_CODE:
        // if (D)
        // Log.d(TAG, "get normal setup result");
        // if (resultCode == Activity.RESULT_OK && data != null
        // && data.hasExtra(SELECTED_BEACON)) {
        // if (D)
        // Log.d(TAG, "get normal setup data");
        // Atom atom = (Atom) data
        // .getSerializableExtra(ConfigureActivity.CONFIG_KEY);
        //
        // Toast.makeText(getApplicationContext(),
        // atom.getName() + " is Added", Toast.LENGTH_LONG).show();
        //
        // // TODO custom ListView that can be add beacon automatically
        // addListData(atom);
        // } else {
        // if (D)
        // Log.d(TAG, "not get normal setup data");
        // Toast.makeText(getApplicationContext(),
        // "Normal setup data is Null", Toast.LENGTH_LONG).show();
        // return;
        // }
        // break;
        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startNormalSetup() {
        // Intent intent = new Intent(this, NormalSetupActivity.class);
        Intent intent = new Intent();
        intent.setData(BeaconApp.Beacon.CONTENT_URI);
        intent.setAction(Intent.ACTION_INSERT);
        // Intent intent = new Intent(Intent.ACTION_INSERT,
        // BeaconApp.Beacon.CONTENT_URI);
        // intent.putExtra(SELECTED_BEACON, atom);

        startActivity(intent);
    }

    // ////////////////////////////////////////////////////////
    // action bar setup
    // ////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_new_beacon:
            // trigger normal pattern
            // Intent intent = new Intent(this, SensorListActivity.class);
            // startActivityForResult(intent, CONFIG_REQUEST_CODE);
            // overridePendingTransition(R.anim.activity_pop_in, 0);
            startNormalSetup();

            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
