package com.example.henryho.mymusicplayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_playOrStop;
    private boolean isMusicPlaying = false;
    private Intent serviceIntent;
    private String strAudioLink = "10.mp3";

    private boolean isOnline;//3.判斷網路是否連線

    //5.Progress dialogue and broadcast receiver variables
    boolean mBufferBroadcastIsRegistered;
    private ProgressDialog pdBuff = null;

    //7.Seekbar variables
    private SeekBar seekBar;
    private int seekMax;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(MainActivity.this, MyPlayService.class);
        initViews();
        setListeners();
    }

    //5.onPause, unregister broadcast receiver. To improve, also save screen data
    @Override
    protected void onPause() {
        //5.Unregister broadcast receiver
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

        //7.Unregister seekbar broadcast receiver
        if (mBroadcastIsRegistered) {
            unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
        }

        super.onPause();
    }


    //5.onResume register broadcast receiver. To improve, retrieve saved screen data
    @Override
    protected void onResume() {
        //5.Register broadcast receiver
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(MyPlayService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }

        //7.Register seekbar broadcast receiver
        if (!mBroadcastIsRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(MyPlayService.BROADCAST_ACTION));
            mBroadcastIsRegistered = true;
        }

        super.onResume();
    }

    /*==============================================================================*/

    private void initViews() {
        btn_playOrStop = (Button) findViewById(R.id.btn_playOrStop);
        btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
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
        //3.確認網路是否連線
        checkConnectivity();
        if (isOnline) {
            //1.
            stopAudio();//播放之前先停止之前的服務
            serviceIntent.putExtra("sentAudioLink", strAudioLink);//serviceIntent放入曲目名稱
            startService(serviceIntent);//啟動服務

            //7.Register receiver for seekbar
            registerReceiver(broadcastReceiver, new IntentFilter(MyPlayService.BROADCAST_ACTION));
            mBroadcastIsRegistered = true;

        } else {
            //3.
            /*if network connection failed, show AlertDialog*/
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network not connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.setIcon(R.mipmap.ic_launcher);
            btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
            alertDialog.show();
        }
    }

    private void stopAudio() {

        //7.Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            unregisterReceiver(broadcastReceiver);
            mBroadcastIsRegistered = false;
        }

        stopService(serviceIntent);//1.停止服務
    }

    //3.確認網路是否連線
    private void checkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()
                || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
            isOnline = true;
        } else {
            isOnline = false;
        }

		/* this method need min api = 21 */
		/*Network[] networks = connMgr.getAllNetworks();
		Network network;
		NetworkInfo networkInfo;
		for (int i = 0; i < networks.length; i++) {
			network = networks[i];
			networkInfo = connMgr.getNetworkInfo(network);
			if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) &&
					(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) ) {
				isOnline = true;

			} else if ((networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) &&
					(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) ) {
				isOnline = true;

			} else {
				isOnline = false;
			}
		}*/
    }

    //5.Handle progress dialogue for buffering...
    private void showProgressDialog(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);

        switch (bufferIntValue) {
            case 0:
                // When the broadcasted "buffering" value is 0, dismiss the progress dialogue.
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;
            case 1:
                // When the broadcasted "buffering" value is 1, show "Buffering" progress dialogue.
                BufferDialogue();
                break;

            case 2:
                //6.
                // Listen for "2" to reset the button to a play button
                btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
                break;
        }
    }

    //5.Progress dialogue...
    private void BufferDialogue() {
        pdBuff = ProgressDialog.show(MainActivity.this, "Buffering...", "Acquiring song...", true);
    }

    //5.Set up broadcast receiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showProgressDialog(bufferIntent);
        }
    };

    //7.Broadcast Receiver to update position of seekbar from service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    //7.
    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        if (songEnded == 1) {
            //songEnded = 1 代表歌曲播完了
            btn_playOrStop.setBackgroundResource(R.drawable.button_play_icon);
        }
    }
}
