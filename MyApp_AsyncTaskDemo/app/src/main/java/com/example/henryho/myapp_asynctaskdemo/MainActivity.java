package com.example.henryho.myapp_asynctaskdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private TextView textView;
    private Button btn_start;
    private Button btn_stop;
    private MyAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAsyncTask == null) {
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(10);
                } else if (myAsyncTask.isCancelled() || myAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute(10);
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAsyncTask != null) {
                    if (!myAsyncTask.isCancelled() && myAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                        myAsyncTask.cancel(true);
                    }
                }
            }
        });
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

        int count = 0;

        @Override
        protected String doInBackground(Integer... params) {
            try {
                for (int i = 0; i < params[0]; i++) {
                    Thread.sleep(500);
                    count = i + 1;
                    publishProgress(count);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "10!!!";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "開始計時...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            textView.setText("目前計到" + values[0] + "秒");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "接收到的完成參數為" + s + "秒,計時完成!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getApplicationContext(), "已停止,目前計到" + count + "秒", Toast.LENGTH_SHORT).show();
        }
    }

}
