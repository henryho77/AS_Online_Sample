package com.example.henryho.multitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACT2 = 2;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MainActivity");

        /* 建立ListView項目 */
        listView = (ListView) findViewById(R.id.listView);
        String item[] = new String[] {"1-startActivity",
                                        "2-startActivityForResult",
                                        "3-AsyncTask",
                                        "4-Thread,Handler",
                                        "5-Implicit Intent for ACTION",
                                        "6-SharedPreferences",
                                        "7-Service" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (
                getApplicationContext(), android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();

                switch (position) {
                    case 0://Activity1
                        intent.setClass(MainActivity.this, Activity_1.class);
                        intent.putExtra("TEXT","被傳過來的字串");
                        startActivity(intent);
                        break;
                    case 1://Activity2
                        intent.setClass(MainActivity.this, Activity_2.class);
                        startActivityForResult(intent, REQUEST_CODE_ACT2);
                        break;
                    case 2://Activity3
                        intent.setClass(MainActivity.this, Activity_3.class);
                        startActivity(intent);
                        break;
                    case 3://Activity4
                        intent.setClass(MainActivity.this, Activity_4.class);
                        startActivity(intent);
                        break;
                    case 4://Activity5
                        intent.setClass(MainActivity.this, Activity_5.class);
                        startActivity(intent);
                        break;
                    case 5://Activity6
                        intent.setClass(MainActivity.this, Activity_6.class);
                        startActivity(intent);
                        break;
                    case 6://Activity7
                        intent.setClass(MainActivity.this, Activity_7.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_ACT2:
                Toast.makeText(getApplicationContext(),"Activity_2 requestCode Back", Toast.LENGTH_SHORT).show();
                if (data != null) {
                    String msgFromInent = data.getStringExtra("MSG");
                    Toast.makeText(getApplicationContext(),msgFromInent,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
