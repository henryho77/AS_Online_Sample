package com.example.henryho.myapp_multitest;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class Activity6 extends ActionBarActivity {

    private EditText edt_content;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity6);

        edt_content = (EditText)findViewById(R.id.edt_content);
        sp = getPreferences(MODE_PRIVATE);// 取得SharedPreferences物件
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = sp.getString("TEXT", "未有任何儲存過的文字");// 取得偏好設定資料
        edt_content.setText(text);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor spEditor = sp.edit();// 取得Editor物件
        spEditor.putString("TEXT",edt_content.getText().toString());// 存入偏好設定資料至Editor物件
        spEditor.apply();//寫入檔案
    }
}
