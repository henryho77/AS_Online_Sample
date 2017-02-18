package com.example.henryho.multitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Activity_1 extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        setTitle("Activity_1");

        textView = (TextView) findViewById(R.id.textView);
        textView.setText(getIntent().getStringExtra("TEXT"));
    }
}
