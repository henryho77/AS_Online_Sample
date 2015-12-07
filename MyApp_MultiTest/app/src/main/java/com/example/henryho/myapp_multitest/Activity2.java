package com.example.henryho.myapp_multitest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class Activity2 extends Activity {

    private Button button2;
    private Button button3;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);

        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.button2:
                        setResult(RESULT_OK);
                        break;
                    case R.id.button3:
                        Intent intent = new Intent();
                        intent.putExtra("MSG", "Activity2 back with intent message");
                        setResult(RESULT_OK, intent);
                        break;
                }
                finish();
            }
        };

        button2.setOnClickListener(onClickListener);
        button3.setOnClickListener(onClickListener);
    }


}
