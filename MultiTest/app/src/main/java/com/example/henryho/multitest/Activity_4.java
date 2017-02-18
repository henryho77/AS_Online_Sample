package com.example.henryho.multitest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Activity_4 extends AppCompatActivity {

    boolean isRunning = false;
    private TextView txt_show2;
    private Button btn_start2;
    private Button btn_stop2;
    private Handler handler;
    private CountThread countThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);
        setTitle("Activity_4");

        txt_show2 = (TextView) findViewById(R.id.txt_showCount2);
        btn_start2 = (Button) findViewById(R.id.btn_start2);
        btn_stop2 = (Button) findViewById(R.id.btn_stop2);

        handler = new MyHandler();

        ButtonListener listener = new ButtonListener();
        btn_start2.setOnClickListener(listener);
        btn_stop2.setOnClickListener(listener);
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_start2:
                    if (countThread == null) {
                        countThread = new CountThread();
                        isRunning = true;
                        countThread.start();
                    } else if (!countThread.getState().equals(Thread.State.TIMED_WAITING)) {
                        countThread = new CountThread();
                        isRunning = true;
                        countThread.start();
                    }
                    break;
                case R.id.btn_stop2:
                    if (countThread != null) {
                        isRunning = false;
                        countThread.interrupt();
                    }
                    break;
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txt_show2.setText(Integer.toString(msg.getData().getInt("COUNT",0)));
        }
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
                        Message message = Message.obtain();
                        message.setData(bundle);
                        handler.sendMessage(message);
//                        Thread.sleep(500);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                txt_show2.setText("ya");
//                            }
//                        });
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
