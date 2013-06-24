package com.seeedstudio.beacon.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.seeedstudio.beacon.utility.BeaconApp;
import com.seeedstudio.library.Atom;
import com.seeedstudio.library.State;
import com.seeedstudio.library.Utility;

public class NormalSetupActivity extends Activity {
    // debugging
    private static final boolean D = Utility.DEBUG;
    private static final String TAG = "NormalSetupActivity";

    public static final String NORMAL_CONFIG_KEY = "com.seeedstudio.beacon.ui.normal.configure.atom";

    // This Activity can be started by more than one action. Each action is
    // represented
    // as a "state" constant
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private ViewSwitcher mViewSwitcher;
    private ScrollView mScrollView;
    private Button cancel, submit, yes, again;
    private Spinner sensorSpinner, sensorFrqSpinner, actuatorSpinner,
            triggerSpinner, compareAction, actionSpinner;
    private EditText deviceName, compareValue;
    private View flash, setupAll, setupSubmit;
    private Uri mUri;
    private Cursor mCursor = null, triggerCursor = null;
    private Atom atom = null;
    private State state;

    private int mColor = Color.WHITE;
    private int mContentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_setup);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (intent.hasExtra(MainActivity.SELECTED_BEACON)) {
            atom = (Atom) getIntent().getSerializableExtra(
                    MainActivity.SELECTED_BEACON);
            Toast.makeText(getApplicationContext(), "get atom from itent",
                    Toast.LENGTH_SHORT).show();
        }

        initUI();

        if (Intent.ACTION_INSERT.equals(action)) {
            if (D)
                Log.d(TAG, "action is " + action.toString());

            mContentState = STATE_INSERT;
            // mUri = getContentResolver().insert(BeaconApp.Beacon.CONTENT_URI,
            // null);
            mUri = intent.getData();
            if (D)
                Log.d(TAG, "getData Uri:" + mUri);

            if (mUri == null) {

                // Writes the log identifier, a message, and the URI that
                // failed.
                Log.e(TAG, "Failed to query the beacon paried database "
                        + getIntent().getData());

                // Closes the activity.
                finish();
                return;
            }

            // Since the new entry was created, this sets the result to be
            // returned
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));

            // get the cursor for manager database
            mCursor = getContentResolver().query(mUri,
                    BeaconApp.Beacon.PROJECTION, null, null, null);
            if (mCursor.moveToFirst()) {
                if (D)
                    Log.d(TAG, "mCursor moveToFirst() ");

                int index = mCursor
                        .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID);
                int id = mCursor.getInt(index);

                atom = new Atom("Device Name", "new beacon", id + 1);

                if (D)
                    Log.d(TAG, "mCursor moveToFirst(), id: " + id
                            + ", and new atom id is: " + id + 1);
            } else {
                if (D)
                    Log.d(TAG, "mCursor is empty ");

                // insert "NULL"
                ContentValues v = new ContentValues();
                v.put(BeaconApp.Beacon.COLUMN_NAME_TITLE, "NULL");
                v.put(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID, "0");
                getContentResolver().insert(mUri, v);
                atom = new Atom("Device Name", "new beacon", 1);
            }

        } else if (Intent.ACTION_EDIT.equals(action)) {

            mContentState = STATE_EDIT;
            mUri = intent.getData();

            if (D)
                Log.d(TAG, "action is " + action.toString() + ", uri: " + mUri
                        + ", uri last segment: " + mUri.getLastPathSegment());

            // get the cursor for manager database
            mCursor = getContentResolver().query(mUri,
                    BeaconApp.Beacon.PROJECTION, null, null, null);
            reconfigureBeacon();
        } else {

            // Logs an error that the action was not understood, finishes the
            // Activity, and
            // returns RESULT_CANCELED to an originating Activity.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }
        initData();
        state = new State(getApplicationContext(), mHandler);

    }

    private void initUI() {
        // flash layout
        flash = findViewById(R.id.setup_flash);
        // flash.setVisibility(View.VISIBLE);

        setupAll = findViewById(R.id.setup_all);
        // view switcher
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.setup_viewswitcher);
        // scrollView
        mScrollView = (ScrollView) findViewById(R.id.setup_scrollview);
        // setup submit
        setupSubmit = findViewById(R.id.setup_submit);

        // button
        cancel = (Button) findViewById(R.id.normal_cancel);
        submit = (Button) findViewById(R.id.normal_submit);
        cancel.setOnClickListener(new ClickEvent());
        submit.setOnClickListener(new ClickEvent());
        //
        yes = (Button) findViewById(R.id.yes);
        again = (Button) findViewById(R.id.again);
        yes.setOnClickListener(new ClickEvent());
        again.setOnClickListener(new ClickEvent());

        // edittext
        deviceName = (EditText) findViewById(R.id.setup_name_text);
        compareValue = (EditText) findViewById(R.id.setup_action_if_text);

        // spinner
        triggerSpinner = (Spinner) findViewById(R.id.setup_trigger_spinner);
        compareAction = (Spinner) findViewById(R.id.setup_action_if_spinner);
        actionSpinner = (Spinner) findViewById(R.id.setup_action_then_spinner);
        sensorSpinner = (Spinner) findViewById(R.id.setup_sensor_spinner);
        sensorFrqSpinner = (Spinner) findViewById(R.id.setup_sensor_frq_spinner);
        actuatorSpinner = (Spinner) findViewById(R.id.setup_actuator_spinner);

        // sensor
        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.sensor));
        sensorAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpinner.setAdapter(sensorAdapter);
        sensorSpinner.setOnItemSelectedListener(new SpinnerEvent());

        // frequency
        ArrayAdapter<String> sensorFrqAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.frq));
        sensorFrqAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorFrqSpinner.setAdapter(sensorFrqAdapter);
        sensorFrqSpinner.setOnItemSelectedListener(new SpinnerEvent());

        // actuator
        ArrayAdapter<String> actuatorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.actuator));
        actuatorAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actuatorSpinner.setAdapter(actuatorAdapter);
        actuatorSpinner.setOnItemSelectedListener(new SpinnerEvent());

        // compare and action
        ArrayAdapter<String> compareAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.compare_type));
        compareAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compareAction.setAdapter(compareAdapter);
        compareAction.setOnItemSelectedListener(new SpinnerEvent());

        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.action_type));
        actionAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(actionAdapter);
        actionSpinner.setOnItemSelectedListener(new SpinnerEvent());

    }

    private void initData() {

        triggerCursor = getContentResolver().query(
                BeaconApp.Beacon.CONTENT_URI, BeaconApp.Beacon.PROJECTION,
                null, null, null);

        String[] dataColumns = { BeaconApp.Beacon.COLUMN_NAME_TITLE };
        int[] viewIDs = { android.R.id.text1 };
        // trigger get from database.
        if (D)
            Log.d(TAG, "triggerAdapter init");
        SimpleCursorAdapter triggerAdapter = new SimpleCursorAdapter(
                getApplicationContext(), R.layout.list, triggerCursor,
                dataColumns, viewIDs, 0);
        triggerAdapter.setDropDownViewResource(R.layout.spiner_dropdown);
        triggerSpinner.setAdapter(triggerAdapter);
        triggerSpinner.setOnItemSelectedListener(new SpinnerEvent());

        if (mContentState == STATE_EDIT) {
            // TODO get trigger Beacon name

            // if (mCursor.moveToFirst()) {
            // int index = mCursor
            // .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE);
            // }

            // String sourceId = mCursor.getString(index);
            // triggerCursor.moveToFirst();
            //
            // index = triggerCursor
            // .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE);
            // triggerCursor.getColumnName(index);
            //
            // triggerSpinner.setSelection(triggerCursor.getPosition());
        }

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
    }

    private class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.normal_cancel:
                NormalSetupActivity.this.finish();
                break;
            case R.id.normal_submit:
                if (checkTextValue()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    if (imm.isActive()) {

                    }
                    startCountDown();
                }
                break;
            case R.id.yes:
                if (D)
                    Log.d(TAG, "yes, to insert data");
                insertData(atom);
                break;
            case R.id.again:
                if (D)
                    Log.d(TAG, "no, try again");
                // mScrollView.setVisibility(View.VISIBLE);
                // mViewSwitcher.setVisibility(View.VISIBLE);
                setupSubmit.setVisibility(View.GONE);
                // // setupAll.setVisibility(View.GONE);
                // // flash.setVisibility(View.VISIBLE);
                // startFlash(atom);

                mViewSwitcher.showPrevious();
                startCountDown();
                break;

            default:
                break;
            }
        }

    }

    private class SpinnerEvent implements Spinner.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                long id) {
            switch (parent.getId()) {
            case R.id.setup_sensor_spinner:
                switch (position) {
                case 0:
                    atom.setSensorId(0);
                    break;
                case 1:
                    atom.setSensorId(1);
                    break;
                case 2:
                    atom.setSensorId(2);
                    break;
                case 3:
                    atom.setSensorId(3);
                    break;
                case 4:
                    atom.setSensorId(4);
                    break;
                case 5:
                    atom.setSensorId(5);
                    break;
                case 6:
                    atom.setSensorId(6);
                    break;
                case 7:
                    atom.setSensorId(7);
                    break;
                case 8:
                    atom.setSensorId(8);
                    break;
                case 9:
                    atom.setSensorId(9);
                    break;
                case 10:
                    atom.setSensorId(10);
                    break;
                case 11:
                    atom.setSensorId(11);
                    break;
                case 12:
                    atom.setSensorId(12);
                    break;
                case 13:
                    atom.setSensorId(13);
                    break;
                case 14:
                    atom.setSensorId(14);
                    break;
                case 15:
                    atom.setSensorId(44);
                    break;
                case 16:
                    atom.setSensorId(45);
                    break;
                case 17:
                    atom.setSensorId(46);
                    break;
                case 18:
                    atom.setSensorId(47);
                    break;
                case 19:
                    atom.setSensorId(48);
                    break;
                case 20:
                    atom.setSensorId(49);
                    break;
                case 21:
                    atom.setSensorId(50);
                    break;
                case 22:
                    atom.setSensorId(51);
                    break;
                case 23:
                    atom.setSensorId(53);
                    break;
                case 24:
                    atom.setSensorId(54);
                    break;
                case 25:
                    atom.setSensorId(82);
                    break;
                case 26:
                    atom.setSensorId(101);
                    break;
                case 27:
                    atom.setSensorId(102);
                    break;
                case 28:
                    atom.setSensorId(103);
                    break;
                case 29:
                    atom.setSensorId(105);
                    break;
                case 30:
                    atom.setSensorId(110);
                    break;
                default:
                    break;
                }
                break;
            case R.id.setup_actuator_spinner:
                switch (position) {
                case 0:
                    atom.setActuatorId(0);
                    break;
                case 1:
                    atom.setActuatorId(128);
                    break;
                case 2:
                    atom.setActuatorId(129);
                    break;
                case 3:
                    atom.setActuatorId(134);
                    break;
                case 4:
                    atom.setActuatorId(135);
                    break;
                case 5:
                    atom.setActuatorId(136);
                    break;
                case 6:
                    atom.setActuatorId(137);
                    break;
                case 7:
                    atom.setActuatorId(138);
                    break;
                case 8:
                    atom.setActuatorId(200);
                    break;
                case 9:
                    atom.setActuatorId(201);
                    break;
                case 10:
                    atom.setActuatorId(220);
                    break;
                case 11:
                    atom.setActuatorId(221);
                    break;
                case 12:
                    atom.setActuatorId(230);
                    break;
                case 13:
                    atom.setActuatorId(231);
                    break;
                case 14:
                    atom.setActuatorId(232);
                    break;
                default:
                    break;
                }
                break;
            case R.id.setup_sensor_frq_spinner:
                switch (position) {
                case 0:
                    atom.setSensorFrequency(Atom.FRQ_NULL);
                    break;
                case 1:
                    atom.setSensorFrequency(Atom.FRQ_STD);
                    break;
                case 2:
                    atom.setSensorFrequency(Atom.FRQ_FAST);
                    break;
                case 3:
                    atom.setSensorFrequency(Atom.FRQ_LOW);
                    break;
                case 4:
                    atom.setSensorFrequency(Atom.FRQ_MINI);
                    break;
                default:
                    break;
                }
                break;
            case R.id.setup_trigger_spinner:

                if (mContentState == STATE_INSERT) {
                    if (!mCursor.moveToFirst()) {
                        if (D)
                            Log.d(TAG, "mCursor is " + "empty");

                        atom.setActuatorTrigger(0);
                        return;
                    }

                    mCursor.moveToPosition(position);
                    int index = mCursor
                            .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID);
                    int deviceId = mCursor.getInt(index);
                    atom.setActuatorTrigger(deviceId);
                } else if (mContentState == STATE_EDIT) {

                    triggerCursor.moveToPosition(position);
                    int index = triggerCursor
                            .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID);
                    // int deviceId =
                    // Integer.parseInt(mCursor.getString(index));
                    int deviceId = triggerCursor.getInt(index);
                    atom.setActuatorTrigger(deviceId);
                }

                break;
            case R.id.setup_action_if_spinner:
                switch (position) {
                case 0:
                    atom.setActuatorCompare(Atom.COMPARE_TYEP_GREATER);
                    break;
                case 1:
                    atom.setActuatorCompare(Atom.COMPARE_TYEP_LESS);
                    break;
                case 2:
                    atom.setActuatorCompare(Atom.COMPARE_TYEP_EQUAL);
                    break;
                case 3:
                    atom.setActuatorCompare(Atom.COMPARE_TYEP_NULL);
                    break;
                default:
                    break;
                }
                break;
            case R.id.setup_action_then_spinner:
                switch (position) {
                case 0:
                    atom.setActuatorAction(Atom.ACTIN_TYPE_OFF);
                    break;
                case 1:
                    atom.setActuatorAction(Atom.ACTIN_TYPE_ON);
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    private void startCountDown() {

        getApplication().setTheme(R.style.Theme_Configure);

        // Start countdown.
        View v = findViewById(R.id.record_overlay);
        v.setVisibility(View.VISIBLE);

        TextView tv = (TextView) findViewById(R.id.record_countdown);
        tv.setVisibility(View.VISIBLE);

        mScrollView.setVisibility(View.GONE);

        // Play countdown
        MediaPlayer mp = MediaPlayer.create(this, R.raw.countdown);
        mp.start();

        countDownStep(3);
    }

    private void countDownStep(final int step) {
        TextView tv = (TextView) findViewById(R.id.record_countdown);

        // If step has reached zero, hide all countdown-related views and start
        // recording.
        if (0 == step) {
            tv.setVisibility(View.GONE);

            View v = findViewById(R.id.record_overlay);
            v.setVisibility(View.GONE);

            mScrollView.setVisibility(View.VISIBLE);
            mViewSwitcher.showNext();
            mScrollView.setBackgroundColor(Color.BLACK);

            // mViewSwitcher.setVisibility(View.VISIBLE);
            // setupAll.setVisibility(View.GONE);
            // flash.bringToFront();
            // start communicating
            startFlash(atom);

            return;
        }

        // Else display the step..
        tv.setText(String.format("%d", step));

        // ... start a 1 second animation that ends up in this function again
        // ...
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.countdown);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                // When the player finished fading out, stop capturing clicks.
                countDownStep(step - 1);
            }

            public void onAnimationRepeat(Animation animation) {
                // pass
            }

            public void onAnimationStart(Animation animation) {
                // pass
            }
        });
        tv.startAnimation(animation);

        // ... and vibrate, if we're in vibrate mode!
