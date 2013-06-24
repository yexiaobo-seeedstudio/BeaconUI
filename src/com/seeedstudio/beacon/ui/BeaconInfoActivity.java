package com.seeedstudio.beacon.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.seeedstudio.beacon.utility.BeaconApp;
import com.seeedstudio.library.Atom;
import com.seeedstudio.library.Utility;

public class BeaconInfoActivity extends SherlockActivity {

    private static final String TAG = "BeaconInfoActivity";

    private Button reconfig, delete;
    private TextView device, sensorName, sensorFreq, deviceId, actName,
            actAction, actCompare, actCompareValue, actTrigger;
    private Cursor mCursor;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Sherlock_Light);
        setTitle(R.string.beacon_info);
        setContentView(R.layout.activity_beacon_info);

        // set action bar background
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(
                        R.drawable.action_bar_gradient_background));

        mUri = getIntent().getData();

        if (mUri == null) {
            finish();
            return;
        } else {
            if (Utility.DEBUG)
                Log.d(TAG, "Uri: " + mUri);

            mCursor = getContentResolver().query(mUri,
                    BeaconApp.Beacon.PROJECTION, null, null, null);

            if (Utility.DEBUG) {

            }

        }

        initUI();

    }

    private void initUI() {
        reconfig = (Button) findViewById(R.id.info_reconfig);
        delete = (Button) findViewById(R.id.info_delete);
        reconfig.setOnClickListener(new ClickEvent());
        delete.setOnClickListener(new ClickEvent());

        // TODO all textview must be setup

        device = (TextView) findViewById(R.id.device_name);
        deviceId = (TextView) findViewById(R.id.device_id);
        sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorFreq = (TextView) findViewById(R.id.sensor_freq);
        actName = (TextView) findViewById(R.id.actuator_name);
        actTrigger = (TextView) findViewById(R.id.actuator_trigger);
        actAction = (TextView) findViewById(R.id.actuator_action);
        actCompare = (TextView) findViewById(R.id.actuator_compare);
        actCompareValue = (TextView) findViewById(R.id.actuator_compare_value);

        getBeaconInfo();

    }

    private void getBeaconInfo() {
        mCursor.moveToFirst();
        // Log.d(TAG, mCursor.getColumnCount() + "");

        String[] names = mCursor.getColumnNames();
        int[] ix = new int[names.length];

        for (int i = 0; i < names.length; i++) {
            ix[i] = mCursor.getColumnIndexOrThrow(names[i]);
            names[i] = mCursor.getString(ix[i]);
        }

        device.setText(names[1]);
        deviceId.setText(names[3]);
        actTrigger.setText("Device ID is " + names[8]);
        checkSensorAndFrequency(names[4], names[5]);// done
        checkActuator(names[7]);// done
        checkCompareAndAction(names[9], names[10], names[11]); // done

    }

    private void checkActuator(String actuatorName) {
        String[] act = this.getResources().getStringArray(R.array.actuator);

        int i = Integer.parseInt(actuatorName);
        switch (i) {
        case 0:
            actName.setText(act[0]);
            break;
        case 128:
            actName.setText(act[1]);
            break;
        case 129:
            actName.setText(act[2]);
            break;
        case 134:
            actName.setText(act[3]);
            break;
        case 135:
            actName.setText(act[4]);
            break;
        case 136:
            actName.setText(act[5]);
            break;
        case 137:
            actName.setText(act[6]);
            break;
        case 138:
            actName.setText(act[7]);
            break;
        case 200:
            actName.setText(act[8]);
            break;
        case 201:
            actName.setText(act[9]);
            break;
        case 220:
            actName.setText(act[10]);
            break;
        case 221:
            actName.setText(act[11]);
            break;
        case 230:
            actName.setText(act[12]);
            break;
        default:
            break;
        }
    }

    private void checkSensorAndFrequency(String name, String frq) {
        String[] sensor = this.getResources().getStringArray(R.array.sensor);

        for (int i = 0; i < sensor.length; i++) {
            Log.d(TAG, "sensor string: " + sensor[i]);
        }

        int i = Integer.parseInt(name);
        switch (i) {
        case 0:
            sensorName.setText(sensor[0]);
            break;
        case 1:
            sensorName.setText(sensor[1]);
            break;
        case 2:
            sensorName.setText(sensor[2]);
            break;
        case 3:
            sensorName.setText(sensor[3]);
            break;
        case 4:
            sensorName.setText(sensor[4]);
            break;
        case 5:
            sensorName.setText(sensor[5]);
            break;
        case 6:
            sensorName.setText(sensor[6]);
            break;
        case 7:
            sensorName.setText(sensor[7]);
            break;
        case 8:
            sensorName.setText(sensor[8]);
            break;
        case 9:
            sensorName.setText(sensor[9]);
            break;
        case 10:
            sensorName.setText(sensor[10]);
            break;
        case 11:
            sensorName.setText(sensor[11]);
            break;
        case 12:
            sensorName.setText(sensor[12]);
            break;
        case 13:
            sensorName.setText(sensor[13]);
            break;
        case 14:
            sensorName.setText(sensor[14]);
            break;
        case 44:
            sensorName.setText(sensor[15]);
            break;
        case 45:
            sensorName.setText(sensor[16]);
            break;
        case 46:
            sensorName.setText(sensor[17]);
            break;
        case 47:
            sensorName.setText(sensor[18]);
            break;
        case 48:
            sensorName.setText(sensor[19]);
            break;
        case 49:
            sensorName.setText(sensor[20]);
            break;
        case 50:
            sensorName.setText(sensor[21]);
            break;
        case 51:
            sensorName.setText(sensor[21]);
            break;
        case 53:
            sensorName.setText(sensor[23]);
            break;
        case 54:
            sensorName.setText(sensor[24]);
            break;
        default:
            break;
        }

        i = Integer.parseInt(frq);
        switch (i) {
        case Atom.FRQ_FAST:
            sensorFreq.setText("Fast(100ms)");
            break;
        case Atom.FRQ_LOW:
            sensorFreq.setText("Low(1min)");
            break;
        case Atom.FRQ_MINI:
            sensorFreq.setText("Minimun(1hour)");
            break;
        case Atom.FRQ_STD:
            sensorFreq.setText("Standard(1s)");
            break;
        case Atom.FRQ_NULL:
            sensorFreq.setText("Null");
            break;
        default:
            break;
        }
    }

    private void checkCompareAndAction(String action, String compare,
            String compareValue) {
        String[] actions = this.getResources().getStringArray(
                R.array.action_type);

        int i = Integer.parseInt(action);
        switch (i) {
        case Atom.ACTIN_TYPE_OFF:
            actAction.setText(actions[0]);
            break;
        case Atom.ACTIN_TYPE_ON:
            actAction.setText(actions[1]);
        default:
            break;
        }

        String[] compares = this.getResources().getStringArray(
                R.array.compare_type);
        i = Integer.parseInt(compare);
        switch (i) {
        case Atom.COMPARE_TYEP_EQUAL:
            actCompare.setText(compares[2]);
            break;
        case Atom.COMPARE_TYEP_GREATER:
            actCompare.setText(compares[0]);
            break;
        case Atom.COMPARE_TYEP_LESS:
            actCompare.setText(compares[1]);
            break;
        case Atom.COMPARE_TYEP_NULL:
            actCompare.setText(compares[3]);
            break;
        default:
            break;
        }

        actCompareValue.setText(compareValue);
    }

    private class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO reconfigure and delete beacon which be store in database
            switch (v.getId()) {
            case R.id.info_reconfig:
                Toast.makeText(getApplicationContext(), "Reconfigure",
                        Toast.LENGTH_SHORT).show();
                // TODO jump to setup activity
                reconfigureBeacon();
                BeaconInfoActivity.this.finish();
                break;
            case R.id.info_delete:
                Toast.makeText(getApplicationContext(), "Delete it",
                        Toast.LENGTH_SHORT).show();
                deleteBeacon();
                BeaconInfoActivity.this.finish();
                break;
            default:
                break;
            }
        }

    }

    private void reconfigureBeacon() {
        startActivity(new Intent(Intent.ACTION_EDIT, mUri));
    }

    private final void deleteBeacon() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
