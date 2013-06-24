package com.seeedstudio.beacon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.seeedstudio.beacon.ui.tutor.TutorActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this.getApplicationContext(),
                R.layout.splash, null);
        setContentView(view);

        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(3000);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gotoMainActivity();
            }
        });

    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this.getApplicationContext(),
                TutorActivity.class);
        startActivity(intent);
        this.finish();
    }
}