//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (null != am) {
//            if (AudioManager.RINGER_MODE_VIBRATE == am.getRingerMode()) {
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                if (null != v) {
//                    v.vibrate(500);
//                }
//            }
//        }
    }

    private void insertData(Atom atom) {

        if (D)
            Log.d(TAG, "insertData()");

        if (mContentState == STATE_INSERT) {
            insertBeacon(atom);
        } else if (mContentState == STATE_EDIT) {
            updateBeacon(atom);
        }

        this.finish();

    }

    private void setupOk() {
        // Play countdown
        // MediaPlayer mp = MediaPlayer.create(this, R.raw.countdown);
        // mp.start();
//        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        if (null != v) {
//            v.vibrate(500);
//        }

        mScrollView.setVisibility(View.GONE);
        // flash.setVisibility(View.GONE);
        setupSubmit.setVisibility(View.VISIBLE);
    }

    private void reconfigureBeacon() {
        if (mCursor != null) {
            mCursor.moveToFirst();

            String[] names = mCursor.getColumnNames();
            int[] ix = new int[names.length];

            // get all column data for 
            for (int i = 0; i < names.length; i++) {
                // if (Utility.DEBUG)
                // Log.d(TAG, ">>>>>mCursor.getColumnNames(): " + names[i]);
                ix[i] = mCursor.getColumnIndexOrThrow(names[i]);
                // if (Utility.DEBUG)
                // Log.d(TAG, "mCursor.getColumnIndex(" + names[i] + "): "
                // + ix[i]);
                // if (Utility.DEBUG)
                // Log.d(TAG,
                // "mCursor.getString(" + ix[i] + "): "
                // + mCursor.getString(ix[i]));
                names[i] = mCursor.getString(ix[i]);
            }

            // if (Utility.DEBUG) {
            // Log.d(TAG, "reconfigureBeacon(), mCursor.getColumnCount(): "
            // + mCursor.getColumnCount()
            //
            // + ", mCursor.getPosition(): " + mCursor.getPosition());
            // }

            int index = mCursor
                    .getColumnIndex(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID);
            int id = mCursor.getInt(index);

            atom = new Atom(names[1], "new Beacon", id);

            this.deviceName.setText(atom.getName());
            checkSensorAndFrequency(names[4], names[5]);// done
            checkActuator(names[7]);// done
            checkCompareAndAction(names[9], names[10], names[11]); // done
        }

    }

    private void checkCompareAndAction(String action, String compare,
            String compareValue) {
        int i = Integer.parseInt(action);
        switch (i) {
        case Atom.ACTIN_TYPE_OFF:
            actionSpinner.setSelection(0);
            break;
        case Atom.ACTIN_TYPE_ON:
            actionSpinner.setSelection(1);
        default:
            break;
        }

        i = Integer.parseInt(compare);
        switch (i) {
        case Atom.COMPARE_TYEP_EQUAL:
            compareAction.setSelection(2);
            break;
        case Atom.COMPARE_TYEP_GREATER:
            compareAction.setSelection(0);
            break;
        case Atom.COMPARE_TYEP_LESS:
            compareAction.setSelection(1);
            break;
        case Atom.COMPARE_TYEP_NULL:
            compareAction.setSelection(3);
            break;
        default:
            break;
        }

        this.compareValue.setText(compareValue);
    }

    private void checkActuator(String actuatorName) {
        if (Utility.DEBUG)
            Log.d(TAG, "checkActuator: " + actuatorName);
        int i = Integer.parseInt(actuatorName);
        switch (i) {
        case 0:
            actuatorSpinner.setSelection(0);
            break;
        case 128:
            actuatorSpinner.setSelection(1);
            break;
        case 129:
            actuatorSpinner.setSelection(2);
            break;
        case 137:
            actuatorSpinner.setSelection(3);
            break;
        case 138:
            actuatorSpinner.setSelection(4);
            break;
        case 201:
            actuatorSpinner.setSelection(5);
            break;
        default:
            break;
        }
    }

    private void checkSensorAndFrequency(String sensorName, String frq) {
        int i = Integer.parseInt(sensorName);
        switch (i) {
        case 0:
            sensorSpinner.setSelection(0);
            break;
        case 48:
            sensorSpinner.setSelection(1);
            break;
        case 82:
            sensorSpinner.setSelection(2);
            break;
        case 102:
            sensorSpinner.setSelection(3);
            break;
        case 103:
            sensorSpinner.setSelection(4);
            break;
        case 1:
            sensorSpinner.setSelection(5);
            break;
        case 13:
            sensorSpinner.setSelection(6);
            break;
        case 4:
            sensorSpinner.setSelection(7);
            break;
        case 45:
            sensorSpinner.setSelection(8);
            break;
        case 53:
            sensorSpinner.setSelection(9);
            break;
        case 8:
            sensorSpinner.setSelection(10);
            break;
        case 44:
            sensorSpinner.setSelection(11);
            break;
        case 12:
            sensorSpinner.setSelection(12);
            break;
        default:
            break;
        }

        i = Integer.parseInt(frq);
        switch (i) {
        case Atom.FRQ_FAST:
            sensorFrqSpinner.setSelection(2);
            break;
        case Atom.FRQ_LOW:
            sensorFrqSpinner.setSelection(3);
            break;
        case Atom.FRQ_MINI:
            sensorFrqSpinner.setSelection(4);
            break;
        case Atom.FRQ_STD:
            sensorFrqSpinner.setSelection(1);
            break;
        case Atom.FRQ_NULL:
            sensorFrqSpinner.setSelection(0);
            break;
        default:
            break;
        }
    }

    private void startFlash(Atom atom) {
        state.addAtom(atom);
        state.prepare();
        state.start();
    }

    private boolean checkTextValue() {

        if (deviceName.getText().toString().equals("")) {
            atom.setName("Defalut Beacon");
        } else {
            String name = deviceName.getText().toString();
            atom.setName(name);
        }

        if (compareValue.getText().toString().equals("")) {
            atom.setActuatorCompareValue(0);
        } else {
            String value = compareValue.getText().toString();
            atom.setActuatorCompareValue(Integer.parseInt(value));
        }

        return true;
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            switch (msg.what) {

            case State.START_DOWN_BIT:
                flash.setBackgroundColor(Color.BLACK);
                if (D) {
                    Log.d(TAG, "start down bit .....");
                }
                break;
            case State.START_UP_BIT:
                flash.setBackgroundColor(mColor);
                if (D) {
                    Log.d(TAG, "start up bit .....");
                }
                break;
            case State.HIGH_DOWN_BIT:
                flash.setBackgroundColor(Color.BLACK);
                if (D) {
                    Log.d(TAG, "high down bit .....");
                }
                break;
            case State.HIGH_UP_BIT:
                flash.setBackgroundColor(mColor);
                if (D) {
                    Log.d(TAG, "high up bit .....");
                }
                break;
            case State.LOW_DOWN_BIT:
                flash.setBackgroundColor(Color.BLACK);
                if (D) {
                    Log.d(TAG, "low down bit .....");
                }
                break;
            case State.LOW_UP_BIT:
                flash.setBackgroundColor(mColor);
                if (D) {
                    Log.d(TAG, "low up bit .....");
                }
                break;
            case State.STATE_NONE:
                flash.setBackgroundColor(mColor);
                if (D) {
                    Log.d(TAG, "state none bit .....");
                }
                break;
            case State.STATE_DONE:
                flash.setBackgroundColor(mColor);
                if (D) {
                    Log.d(TAG, "state done bit .....");
                }
                // insertData(atom);
                setupOk();
                break;
            }
        }

    };

    //

    private final void insertBeacon(Atom atom) {
        // Sets up a map to contain values to be updated in the provider.
        ContentValues values = new ContentValues();
        values.put(BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE,
                System.currentTimeMillis());

        // If the action is to insert a new note, this creates an initial title
        // for it.
        if (mContentState == STATE_INSERT) {
            // In the values map, sets the value of the title
            values.put(BeaconApp.Beacon.COLUMN_NAME_TITLE, atom.getName());
            // This puts the desired notes text into the map.
            values.put(BeaconApp.Beacon.COLUMN_NAME_DESC, atom.getDescription());
            // device id from groove sensor, orgnaizate by Seeed Studio
            values.put(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID, atom.getId());

            // sensor
            values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE,
                    atom.getSensorId());
            values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ,
                    atom.getSensorFrequency());
            values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT,
                    atom.getSensorUnit());

            // actuator
            values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE,
                    atom.getActuatorId());
            values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE,
                    atom.getActuatorTrigger());
            values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION,
                    atom.getActuatorAction());
            values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE,
                    atom.getActuatorCompare());
            values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE,
                    atom.getActuatorCompareValue());
        }

        if (D)
            Log.d(TAG, values.toString());

        if (D)
            Log.d(TAG, "mUri: " + mUri);

        Uri u = getContentResolver().insert(mUri, values);

        if (D)
            Log.d(TAG, "new Uri: " + u);
    }

    private final void updateBeacon(Atom atom) {
        // Sets up a map to contain values to be updated in the provider.
        ContentValues values = new ContentValues();
        values.put(BeaconApp.Beacon.COLUMN_NAME_MODIFICATION_DATE,
                System.currentTimeMillis());

        // If the action is to insert a new note, this creates an initial title
        // for it.
        // In the values map, sets the value of the title
        values.put(BeaconApp.Beacon.COLUMN_NAME_TITLE, atom.getName());
        // This puts the desired notes text into the map.
        values.put(BeaconApp.Beacon.COLUMN_NAME_DESC, atom.getDescription());
        // device id from groove sensor, orgnaizate by Seeed Studio
        values.put(BeaconApp.Beacon.COLUMN_NAME_DEVICE_ID, atom.getId());

        // sensor
        values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_TITLE,
                atom.getSensorId());
        values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_FREQ,
                atom.getSensorFrequency());
        values.put(BeaconApp.Beacon.COLUMN_NAME_SENSOR_UNIT,
                atom.getSensorUnit());

        // actuator
        values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TITLE,
                atom.getActuatorId());
        values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_TRIGGER_SOURCE,
                atom.getActuatorTrigger());
        values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_ACTION,
                atom.getActuatorAction());
        values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE,
                atom.getActuatorCompare());
        values.put(BeaconApp.Beacon.COLUMN_NAME_ACTUATOR_COMPARE_VALUE,
                atom.getActuatorCompareValue());

        if (D)
            Log.d(TAG, values.toString());

        if (D)
            Log.d(TAG, "mUri: " + mUri);

        int u = getContentResolver().update(mUri, values, null, null);

        if (D)
            Log.d(TAG, "new Uri: " + u);
    }

    private final void cancelBeacon(Atom atom) {

    }
}
