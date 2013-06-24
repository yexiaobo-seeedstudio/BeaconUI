package com.seeedstudio.beacon.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.seeedstudio.beacon.ui.widget.CornerListView;
import com.seeedstudio.library.Atom;

public class SensorListActivity extends SherlockActivity {

    // public static final String SENSOR_KEY = "com.seeedstudio.beacon.sensor";
    public static final int NORMAL_CONFIG_REQUEST_CODE = 0;
    public static final int EXPORT_CONFIG_REQUEST_CODE = 1;

    CornerListView sensorList;
    private List<Map<String, String>> listData = null;
    private SimpleAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        setResult(Activity.RESULT_CANCELED);
        initUI();

    }

    private void initUI() {
        sensorList = (CornerListView) findViewById(R.id.sensor_list);

        setTestListData();

        // init adapter
        adapter = new SimpleAdapter(this, listData, R.layout.corner_listview,
                new String[] { "title", "subtitle" }, new int[] {
                        R.id.corner_listview_title,
                        R.id.corner_listview_subtitle });
        sensorList.setAdapter(adapter);
        sensorList.setOnItemClickListener(new ListClickEvent());
    }

    @Override
    protected void onPause() {
        Log.d("SensorListActivity", "onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("SensorListActivity", "onResume()");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("SensorListActivity", "onStop()");
        super.onStop();
    }

    private void setTestListData() {
        listData = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("title", "Beacon Sensor Test");
        map.put("subtitle", "description");
        listData.add(map);

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("title", "Beacon Sensor Test 2");
        map1.put("subtitle", "description");
        listData.add(map1);

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("title", "Beacon Sensor Test");
        map2.put("subtitle", "description");
        listData.add(map2);

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("title", "Beacon Sensor Test 2");
        map3.put("subtitle", "description");
        listData.add(map3);

        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("title", "Beacon Sensor Test");
        map4.put("subtitle", "description");
        listData.add(map4);

        Map<String, String> map5 = new HashMap<String, String>();
        map5.put("title", "Beacon Sensor Test 2");
        map5.put("subtitle", "description");
        listData.add(map5);

        Map<String, String> map6 = new HashMap<String, String>();
        map6.put("title", "Beacon Sensor Test");
        map6.put("subtitle", "description");
        listData.add(map6);

        Map<String, String> map7 = new HashMap<String, String>();
        map7.put("title", "Beacon Sensor Test 2");
        map7.put("subtitle", "description");
        listData.add(map7);
    }

    private class ListClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, int arg2,
                long arg3) {
//            Object o = parent.getItemAtPosition(arg2);
//            String str = (String) o;
//            Toast.makeText(getApplicationContext(), str + " is Selected",
//                    Toast.LENGTH_SHORT).show();
            beaconSetup(v);
        }
    }

    private void beaconSetup(View v) {
        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(MainActivity.SELECTED_BEACON, new Atom(v
                .getResources().getText(R.id.corner_listview_title).toString(),
                v.getResources().getText(R.id.corner_listview_subtitle)
                        .toString(), 0));
        intent.putExtras(mBundle);
        setResult(Activity.RESULT_FIRST_USER, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("SensorListActivity", "onActivityResult()");

        switch (requestCode) {
        case NORMAL_CONFIG_REQUEST_CODE:

            Log.d("SensorListActivity", "normal config get data");
            if (data != null) {
                setResult(Activity.RESULT_FIRST_USER, data);
                finish();
            }

            break;
        case EXPORT_CONFIG_REQUEST_CODE:

            Log.d("SensorListActivity", "export config get data");
            if (data != null) {
                setResult(Activity.RESULT_FIRST_USER, data);
                finish();
            }

            break;
        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_pop_out);
    }
}
