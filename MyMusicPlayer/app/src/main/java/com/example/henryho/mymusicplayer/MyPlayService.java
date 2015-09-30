package com.example.henryho.mymusicplayer;

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
}
