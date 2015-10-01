package com.example.henryho.mymusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
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


    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);


        /*2.Insert notification start */
        initNotification();

        sntAudioLink = intent.getExtras().getString("sentAudioLink");//1.
        mediaPlayer.reset();//1.
        if (!mediaPlayer.isPlaying()) {
            try {
                //1.Set up the MediaPlayer data source using the strAudioLink value
                mediaPlayer.setDataSource("http://licensing.glowingpigs.com/Audio/" + sntAudioLink);
                mediaPlayer.prepareAsync();//1.Prepare mediaPlayer

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
            }
        }
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
        playMedia();//1.
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
    /*===============================================================================*/

    private void playMedia() {
        //1.
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        //1.
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
}
