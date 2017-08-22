package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.administrator.myironguard.Activity.Login_Activity;
import com.example.administrator.myironguard.R;

public class NotifyService extends IntentService {


    public NotifyService() {
        super("NotifyService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,
                new Intent(this, Login_Activity.class),0);
        Notification notification=new Notification.Builder(this)
                .setSmallIcon(R.mipmap.icon_dunpai)
                .setContentTitle("已为您加密新消息")
                .setContentText("点击查看")
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH).build();
        Log.d("TiemInfo","启动通知");
        notification.flags |=Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1,notification);
        notification.defaults=Notification.DEFAULT_ALL;
    }

}
