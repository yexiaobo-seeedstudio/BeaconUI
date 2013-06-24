package com.seeedstudio.beacon.ui.tutor;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seeedstudio.beacon.ui.MainActivity;
import com.seeedstudio.beacon.ui.R;

public class TutorActivity extends Activity {

    ViewFlow tutorViewflow;
    TutorAdapter tutorAdapter;
    Button tutorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor);

        // setup viewflow
        tutorViewflow = (ViewFlow) findViewById(R.id.tutor_viewflow);
        tutorAdapter = new TutorAdapter(this);
        tutorViewflow.setAdapter(tutorAdapter);
        // set indicator circle point
        CircleFlowIndicator indicator = (CircleFlowIndicator) findViewById(R.id.tutor_viewflow_indicator);
        tutorViewflow.setFlowIndicator(indicator);

        // last viewflow start MainActivity
        tutorViewflow.setOnViewSwitchListener(new ViewSwitchListener() {

            @Override
            public void onSwitched(View view, int position) {

                if (position == (tutorAdapter.getCount() - 1)) {
                    tutorButton.setText(R.string.start_button);
                } else {
                    tutorButton.setTag(R.string.tutor_button);
                }
                // Log.d("Tutor",
                // "view id: " + view.getId() + ", position: " + position
                // + ", tutorViewlfow id: "
                // + tutorViewflow.getId()
                // + ", tutorAdapter count: "
                // + tutorAdapter.getCount()
                // + ", tutorViewflow item: "
                // + tutorViewflow.getItemAtPosition(position));
            }
        });

        tutorButton = (Button) findViewById(R.id.tutor_skip);
        tutorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorActivity.this,
                        MainActivity.class);
                startActivity(intent);
                TutorActivity.this.finish();
            }
        });

    }

    /*
     * If your min SDK version is < 8 you need to trigger the
     * onConfigurationChanged in ViewFlow manually, like this
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tutorViewflow.onConfigurationChanged(newConfig);
    }
}
