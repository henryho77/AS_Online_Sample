package com.example.henryho.myapp_multitest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Activity7 extends ActionBarActivity {

    private static final int NOTI_ID = 1;
    private Button btn_sendNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity7);

        btn_sendNotification = (Button) findViewById(R.id.btn_sendNotification);
        btn_sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// 取得NotificationManager系統服務
                NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(Activity7.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("收到一則推播")
                        .setContentText("點選開啟新的Activity");// 建立狀態列顯示的提醒訊息

                /* 上面這種不停點下去的寫法是簡寫,其實等同於下面這種寫法
                 * NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(Activity7.this);
                 * notiBuilder.setSmallIcon(R.mipmap.ic_launcher);
                 * notiBuilder.setContentTitle("收到一則推播");
                 * notiBuilder.setContentText("點選開啟新的Activity"); */

                Intent intent = new Intent(Activity7.this, Activity8.class);
                intent.putExtra("NOTIFICATION_ID", NOTI_ID);

                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(Activity7.this);// 建立TaskStackBuilder物件
                taskStackBuilder.addParentStack(Activity8.class);// 新增活動至返回堆疊
                taskStackBuilder.addNextIntent(intent);// 新增啟動活動的Intent物件

                PendingIntent pIntent = PendingIntent.getActivity(Activity7.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);// 建立PendingIntent物件
                //PendingIntent pIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);// 建立PendingIntent物件
                notiBuilder.setContentIntent(pIntent);// 指定PendingIntent


                Notification notification = notiBuilder.build();
                // 使用振動
                notification.vibrate= new long[] {100, 250, 100, 500};
                // 使用LED
                notification.ledARGB = Color.RED;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                notification.ledOnMS = 200;
                notification.ledOffMS = 300;

                notiMgr.notify(NOTI_ID, notiBuilder.build());// 送出提醒訊息
            }
        });
    }

}
