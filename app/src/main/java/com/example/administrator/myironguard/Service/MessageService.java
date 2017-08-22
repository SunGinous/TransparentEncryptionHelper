package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.Db.MessageInfo;

public class MessageService extends IntentService {
    private String data;
    private Uri SMS_URI=Uri.parse("content://sms/inbox");
    private Sms sms=new Sms();
    public MessageService() {
        super("MessageService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("TimeInfo","服务启动");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        data=intent.getStringExtra("number");
        getMessage();
    }

    public void getMessage(){
        String fullnumber="+86"+data;
        ContentResolver contentResolver=this.getContentResolver();
        String[] message=new String[]{"_id","address","person","body","date","type"};
        String where="address='"+data+"'";
        String fullwhere="address='"+fullnumber+"'";
        Cursor cursor=contentResolver.query(SMS_URI,message,where,null,null);
        Cursor fullcursor=contentResolver.query(SMS_URI,message,fullwhere,null,null);
        Log.d("TimeInfo","cursor="+cursor);
        if (cursor==null){
            return;
        }
        try{
            while (cursor.moveToNext()){
                Log.d("TimeInfo","我是可以执行的1");
                String id=cursor.getString(cursor.getColumnIndex("_id"));
                String name=cursor.getString(cursor.getColumnIndex("person"));
                String number=cursor.getString(cursor.getColumnIndex("address"));
                String content=cursor.getString(cursor.getColumnIndex("body"));
                String date=cursor.getString(cursor.getColumnIndex("date"));
                int  type=cursor.getInt(cursor.getColumnIndex("type"));

                MessageInfo messageInfo=new MessageInfo(getApplicationContext());
                sms.id=id;
                sms.name=name;
                sms.number=number;
                sms.content=content;
                sms.date=date;
                sms.type=type;
                messageInfo.save(sms);
                Log.d("TimeInfo","我是可以执行的2");
            }
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"未获取到短信内容",Toast.LENGTH_SHORT).show();
        }

        if (fullcursor==null){
            return;
        }
        try {
            while (fullcursor.moveToNext()){
                Log.d("TimeInfo","我是可以执行的3");
                String id=fullcursor.getString(fullcursor.getColumnIndex("_id"));
                String name=fullcursor.getString(fullcursor.getColumnIndex("person"));
                String number=fullcursor.getString(fullcursor.getColumnIndex("address"));
                String content=fullcursor.getString(fullcursor.getColumnIndex("body"));
                String date=fullcursor.getString(fullcursor.getColumnIndex("date"));
                int  type=fullcursor.getInt(fullcursor.getColumnIndex("type"));

                MessageInfo messageInfo=new MessageInfo(getApplicationContext());
                sms.id=id;
                sms.name=name;
                sms.number=number.substring(3);
                sms.content=content;
                sms.date=date;
                sms.type=type;
                messageInfo.save(sms);
                Log.d("TimeInfo","我是可以执行的4");
            }
            fullcursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"未获取到短信内容",Toast.LENGTH_SHORT).show();
        }

    }
}
