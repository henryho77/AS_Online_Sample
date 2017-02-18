package com.example.henryho.multitest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class Activity_6 extends AppCompatActivity {

    private EditText edt_content;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6);
        setTitle("Activity_6");

        edt_content = (EditText)findViewById(R.id.edt_content);
        sp = getSharedPreferences("Data",MODE_PRIVATE);// 取得SharedPreferences物件
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = sp.getString("text", "未有任何儲存過的文字");// 取得偏好設定資料
        edt_content.setText(text);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sp.edit();// 取得Editor物件
        editor.putString("text",edt_content.getText().toString());// 存入偏好設定資料至Editor物件
        editor.apply();//寫入檔案
    }
}
