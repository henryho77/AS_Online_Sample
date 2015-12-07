package com.example.henryho.myapp_multitest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Activity4 extends Activity {

    boolean isRunning = false;
    private TextView txt_show2;
    private Button btn_start2;
    private Button btn_stop2;
    private CountThread countThread;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txt_show2.setText(Integer.toString(msg.getData().getInt("COUNT",0)));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity4);

        txt_show2 = (TextView) findViewById(R.id.txt_showCount2);
        btn_start2 = (Button) findViewById(R.id.btn_start2);
        btn_stop2 = (Button) findViewById(R.id.btn_stop2);

        btn_start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countThread == null) {
                    countThread = new CountThread();
                    isRunning = true;
                    countThread.start();
                } else if (!countThread.getState().equals(Thread.State.TIMED_WAITING)) {
                    countThread = new CountThread();
                    isRunning = true;
                    countThread.start();
                }
            }
        });

        btn_stop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countThread != null) {
                    isRunning = false;
                    countThread.interrupt();
                }
            }
        });
    }

    class CountThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                for (int i = 0; i < 10; i++) {
                    if (isRunning == true) {
                        Thread.sleep(500);
                        Bundle bundle = new Bundle();
                        bundle.putInt("COUNT",i + 1);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt_show2.setText("ya");
                            }
                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
