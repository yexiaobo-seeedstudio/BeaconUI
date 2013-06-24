package com.seeedstudio.beacon.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.seeedstudio.library.Atom;
import com.seeedstudio.library.State;

public class ConfigureActivity extends Activity {

    // debugging
    private static final boolean D = true;
    private static final String TAG = "Configure Activity";

    public final static String CONFIG_KEY_ATOM = "com.seeedstudio.beacon.ui.configure.atom";
    public final static String CONFIG_KEY = "com.seeedstudio.beacon.ui.configure";

    private Button prev, next;
    private RadioGroup unitGroup;
    private RadioButton unitMs, unitS, unitMin, unitHour;
    private View device, sensor, actuator;
    private static View flash;
    private int layout = 0;

    private Atom atom = new Atom("Beacon", "Desc", 0);
    private State state;
    private static int mColor = Color.WHITE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (D)
            Log.d(TAG, "ConfigureActivity onCreate()");
        super.onCreate(savedInstanceState);
        setTitle("Configure Beacon");
        setContentView(R.layout.activity_configure);

        // Set result CANCELED incase the user backs out
        // setResult(Activity.RESULT_CANCELED);

        state = new State(getApplicationContext(), mHandler);
        atom = (Atom) getIntent().getSerializableExtra(CONFIG_KEY);
        if (D)
            Log.d(TAG,
                    "atom name: " + atom.getName() + "\natom Desc: "
                            + atom.getDescription());

