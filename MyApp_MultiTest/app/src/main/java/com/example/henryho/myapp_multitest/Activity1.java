package com.example.henryho.myapp_multitest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Activity1 extends Activity {

    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity1);

        button = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);

        /* 將MainActivity用intent傳來的字串取得,顯示在輸入框 */
        String text = getIntent().getStringExtra("TEXT");
        editText.setText(text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Activity1.this,MainActivity.class);
                startActivity(intent);
                Activity1.this.finish();
            }
        });
    }
}
