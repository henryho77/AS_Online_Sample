package com.example.henryho.myapp_multitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_ACT2 = 2;
    private ListView listView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("MainActivity");

        /* 建立ListView項目 */
        listView = (ListView) findViewById(R.id.listView);
        String item[] = new String[]{"1-startActivity",
                                     "2-startActivityForResult",
                                     "3-AsyncTask",
                                     "4-Thread,Handler",
                                     "5-Implicit Intent for ACTION",
                                     "6-SharedPreferences",
                                     "7-Service"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1, item);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent();
                switch (position) {
                    case 0://Activity1
                        intent.setClass(MainActivity.this, Activity1.class);
                        intent.putExtra("TEXT", "這是被傳過來的字串");
                        startActivity(intent);
                        break;
                    case 1://Activity2
                        intent.setClass(MainActivity.this, Activity2.class);
                        startActivityForResult(intent, REQUEST_CODE_ACT2);
                        break;
                    case 2://Activity3
                        intent.setClass(MainActivity.this, Activity3.class);
                        startActivity(intent);
                        break;
                    case 3://Activity4
                        intent.setClass(MainActivity.this, Activity4.class);
                        startActivity(intent);
                        break;
                    case 4://Activity5
                        intent.setClass(MainActivity.this, Activity5.class);
                        startActivity(intent);
                        break;
                    case 5://Activity6
                        intent.setClass(MainActivity.this, Activity6.class);
                        startActivity(intent);
                        break;
                    case 6://Activity7
                        intent.setClass(MainActivity.this, Activity7.class);
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
                Toast.makeText(getApplicationContext(),"Activity2 Back",Toast.LENGTH_SHORT).show();
                // 如果傳回的intent有夾帶訊息就顯示出來
                if (data != null) {
                    String msgFromInent = data.getStringExtra("MSG");
                    Toast.makeText(getApplicationContext(),msgFromInent,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);//很重要,把MsgQueue內所有Msg移到後面,讓下面finish優先被執行
            MainActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);//很重要,把MsgQueue內所有Msg移到後面,讓下面finish優先被執行
//        MainActivity.this.finish();
//    }
}
