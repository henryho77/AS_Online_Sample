package com.example.henryho.mymusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_playOrStop;
    private boolean isMusicPlaying = false;
    private Intent serviceIntent;
    private String strAudioLink = "10.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(MainActivity.this, MyPlayService.class);
        initViews();
        setListeners();
    }

    private void initViews() {
        btn_playOrStop = (Button) findViewById(R.id.btn_playOrStop);
        btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
    }

    private void setListeners() {
        btn_playOrStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMusicPlaying) {
                    isMusicPlaying = true;
                    playAudio();
                    btn_playOrStop.setBackgroundResource(R.drawable.button_stop_icon);
                } else if (isMusicPlaying) {
                    isMusicPlaying = false;
                    stopAudio();
                    btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
                }
            }
        });
    }

    private void playAudio() {
        stopAudio();//播放之前先停止之前的服務
        serviceIntent.putExtra("sentAudioLink", strAudioLink);//serviceIntent放入曲目名稱
        startService(serviceIntent);//啟動服務
    }

    private void stopAudio() {
        stopService(serviceIntent);//停止服務
    }
}
