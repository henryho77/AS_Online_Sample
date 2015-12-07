package com.example.henryho.myapp_handlerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    boolean isRunning = false;
    private TextView txt_show2;
    private Button btn_start2;
    private Button btn_stop2;
    private CountThread2 countThread2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txt_show2.setText(Integer.toString(msg.getData().getInt("count", 0)));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_show2 = (TextView) findViewById(R.id.txt_showCount2);
        btn_start2 = (Button) findViewById(R.id.btn_start2);
        btn_stop2 = (Button) findViewById(R.id.btn_stop2);

        btn_start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countThread2 == null) {
                    countThread2 = new CountThread2();
                    isRunning = true;
                    countThread2.start();
                } else if (!countThread2.getState().equals(Thread.State.TIMED_WAITING)) {
                    countThread2 = new CountThread2();
                    isRunning = true;
                    countThread2.start();
                }
            }
        });

        btn_stop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                countThread2.interrupt();
            }
        });
    }

    class CountThread2 extends Thread {
        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 10; i++) {
                try {
                    if (isRunning == true) {
                        Thread.sleep(500);
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", i + 1);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
