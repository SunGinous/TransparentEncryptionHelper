package com.example.administrator.myironguard.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.Db.MessageInfo;
import com.example.administrator.myironguard.Service.DeleteMsgService;
import com.example.administrator.myironguard.Service.NotifyService;
import com.example.administrator.myironguard.Utils.SuUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sunginous on 2017/2/22.
 */

public class SMSReceiver extends BroadcastReceiver {
    private Sms sms=new Sms();
    private SmsMessage smsMessage=null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            Object[] pdusData=(Object[]) bundle.get("pdus");
            Log.d("TimeInfo","pudsData="+pdusData);
            for (Object object:pdusData){
                smsMessage=SmsMessage.createFromPdu((byte[]) object);
                String number=smsMessage.getOriginatingAddress();
                Log.d("TimeInfo","短信号码为"+number);
                String content=smsMessage.getMessageBody();
                Log.d("TimeInfo","短信内容为"+content);
                MessageInfo messageInfo=new MessageInfo(context);

                if (number.charAt(0)=='+'){
                    String numberCN=number.substring(3);
                    if (messageInfo.checkExist(numberCN)){
                        sms.number = numberCN;
                        sms.content = content;
                        sms.type=1;
                        long time = System.currentTimeMillis();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
                        Date date=new Date(time);
                        sms.date=format.format(date);
                        Log.d("TimeInfo",sms.date);
                        messageInfo.save(sms);
                        Log.d("TimeInfo","来信号码numberCN为"+numberCN);
                        Intent notifyIntent = new Intent(context, NotifyService.class);
                        Intent deleteIntent = new Intent(context, DeleteMsgService.class);
                        deleteIntent.putExtra("number",numberCN);
                        context.startService(notifyIntent);
                        Log.d("TimeInfo","启动通知");
                        context.startService(deleteIntent);
                        Log.d("TimeInfo","启动删除服务");
                    }
                }else if (messageInfo.checkExist(number)){
                    sms.number=number;
                    sms.content=content;
                    sms.type=1;
                    messageInfo.save(sms);
                    Log.d("TimeInfo","来信号码number为"+number);
                    Intent notifyIntent = new Intent(context, NotifyService.class);
                    Intent deleteIntent = new Intent(context, DeleteMsgService.class);
                    deleteIntent.putExtra("number",number);
                    context.startService(notifyIntent);
                    Log.d("TimeInfo","启动通知");
                    context.startService(deleteIntent);
                }
            }
        }
    }
}
