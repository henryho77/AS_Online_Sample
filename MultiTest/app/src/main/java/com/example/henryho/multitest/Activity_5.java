package com.example.henryho.multitest;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Activity_5 extends AppCompatActivity implements View.OnClickListener {

    private static final int GET_CONTACT = 1;
    private Button btn_browser;
    private Button btn_map;
    private Button btn_call;
    private Button btn_contacts;
    private Button btn_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5);

        btn_browser = (Button) findViewById(R.id.btn_browser);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_call = (Button) findViewById(R.id.btn_call);
        btn_contacts = (Button) findViewById(R.id.btn_contacts);
        btn_mail = (Button) findViewById(R.id.btn_mail);

        btn_browser.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_call.setOnClickListener(this);
        btn_contacts.setOnClickListener(this);
        btn_mail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btn_browser:
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW).setData(Uri.parse("http://www.google.com.tw"));
                //intent.setData(Uri.parse("http://www.google.com.tw"));
                /* 上面三行可以改寫成建構子的方式用一行表達:
                 * Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com.tw")) */
                startActivity(intent);
                break;
            case R.id.btn_map:
                intent = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:25.047245, 121.517060"));
                startActivity(intent);
                break;
            case R.id.btn_call:
                intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:+886222258859"));
                startActivity(intent);
                break;
            case R.id.btn_contacts:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(intent,GET_CONTACT);
                break;
            case R.id.btn_mail:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:henry.ho@autolutiontech.com"));
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CONTACT) {
            if (resultCode == RESULT_OK) {
                String uri = data.getData().toString();
                Toast.makeText(this,uri, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
                startActivity(intent);
            }
        }
    }
}
