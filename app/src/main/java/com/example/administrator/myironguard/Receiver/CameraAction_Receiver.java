package com.example.administrator.myironguard.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.administrator.myironguard.Service.Bitmap_Service;

/**
 * Created by Administrator on 2016/11/26.
 */
public class CameraAction_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Intent in = new Intent(context, Bitmap_Service.class);
        context.startService(in);
        Log.d("TimeInfo","收到广播");
    }
}
