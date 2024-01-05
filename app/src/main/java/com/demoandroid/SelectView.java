package com.demoandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.demoandroid.activities.DrawerActivity;

public class SelectView extends AppCompatActivity {

    Button button,button1,button2;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            i = new Intent(SelectView.this, DrawerActivity.class);
            startActivity(i);
        });
    }
}