        // TODO refactor with SwitchViewer
        initUI();
    }

    private void initUI() {

        device = findViewById(R.id.device_layout);
        sensor = findViewById(R.id.sensor_layout);
        actuator = findViewById(R.id.act_layout);

        prev = (Button) findViewById(R.id.prev);
        next = (Button) findViewById(R.id.next);
        prev.setOnClickListener(new ClickEvent());
        next.setOnClickListener(new ClickEvent());
    }

    private class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.prev:
                if (layout > 0 && layout < 3) {
                    layout--;
                    active();
                }
                break;
            case R.id.next:
                if (layout >= 0 && layout < 3) {
                    if (D)
                        Log.d(TAG, "layout id: " + layout);
                    if (getData()) {
                        layout++;
                        active();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Your forgot input Beacon infomation :P",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
            }
        }
    }

    private void active() {
        switch (layout) {
        case 0:
            activeDevice();
            break;
        case 1:
            activeSensor();
            break;
        case 2:
            activeActuator();
            break;
        case 3:
            Doit();
            break;
        default:
            break;
        }
    }

    private boolean getData() {
        switch (layout) {
        case 0:
            return getDeviceData();
        case 1:
            return getSensorData();
        case 2:
            return getActuatorData();
        case 3:
            return true;
        default:
            break;
        }
        return false;
    }

    private void activeDevice() {
        setTitle("1/4, Device Info");
        actuator.setVisibility(View.GONE);
        sensor.setVisibility(View.GONE);
        device.setVisibility(View.VISIBLE);
    }

    private void activeSensor() {
        setTitle("2/4, Sensor Info");
        actuator.setVisibility(View.GONE);
        device.setVisibility(View.GONE);
        sensor.setVisibility(View.VISIBLE);
    }

    private void activeActuator() {
        setTitle("3/4, Actuator Info");
        sensor.setVisibility(View.GONE);
        device.setVisibility(View.GONE);
        actuator.setVisibility(View.VISIBLE);
        next.setText("Start paire");
    }

    private void Doit() {
        setTitle("4/4, Pairing");
        sensor.setVisibility(View.GONE);
        device.setVisibility(View.GONE);
        actuator.setVisibility(View.GONE);
        prev.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        startCountDown();
    }

    private boolean getDeviceData() {
        EditText deviceName = (EditText) findViewById(R.id.device);
        if (!deviceName.getText().toString().equals("")) {
            atom.setName(deviceName.getText().toString());
            return true;
        }
        return false;

    }

    private boolean getSensorData() {
        EditText sensorName = (EditText) findViewById(R.id.sensor);
        EditText sensorFreq = (EditText) findViewById(R.id.freq);

        unitGroup = (RadioGroup) findViewById(R.id.unit_group);
        // unitGroup.setOnCheckedChangeListener(new RadioGroupEvent());

        unitMs = (RadioButton) findViewById(R.id.unit_ms);
        unitS = (RadioButton) findViewById(R.id.unit_s);
        unitMin = (RadioButton) findViewById(R.id.unit_min);
        unitHour = (RadioButton) findViewById(R.id.unit_hour);

        if (!sensorName.getText().toString().equals("")
                && !sensorFreq.getText().equals("")) {
            // atom.setSensorId(sensorName.getText().toString());
            atom.setSensorFrequency(Integer.parseInt(sensorFreq.getText()
                    .toString()));
            return true;
        }
        return false;

    }

    private boolean getActuatorData() {
        EditText actName = (EditText) findViewById(R.id.act);
        EditText actCompareValue = (EditText) findViewById(R.id.value);
        RadioGroup actionGroup = (RadioGroup) findViewById(R.id.action_group);
        RadioGroup compareGroup = (RadioGroup) findViewById(R.id.compare_group);
        // actionGroup.setOnCheckedChangeListener(new RadioGroupEvent());
        // compareGroup.setOnCheckedChangeListener(new RadioGroupEvent());

        RadioButton actionOn = (RadioButton) findViewById(R.id.action_on);
        RadioButton actionOff = (RadioButton) findViewById(R.id.action_off);

        RadioButton compareGreater = (RadioButton) findViewById(R.id.compare_greater);
        RadioButton compareLess = (RadioButton) findViewById(R.id.compare_less);
        RadioButton compareEqual = (RadioButton) findViewById(R.id.compare_equal);
        RadioButton compareNone = (RadioButton) findViewById(R.id.compare_none);

        if (!actName.getText().toString().equals("")
                && !actCompareValue.getText().toString().equals("")) {
            // atom.setActuatorId(actName.getText().toString());
            atom.setActuatorCompareValue(Integer.parseInt(actCompareValue
                    .getText().toString()));
            return true;
        }

        return false;

    }

    public void onRadioEvent(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {

            switch (view.getId()) {
            // sensor unit
            case R.id.unit_ms:
                atom.setSensorUnit(Atom.UNIT_MS);
                // if (D)
                // Log.d(TAG, "unit ms is selectied");
                Toast.makeText(getApplicationContext(), "unit is ms",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.unit_s:
                atom.setSensorUnit(Atom.UNIT_S);
                Toast.makeText(getApplicationContext(), "unit is s",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.unit_min:
                atom.setSensorUnit(Atom.UNIT_MIN);
                Toast.makeText(getApplicationContext(), "unit is min",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.unit_hour:
                atom.setSensorUnit(Atom.UNIT_HOUR);
                Toast.makeText(getApplicationContext(), "unit is hour",
                        Toast.LENGTH_SHORT).show();
                break;
            // actuator action
            case R.id.action_on:
                atom.setActuatorAction(Atom.ACTIN_TYPE_ON);
                Toast.makeText(getApplicationContext(), "action is on",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_off:
                atom.setActuatorAction(Atom.ACTIN_TYPE_OFF);
                Toast.makeText(getApplicationContext(), "action is off",
                        Toast.LENGTH_SHORT).show();
                break;

            // actuator compare
            case R.id.compare_greater:
                atom.setActuatorCompare(Atom.COMPARE_TYEP_GREATER);
                Toast.makeText(getApplicationContext(), "compare is >",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.compare_less:
                atom.setActuatorCompare(Atom.COMPARE_TYEP_LESS);
                Toast.makeText(getApplicationContext(), "compare is <",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.compare_equal:
                atom.setActuatorCompare(Atom.COMPARE_TYEP_EQUAL);
                Toast.makeText(getApplicationContext(), "compare is =",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.compare_none:
                atom.setActuatorCompare(Atom.COMPARE_TYEP_NULL);
                Toast.makeText(getApplicationContext(), "compare is None",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }
    }

    private void startCountDown() {

        getApplication().setTheme(R.style.Theme_Configure);

        // Start countdown.
        View v = findViewById(R.id.record_overlay);
        v.setVisibility(View.VISIBLE);

        TextView tv = (TextView) findViewById(R.id.record_countdown);
        tv.setVisibility(View.VISIBLE);

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

            // start communicating
            startFlash();

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

    private void startFlash() {
        View overflash = findViewById(R.id.overflash);
        overflash.setVisibility(View.VISIBLE);
        flash = findViewById(R.id.flash);
        flash.setVisibility(View.VISIBLE);

        state.addAtom(atom);
        state.prepare();
        state.start();
    }

    private void readingResult(final int step) {
        View block = findViewById(R.id.overflash);

        if (step == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle mBundle = new Bundle();
            // mBundle.putSerializable(CONFIG_KEY, atom);
            // intent.putExtras(mBundle);
            intent.putExtra(CONFIG_KEY_ATOM, atom);
            setResult(Activity.RESULT_FIRST_USER, intent);
            // setResult(Activity.RESULT_OK, intent);
            if (D)
                Log.d(TAG, "set configure result");
            finish();
        }

        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.countdown);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                // When the player finished fading out, stop capturing clicks.
                readingResult(step - 1);
            }

            public void onAnimationRepeat(Animation animation) {
                // pass
            }

            public void onAnimationStart(Animation animation) {
                // pass
            }
        });
        block.startAnimation(animation);
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
                readingResult(1);
                break;
            }
        }

    };
}