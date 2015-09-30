package com.example.henryho.mymusicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_playOrStop;
    private boolean isMusicPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void stopAudio() {
    }
}
