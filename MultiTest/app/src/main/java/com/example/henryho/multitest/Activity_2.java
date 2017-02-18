package com.example.henryho.multitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Activity_2 extends AppCompatActivity {

    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        setTitle("Activity_2");

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        ButtonListener listener = new ButtonListener();
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button1:
                    setResult(RESULT_OK);
                    break;
                case R.id.button2:
                    Intent intent = new Intent();
                    intent.putExtra("MSG", "Activity_2透過Intent夾帶回來的訊息");
                    setResult(RESULT_OK, intent);
                    break;
            }
            finish();
        }
    }
}
