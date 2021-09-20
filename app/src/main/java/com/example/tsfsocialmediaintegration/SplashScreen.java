package com.example.tsfsocialmediaintegration;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    TextView designed, name, app_name;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        designed = findViewById(R.id.designed);
        name = findViewById(R.id.name);
        app_name = findViewById(R.id.app_name);

        new Handler().postDelayed(this::startEnterAnimation, 3000);

        new Handler().postDelayed(() -> {


            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }, 8000);
    }

    private void startEnterAnimation() {
        app_name.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom));
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.p_in));
        designed.startAnimation(AnimationUtils.loadAnimation(this, R.anim.p_in));
        name.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom));

        logo.setVisibility(View.VISIBLE);
        designed.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        app_name.setVisibility(View.VISIBLE);
    }
}