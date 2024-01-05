package com.demoandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.demoandroid.R;
import com.demoandroid.SelectView;
import com.demoandroid.services.ApiClient;
import com.demoandroid.services.Utilities;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, String> tokenMap = ApiClient.getHeaders(true, getApplicationContext());
                Intent i;
                if (!(tokenMap.get("Token").isEmpty())) {
                    i = new Intent(SplashActivity.this, SelectView.class);
                }else {
                    i = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, 2000);

    }
}