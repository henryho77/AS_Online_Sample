package com.example.henryho.mymusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MyPlayService extends Service implements MediaPlayer.OnBufferingUpdateListener,
                                                      MediaPlayer.OnCompletionListener,
                                                      MediaPlayer.OnErrorListener,
                                                      MediaPlayer.OnInfoListener,
                                                      MediaPlayer.OnPreparedListener,
                                                      MediaPlayer.OnSeekCompleteListener {

    MediaPlayer mediaPlayer = new MediaPlayer();
    private String sntAudioLink;
    private static final int NOTIFICATION_ID = 1;//2.Set up the notification ID
    //4.判斷電話是否進來的參數
    private static final String TAG = "TEL_SERVICE";
    private boolean isPauseInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    //5.Set up broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "com.example.henryho.mymusicplayer.broadcastbuffer";
    Intent bufferIntent;

    private int headsetSwitch = 1;//6.Declare headsetSwitch variable

    //7.Variables for seekbar processing
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition;
    int mediaMax;
    Intent seekIntent;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "com.example.henryho.mymusicplayer.seekprogress";

    @Override
    public void onCreate() {
        super.onCreate();

        bufferIntent = new Intent(BROADCAST_BUFFER);//5.Instantiate bufferIntent to communicate with Activity for progress dialogue
        seekIntent = new Intent(BROADCAST_ACTION);//7.Set up intent for seekbar broadcast

        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);//7.
        mediaPlayer.reset();

        //6.Register headset receiver
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        //8.Set up receiver for seekbar change
        registerReceiver(broadcastReceiver, new IntentFilter(MainActivity.BROADCAST_SEEKBAR));

        /*4.電話打進來時暫停音樂,掛斷時接續播放
         *Manage incoming phone calls during playback.
		 *Pause MediaPlayer on incoming,
		 *resume on hangup. */
        Log.v(TAG, "Starting telephony");
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Log.v(TAG, "Starting listener");
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.v(TAG, "Starting CallStateChange");
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPauseInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        //Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (isPauseInCall) {
                                isPauseInCall = false;
                                playMedia();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        //4.Register the listener with the telephony manager
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);


        /*2.Insert notification start */
        initNotification();

        sntAudioLink = intent.getExtras().getString("sentAudioLink");//1.
        mediaPlayer.reset();//1.
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource("http://licensing.glowingpigs.com/Audio/" + sntAudioLink);//1.Set up the MediaPlayer data source using the strAudioLink value
                sendBufferingBroadcast();//5.Send message to Activity to display progress dialogue
                mediaPlayer.prepareAsync();//1.Prepare mediaPlayer

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
            }
        }

        //7.Set up seekbar handler
        setupHandler();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //1.
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        //2.Cancel the notification
        cancelNotification();

        //4.
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        //6.Unregister headsetReceiver
        unregisterReceiver(headsetReceiver);

        //7.Stop the seekbar handler from sending updates to UI
        handler.removeCallbacks(sendUpdatesToUI);

        //8.Unregister seekbar receiver
        unregisterReceiver(broadcastReceiver);

        //6.Service ends, need to tell activity to display "Play" button
        resetButtonPlayOrStopBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*===============================================================================*/
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();//1.
        stopSelf();//1.
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //1.
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "Media error not valid for progressive playback " + extra, Toast.LENGTH_SHORT).show();
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "Media error server died " + extra, Toast.LENGTH_SHORT).show();
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "Media error unknown " + extra, Toast.LENGTH_SHORT).show();
                break;
        }

        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        sendBufferCompleteBroadcast();//4.Send a message to activity to end progress dialogue
        playMedia();//1.
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mediaPlayer.isPlaying()){
            playMedia();
        }
    }
    /*===============================================================================*/

    //1.
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //4.音樂暫停功能 Add for Telephony Manager
    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //1.
    private void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }


    //2.Create notification
    private void initNotification() {
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder notiBuilder = new Notification.Builder(this);
        notiBuilder.setSmallIcon(R.mipmap.ic_launcher);//推播的圖案
        notiBuilder.setContentTitle("Music In Service App Tutorial");//推播下拉時所顯示的標題
        notiBuilder.setContentText("Listen To Music While Performing Other Tasks");//推播下拉時所顯示的內文
        notiBuilder.setTicker("Tutorial: Music In Service");//推播剛跳出時,會有一小段時間可以顯示即時的文字,就是用setTicker做的
        notiBuilder.setWhen(System.currentTimeMillis());//推播下拉時,右邊會顯示該推播的時間點,就是用setWhen做的
        notiBuilder.setAutoCancel(true);

        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, notiIntent, 0);
        notiBuilder.setContentIntent(pIntent);

        Notification noti = notiBuilder.build();
        notiMgr.notify(NOTIFICATION_ID, noti);

        /*早期的寫法*/
        //int icon = R.drawable.ic_launcher;
        //CharSequence tickerText = "Tutorial: Music In Service";
        //long when = System.currentTimeMillis();
        //Notification noti = new Notification(icon, tickerText, when);
        //noti.flags = Notification.FLAG_ONGOING_EVENT;
        //Context context = getApplicationContext();
        //CharSequence contentTitle = "Music In Service App Tutorial";
        //CharSequence contentText = "Listen To Music While Performing Other Tasks";
        //Intent notiIntent = new Intent(this, MyActivity.class);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
    }

    //2.Cancel notification
    private void cancelNotification() {
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiMgr.cancel(NOTIFICATION_ID);
    }

    //5.Send a message to Activity that audio is being prepared and buffering started
    private void sendBufferingBroadcast() {
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }
    //5.Send a message to Activity that audio is prepared and ready to start playing
    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }

    //6.Send a message to Activity to reset the play button.
    private void resetButtonPlayOrStopBroadcast() {
        // Log.v(TAG, "BufferCompleteSent");
        bufferIntent.putExtra("buffering", "2");
        sendBroadcast(bufferIntent);
    }

    //6.If headset gets unplugged, stop music and service.
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                    // Log.v(TAG, "State =  Headset disconnected");
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                    // Log.v(TAG, "State =  Headset connected");
                }
            }

            switch (headsetSwitch) {
                case 0:
                    headsetDisconnected();
                    break;
                case 1:
                    break;
            }
        }

    };

    //6.
    private void headsetDisconnected() {
        stopMedia();
        stopSelf();
    }

    //7.Send seekbar info to activity
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);//the method takes away,stops anything that the thread is going to be
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    //7.
    //特別注意,handler.postDelayed(Runnable r, long delayMillis)這種直接使用Runnable的方式,
    //並沒有開啟一個新的Thread,這個Runnable跟handler一樣都在MainThread(UiThread)跑,
    //如果你想要開一個新的Thread,應該要宣告一個class繼承Thread,然後在裡面複寫run()方法.
    private Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000); // 1 second
        }
    };

    //7.
    private void LogMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();

            mediaMax = mediaPlayer.getDuration();

            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    //8.Receive seekbar position if it has been changed by the user in the activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    //8.Update seek position from Activity
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }
    }
}
